package com.foodie.application.domain;

import jakarta.persistence.Entity;
import lombok.*;

/**
 * Represents a product item within an order with its quantity and pricing information.
 * This entity is used to store product details as they appear in a specific order,
 * including historical snapshots of product names and prices.
 *
 * This class is typically stored as JSON within an OrderEntity to represent
 * the list of products and their quantities in a single order.
 *
 * @author Jesus Rodriguez
 * @version 1.0
 * @since 2024
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductList {

    private Integer productId;
    private String productName;
    private Double price;

    private Integer quantity;

}
