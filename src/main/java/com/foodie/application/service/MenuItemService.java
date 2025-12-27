package com.foodie.application.service;

import com.foodie.application.domain.Menu;
import com.foodie.application.domain.MenuItem;
import com.foodie.application.repository.MenuItemRepository;
import com.foodie.application.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MenuItemService {
    private final MenuItemRepository menuItemRepository;
    private final ProductRepository productRepository;

    public MenuItemService(MenuItemRepository menuItemRepository, ProductRepository productRepository) {
        this.menuItemRepository = menuItemRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public MenuItem addMenuItem(Integer productId, Menu menu, Boolean featured, Integer discountPercentage) {
        MenuItem menuItem = MenuItem.builder()
                .product(productRepository.findById(productId).orElseThrow())
                .menu(menu)
                .featured(featured)
                .discountPercentage(discountPercentage)
                .build();
        return menuItemRepository.save(menuItem);
    }

    @Transactional
    public void deleteById(Integer menuItemId) {
        menuItemRepository.deleteMenuItemById(menuItemId);
    }

    public java.util.List<MenuItem> getMenuItems(Integer menuId) {
        return menuItemRepository.findByMenuId(menuId);
    }

}
