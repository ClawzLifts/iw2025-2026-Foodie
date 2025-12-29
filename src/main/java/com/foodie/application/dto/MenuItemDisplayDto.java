package com.foodie.application.dto;

import com.foodie.application.domain.MenuItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for displaying menu items with pricing and display information.
 * Combines product information with menu-specific details like discounts and featured status.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuItemDisplayDto {
    private Integer productId;
    private String name;
    private String description;
    private Double originalPrice;
    private Double discountedPrice;
    private Integer discountPercentage;
    private Boolean featured;
    private String imageUrl;

    /**
     * Creates a MenuItemDisplayDto from a MenuItem entity.
     * Calculates the discounted price based on the discount percentage.
     *
     * @param menuItem the MenuItem entity
     * @return MenuItemDisplayDto with all necessary display information
     */
    public static MenuItemDisplayDto fromMenuItem(MenuItem menuItem) {
        Double originalPrice = menuItem.getProduct().getPrice();
        Double discountedPrice = originalPrice;

        if (menuItem.getDiscountPercentage() != null && menuItem.getDiscountPercentage() > 0) {
            discountedPrice = originalPrice * (1 - (menuItem.getDiscountPercentage() / 100.0));
        }

        return MenuItemDisplayDto.builder()
                .productId(menuItem.getProduct().getId())
                .name(menuItem.getProduct().getName())
                .description(menuItem.getProduct().getDescription())
                .originalPrice(originalPrice)
                .discountedPrice(discountedPrice)
                .discountPercentage(menuItem.getDiscountPercentage())
                .featured(menuItem.getFeatured())
                .imageUrl(menuItem.getProduct().getImageUrl())
                .build();
    }
}

