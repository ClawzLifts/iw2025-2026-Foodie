package com.foodie.application.helper;

import com.foodie.application.domain.Order;
import com.foodie.application.domain.ProductList;
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
     * Calculates the total monetary value of all items in the order.
     * The total is computed as the sum of (price × quantity) for all items.
     *
     * @param order the order entity to calculate total for
     * @return the total amount as Double, 0.0 if order has no items or is null
     */
    public static Double calculateTotal(Order order) {
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
    public static Integer calculateTotalItems(Order order) {
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
    public static Map<Integer, Integer> getProductQuantitiesMap(Order order) {
        if (order == null || order.getItems() == null || order.getItems().isEmpty()) {
            return Map.of();
        }

        return order.getItems().stream()
                .filter(item -> item.getProductId() != null)
                .collect(Collectors.toMap(
                        ProductList::getProductId,
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
    public static boolean containsProduct(Order order, Integer productId) {
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
     * Gets the quantity of a specific product in the order.
     *
     * @param order the order entity to check
     * @param productId the product ID to look up
     * @return the quantity of the product, 0 if not found or null inputs
     * @throws IllegalArgumentException if productId is null
     */
    public static Integer getProductQuantity(Order order, Integer productId) {
        if (productId == null) {
            throw new IllegalArgumentException("Product ID cannot be null");
        }
        if (order == null || order.getItems() == null) {
            return 0;
        }

        return order.getItems().stream()
                .filter(item -> productId.equals(item.getProductId()))
                .findFirst()
                .map(ProductList::getQuantity)
                .orElse(0);
    }

    /**
     * Checks if the order has no items.
     *
     * @param order the order entity to check
     * @return true if the order is null, has no items, or items list is empty
     */
    public static boolean isEmpty(Order order) {
        return order == null || order.getItems() == null || order.getItems().isEmpty();
    }

    /**
     * Gets the number of distinct product line items in the order.
     * This counts the number of different products, not the total quantity.
     *
     * @param order the order entity to check
     * @return the number of distinct product items, 0 if order has no items or is null
     */
    public static Integer getItemCount(Order order) {
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
    public static boolean isValid(Order order) {
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