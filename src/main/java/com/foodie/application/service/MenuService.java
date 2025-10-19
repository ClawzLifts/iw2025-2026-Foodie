package com.foodie.application.service;

import com.foodie.application.domain.Menu;
import com.foodie.application.domain.MenuItem;
import com.foodie.application.dto.MenuItemDto;
import com.foodie.application.repository.MenuRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MenuService {

    private final MenuRepository menuRepository;
    private final MenuItemService menuItemService;

    public MenuService(MenuRepository menuRepository, MenuItemService menuItemService) {
        this.menuRepository = menuRepository;
        this.menuItemService = menuItemService;
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
