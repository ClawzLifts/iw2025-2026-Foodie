package com.foodie.application.service;

import com.foodie.application.domain.Menu;
import com.foodie.application.dto.MenuDto;
import com.foodie.application.dto.ProductDto;
import com.foodie.application.repository.MenuRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
        List<MenuDto> menus = new ArrayList<>();
        menuRepository.findAll().forEach(menu -> menus.add(menu.toDto()));
        return menus;
    }

    @Transactional
    public List<ProductDto> getProducts(Integer menuId) {
        Optional<Menu> optMenu = menuRepository.findById(menuId);
        List<ProductDto> products = new ArrayList<>();
        optMenu.ifPresent(
                menu -> menu.getMenuItems().forEach(item -> products.add(item.getProduct().toDto())));
        return products;
    }

    @Transactional
    public Integer deleteMenu(Integer menuId) {
        Menu menu = menuRepository.findById(menuId).orElseThrow();
        menuRepository.delete(menu);
        return menuId;
    }

    public List<Menu> getAllMenus() {
        return menuRepository.findAll();
    }

    @Transactional
    public List<Menu> getAllMenusWithItems() {
        List<Menu> menus = menuRepository.findAll();
        menus.forEach(menu -> {
            if (menu.getMenuItems() != null) {
                menu.getMenuItems().size();
            }
        });
        return menus;
    }

    @Transactional
    public void addProductToMenu(Integer menuId, Integer productId) {
        Menu menu = menuRepository.findById(menuId).orElseThrow(() -> new IllegalArgumentException("Menu no encontrado con ID: " + menuId));
        menuItemService.addMenuItem(productId, menu, false, 0);
    }

    @Transactional
    public void deleteMenuItem(Integer menuItemId) {
        menuItemService.deleteById(menuItemId);
    }
}
