package com.foodie.application.service;

import com.foodie.application.domain.Menu;
import com.foodie.application.domain.MenuItem;
import com.foodie.application.repository.MenuItemRepository;
import com.foodie.application.repository.MenuRepository;
import com.foodie.application.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MenuItemService {
    private final MenuItemRepository menuItemRepository;
    private final MenuRepository menuRepository;
    private final ProductRepository productRepository;

    public MenuItemService(MenuItemRepository menuItemRepository, MenuRepository menuRepository, ProductRepository productRepository) {
        this.menuItemRepository = menuItemRepository;
        this.menuRepository = menuRepository;
        this.productRepository = productRepository;
    }

    /**
     * Adds a menu item to a menu using menu ID (for UI layer).
     *
     * @param productId the ID of the product
     * @param menuId the ID of the menu
     * @param featured whether the item is featured
     * @param discountPercentage the discount percentage
     * @return the created MenuItem
     */
    @Transactional
    public MenuItem addMenuItem(Integer productId, Integer menuId, Boolean featured, Integer discountPercentage) {
        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new EntityNotFoundException("Menu not found with id: " + menuId));
        return addMenuItem(productId, menu, featured, discountPercentage);
    }

    /**
     * Adds a menu item to a menu using Menu entity (for service layer).
     *
     * @param productId the ID of the product
     * @param menu the Menu entity
     * @param featured whether the item is featured
     * @param discountPercentage the discount percentage
     * @return the created MenuItem
     */
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

    public List<MenuItem> getMenuItems(Integer menuId) {
        return menuItemRepository.findByMenuId(menuId);
    }

    @Transactional
    public void deleteMenuItem(Integer menuId, Integer productId) {
        List<MenuItem> menuItems = menuItemRepository.findByMenuId(menuId);
        menuItems.stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .ifPresent(menuItemRepository::delete);
    }

    /**
     * Updates a menu item's featured status and discount percentage.
     *
     * @param menuItemId the ID of the menu item to update
     * @param featured whether the item should be marked as featured
     * @param discountPercentage the discount percentage to apply (0-100)
     */
    @Transactional
    public void updateMenuItem(Integer menuItemId, Boolean featured, Integer discountPercentage) {
        if (menuItemRepository == null) {
            throw new IllegalStateException("MenuItemRepository is not initialized");
        }

        MenuItem menuItem = menuItemRepository.findById(menuItemId)
                .orElseThrow(() -> new IllegalArgumentException("MenuItem not found with id: " + menuItemId));
        menuItem.setFeatured(featured);
        menuItem.setDiscountPercentage(discountPercentage);
        menuItemRepository.save(menuItem);
    }

    /**
     * Updates a menu item by menu ID and product ID.
     * This is an alternative method to update a menu item using menu and product identifiers.
     *
     * @param menuId the ID of the menu
     * @param productId the ID of the product
     * @param featured whether the item should be marked as featured
     * @param discountPercentage the discount percentage to apply (0-100)
     */
    @Transactional
    public void updateMenuItemByMenuAndProduct(Integer menuId, Integer productId, Boolean featured, Integer discountPercentage) {
        List<MenuItem> menuItems = menuItemRepository.findByMenuId(menuId);
        menuItems.stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .ifPresent(item -> {
                    item.setFeatured(featured);
                    item.setDiscountPercentage(discountPercentage);
                    menuItemRepository.save(item);
                });
    }

}
