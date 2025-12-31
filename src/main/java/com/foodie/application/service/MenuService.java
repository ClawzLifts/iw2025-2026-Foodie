package com.foodie.application.service;

import com.foodie.application.domain.Menu;
import com.foodie.application.domain.MenuItem;
import com.foodie.application.dto.MenuDto;
import com.foodie.application.dto.MenuItemDto;
import com.foodie.application.dto.MenuItemDisplayDto;
import com.foodie.application.dto.ProductDto;
import com.foodie.application.repository.MenuRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MenuService {

    private final MenuRepository menuRepository;
    private final MenuItemService menuItemService;

    public MenuService(MenuRepository menuRepository, MenuItemService menuItemService) {
        this.menuRepository = menuRepository;
        this.menuItemService = menuItemService;
    }

    @Transactional
    public List<MenuDto> getMenus(){
        return menuRepository.findAll().stream()
                .map(MenuDto::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<ProductDto> getProducts(Integer menuId) {
        Optional<Menu> optMenu = menuRepository.findById(menuId);
        return optMenu.map(menu -> menu.getMenuItems().stream()
                .map(item -> ProductDto.fromProduct(item.getProduct()))
                .collect(Collectors.toList()))
                .orElse(new ArrayList<>());
    }

    /**
     * Gets menu items with complete display information including product details,
     * ingredients, allergens, and discount information.
     * This method is specifically designed for the frontend to display products.
     *
     * @param menuId the ID of the menu
     * @return list of MenuItemDisplayDto with all display information
     */
    @Transactional
    public List<MenuItemDisplayDto> getMenuItemsForDisplay(Integer menuId) {
        Optional<Menu> optMenu = menuRepository.findById(menuId);
        return optMenu.map(menu -> menu.getMenuItems().stream()
                .map(MenuItemDisplayDto::fromMenuItem)
                .collect(Collectors.toList()))
                .orElse(new ArrayList<>());
    }

    @Transactional
    public Integer addMenu(String name, List<MenuItemDto> items) {
        Menu menu = new Menu();
        menu.setName(name);

        Menu savedMenu = menuRepository.save(menu);
        List<MenuItem> menuItems = items.stream().map(itemDto ->
                menuItemService.addMenuItem(
                        itemDto.getProductId(),
                        savedMenu,
                        itemDto.getFeatured(),
                        itemDto.getDiscountPercentage()
                )
        ).toList();

        savedMenu.setMenuItems(menuItems);
        return savedMenu.getId();
    }

    @Transactional
    public Integer deleteMenu(Integer menuId) {
        Menu menu = menuRepository.findById(menuId).orElseThrow();
        menuRepository.delete(menu);
        return menuId;
    }

    @Transactional
    public void updateMenuName(Integer menuId, String newName) {
        Menu menu = menuRepository.findById(menuId).orElseThrow();
        menu.setName(newName);
    }
}
