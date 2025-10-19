package com.foodie.application.service;


import com.foodie.application.domain.Order;
import com.foodie.application.domain.ProductList;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;



@Service
public class OrderService {

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
    @Transactional
    public void addItem(Order order, Integer productId, String productName, Double price, Integer quantity) {
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
        ProductList newItem = ProductList.builder()
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
    @Transactional
    public void addItem(Order order, ProductList item) {
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
     * Updates the quantity of a specific product in the order.
     * If the product is not found in the order, no action is taken.
     *
     * @param order the order entity to modify
     * @param productId the product ID to update
     * @param newQuantity the new quantity to set
     * @return true if the product was found and updated, false otherwise
     * @throws IllegalArgumentException if productId is null or newQuantity is negative
     */
    @Transactional
    public boolean updateProductQuantity(Order order, Integer productId, Integer newQuantity) {
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
    @Transactional
    public boolean removeProduct(Order order, Integer productId) {
        if (productId == null) {
            throw new IllegalArgumentException("Product ID cannot be null");
        }
        if (order == null || order.getItems() == null) {
            return false;
        }

        return order.getItems().removeIf(item -> productId.equals(item.getProductId()));
    }

    /**
     * Removes all items from the order, effectively emptying it.
     *
     * @param order the order entity to clear
     */
    public void clearItems(Order order) {
        if (order != null && order.getItems() != null) {
            order.getItems().clear();
        }
    }
}
