package com.foodie.application.dto;

import com.foodie.application.domain.MenuItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * DTO for displaying menu item information in the UI.
 * Transfers menu item data from service to presentation layer without exposing domain model.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuItemDto implements Serializable {
    private Integer productId;
    private String productName;
    private Double productPrice;
    private String description;
    private String imageUrl;
    private Boolean featured;
    private Integer discountPercentage;

    /**
     * Converts a MenuItem entity to MenuItemDisplayDto
     */
    public static MenuItemDto fromMenuItem(MenuItem menuItem) {
        return MenuItemDto.builder()
                .productId(menuItem.getProduct().getId())
                .productName(menuItem.getProduct().getName())
                .productPrice(menuItem.getProduct().getPrice())
                .description(menuItem.getProduct().getDescription())
                .imageUrl(menuItem.getProduct().getImageUrl())
                .featured(menuItem.getFeatured())
                .discountPercentage(menuItem.getDiscountPercentage())
                .build();
    }

    /**
     * Gets the original price (without discount)
     */
    public Double getOriginalPrice() {
        return productPrice;
    }

    /**
     * Calculates the discounted price based on discount percentage
     */
    public Double getDiscountedPrice() {
        if (discountPercentage == null || discountPercentage <= 0) {
            return productPrice;
        }
        return productPrice * (1 - discountPercentage / 100.0);
    }
}

