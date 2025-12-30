package com.foodie.application.dto;

import com.foodie.application.domain.MenuItem;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.io.Serializable;
import java.util.Set;

/**
 * DTO for displaying menu items with complete product information including ingredients and allergens
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuItemDisplayDto implements Serializable {
    private Integer id;
    private Integer productId;
    private Integer menuId;
    private Boolean featured;
    private Integer discountPercentage;

    // Product information
    private String productName;
    private String description;
    private Double originalPrice;
    private Double discountedPrice;
    private String imageUrl;

    // Product details
    private Set<String> ingredients;
    private Set<String> allergenNames;

    /**
     * Converts a MenuItem entity with all product details to MenuItemDisplayDto
     */
    public static MenuItemDisplayDto fromMenuItem(MenuItem menuItem) {
        ProductDto productDto = ProductDto.fromProduct(menuItem.getProduct());

        Double discountedPrice = menuItem.getDiscountPercentage() != null && menuItem.getDiscountPercentage() > 0
                ? menuItem.getProduct().getPrice() * (1 - menuItem.getDiscountPercentage() / 100.0)
                : menuItem.getProduct().getPrice();

        return MenuItemDisplayDto.builder()
                .id(menuItem.getId())
                .productId(menuItem.getProduct().getId())
                .menuId(menuItem.getMenu().getId())
                .featured(menuItem.getFeatured())
                .discountPercentage(menuItem.getDiscountPercentage())
                .productName(productDto.getName())
                .description(productDto.getDescription())
                .originalPrice(menuItem.getProduct().getPrice())
                .discountedPrice(discountedPrice)
                .imageUrl(productDto.getImageUrl())
                .ingredients(productDto.getIngredients())
                .allergenNames(productDto.getAllergenNames())
                .build();
    }
}

