package com.foodie.application.helper;

import com.foodie.application.order.OrderEntity;
import com.foodie.application.order.ProductListEntity;
import lombok.experimental.UtilityClass;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Utility class providing helper methods for order management operations.
 * This class contains pure business logic for handling order items and calculations.
 *
 * @author Jesus Rodriguez
 * @version 1.0
 * @since 2025
 */
@UtilityClass
public class OrderHelper {

    /**
     * Adds a product item to the order or updates its quantity if it already exists.
     * If the product is already in the order, it will be replaced with the new quantity.
     *
     * @param order the order entity to modify
     * @param productId the unique identifier of the product
     * @param productName the name of the product (historical snapshot)
     * @param price the unit price of the product (historical snapshot)
     * @param quantity the quantity to add or update
     * @throws NullPointerException if order is null
     */
    public static void addItem(OrderEntity order, Integer productId, String productName, Double price, Integer quantity) {
        if (order == null) {
            throw new IllegalArgumentException("Order cannot be null");
        }

        // Initialize items list if null
        if (order.getItems() == null) {
            order.setItems(new java.util.ArrayList<>());
        }

        // Remove existing item with same productId
        order.getItems().removeIf(item -> item.getProductId().equals(productId));

        // Create and add new item
        ProductListEntity newItem = ProductListEntity.builder()
                .productId(productId)
                .productName(productName)
                .price(price)
                .quantity(quantity)
                .build();

        order.getItems().add(newItem);
    }

    /**
     * Adds a complete ProductListEntity item to the order.
     * If an item with the same productId already exists, it will be replaced.
     *
     * @param order the order entity to modify
     * @param item the product item to add or update
     * @throws NullPointerException if order or item is null
     * @throws IllegalArgumentException if item's productId is null
     */
    public static void addItem(OrderEntity order, ProductListEntity item) {
        if (order == null) {
            throw new IllegalArgumentException("Order cannot be null");
        }
        if (item == null) {
            throw new IllegalArgumentException("Item cannot be null");
        }
        if (item.getProductId() == null) {
            throw new IllegalArgumentException("Product ID cannot be null");
        }

        if (order.getItems() == null) {
            order.setItems(new java.util.ArrayList<>());
        }

        order.getItems().removeIf(i -> item.getProductId().equals(i.getProductId()));
        order.getItems().add(item);
    }

    /**
     * Calculates the total monetary value of all items in the order.
     * The total is computed as the sum of (price × quantity) for all items.
     *
     * @param order the order entity to calculate total for
     * @return the total amount as Double, 0.0 if order has no items or is null
     */
    public static Double calculateTotal(OrderEntity order) {
        if (order == null || order.getItems() == null || order.getItems().isEmpty()) {
            return 0.0;
        }

        return order.getItems().stream()
                .mapToDouble(item -> {
                    if (item.getPrice() == null || item.getQuantity() == null) {
                        return 0.0;
                    }
                    return item.getPrice() * item.getQuantity();
                })
                .sum();
    }

    /**
     * Calculates the total number of items (sum of quantities) in the order.
     * This represents the total number of individual products, not the number of line items.
     *
     * @param order the order entity to calculate total items for
     * @return the total number of items as Integer, 0 if order has no items or is null
     */
    public static Integer calculateTotalItems(OrderEntity order) {
        if (order == null || order.getItems() == null || order.getItems().isEmpty()) {
            return 0;
        }

        return order.getItems().stream()
                .mapToInt(item -> item.getQuantity() != null ? item.getQuantity() : 0)
                .sum();
    }

    /**
     * Creates a map representation of product IDs to their quantities in the order.
     * Useful for quick lookups and quantity management operations.
     *
     * @param order the order entity to extract quantities from
     * @return a Map where keys are product IDs and values are quantities,
     *         empty map if order has no items or is null
     */
    public static Map<Integer, Integer> getProductQuantitiesMap(OrderEntity order) {
        if (order == null || order.getItems() == null || order.getItems().isEmpty()) {
            return Map.of();
        }

        return order.getItems().stream()
                .filter(item -> item.getProductId() != null)
                .collect(Collectors.toMap(
                        ProductListEntity::getProductId,
                        item -> item.getQuantity() != null ? item.getQuantity() : 0
                ));
    }

    /**
     * Checks if the order contains a specific product.
     *
     * @param order the order entity to check
     * @param productId the product ID to search for
     * @return true if the product is found in the order, false otherwise
     * @throws IllegalArgumentException if productId is null
     */
    public static boolean containsProduct(OrderEntity order, Integer productId) {
        if (productId == null) {
            throw new IllegalArgumentException("Product ID cannot be null");
        }
        if (order == null || order.getItems() == null || order.getItems().isEmpty()) {
            return false;
        }

        return order.getItems().stream()
                .anyMatch(item -> productId.equals(item.getProductId()));
    }

    /**
     * Updates the quantity of a specific product in the order.
     * If the product is not found in the order, no action is taken.
     *
     * @param order the order entity to modify
     * @param productId the product ID to update
     * @param newQuantity the new quantity to set
     * @return true if the product was found and updated, false otherwise
     * @throws IllegalArgumentException if productId is null or newQuantity is negative
     */
    public static boolean updateProductQuantity(OrderEntity order, Integer productId, Integer newQuantity) {
        if (productId == null) {
            throw new IllegalArgumentException("Product ID cannot be null");
        }
        if (newQuantity != null && newQuantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
        if (order == null || order.getItems() == null) {
            return false;
        }

        return order.getItems().stream()
                .filter(item -> productId.equals(item.getProductId()))
                .findFirst()
                .map(item -> {
                    item.setQuantity(newQuantity);
                    return true;
                })
                .orElse(false);
    }

    /**
     * Removes a specific product from the order.
     * If the product is not found in the order, no action is taken.
     *
     * @param order the order entity to modify
     * @param productId the product ID to remove
     * @return true if the product was found and removed, false otherwise
     * @throws IllegalArgumentException if productId is null
     */
    public static boolean removeProduct(OrderEntity order, Integer productId) {
        if (productId == null) {
            throw new IllegalArgumentException("Product ID cannot be null");
        }
        if (order == null || order.getItems() == null) {
            return false;
        }

        boolean removed = order.getItems().removeIf(item -> productId.equals(item.getProductId()));
        return removed;
    }

    /**
     * Removes all items from the order, effectively emptying it.
     *
     * @param order the order entity to clear
     */
    public static void clearItems(OrderEntity order) {
        if (order != null && order.getItems() != null) {
            order.getItems().clear();
        }
    }

    /**
     * Gets the quantity of a specific product in the order.
     *
     * @param order the order entity to check
     * @param productId the product ID to look up
     * @return the quantity of the product, 0 if not found or null inputs
     * @throws IllegalArgumentException if productId is null
     */
    public static Integer getProductQuantity(OrderEntity order, Integer productId) {
        if (productId == null) {
            throw new IllegalArgumentException("Product ID cannot be null");
        }
        if (order == null || order.getItems() == null) {
            return 0;
        }

        return order.getItems().stream()
                .filter(item -> productId.equals(item.getProductId()))
                .findFirst()
                .map(ProductListEntity::getQuantity)
                .orElse(0);
    }

    /**
     * Checks if the order has no items.
     *
     * @param order the order entity to check
     * @return true if the order is null, has no items, or items list is empty
     */
    public static boolean isEmpty(OrderEntity order) {
        return order == null || order.getItems() == null || order.getItems().isEmpty();
    }

    /**
     * Gets the number of distinct product line items in the order.
     * This counts the number of different products, not the total quantity.
     *
     * @param order the order entity to check
     * @return the number of distinct product items, 0 if order has no items or is null
     */
    public static Integer getItemCount(OrderEntity order) {
        if (order == null || order.getItems() == null) {
            return 0;
        }
        return order.getItems().size();
    }

    /**
     * Validates that all items in the order have required fields populated.
     *
     * @param order the order entity to validate
     * @return true if all items have non-null productId, productName, price, and quantity
     */
    public static boolean isValid(OrderEntity order) {
        if (order == null || order.getItems() == null) {
            return false;
        }

        return order.getItems().stream()
                .allMatch(item ->
                        item.getProductId() != null &&
                                item.getProductName() != null &&
                                item.getPrice() != null &&
                                item.getQuantity() != null &&
                                item.getQuantity() >= 0
                );
    }
}