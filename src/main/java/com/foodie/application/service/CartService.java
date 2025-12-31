package com.foodie.application.service;

import com.vaadin.flow.server.VaadinSession;
import com.foodie.application.domain.ProductList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Service class for managing the shopping cart.
 * <p>
 * This service provides methods for cart operations including adding,
 * removing, and clearing items. The cart is stored in the Vaadin session,
 * making it persistent throughout the user's session.
 * </p>
 *
 * @author Foodie Team
 */
@Slf4j
@Service
public class CartService {

    private static final String CART_SESSION_KEY = "foodie_shopping_cart";

    /**
     * Gets the shopping cart from the current Vaadin session.
     * If no cart exists in the session, creates and stores a new empty cart.
     *
     * @return the shopping cart (List of ProductList items)
     */
    public List<ProductList> getCart() {
        VaadinSession session = VaadinSession.getCurrent();
        if (session == null) {
            log.warn("VaadinSession is null, returning empty cart");
            return new ArrayList<>();
        }

        @SuppressWarnings("unchecked")
        List<ProductList> cart = (List<ProductList>) session.getAttribute(CART_SESSION_KEY);

        if (cart == null) {
            cart = Collections.synchronizedList(new ArrayList<>());
            session.setAttribute(CART_SESSION_KEY, cart);
        }

        return cart;
    }

    /**
     * Adds a product to the shopping cart.
     * If the product already exists, increments its quantity.
     * If it doesn't exist, adds a new item with the specified quantity.
     *
     * @param productId the ID of the product
     * @param productName the name of the product
     * @param price the price of the product
     * @param quantity the quantity to add
     */
    public void addToCart(Integer productId, String productName, Double price, Integer quantity) {
        List<ProductList> cart = getCart();

        Optional<ProductList> existingItem = cart.stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst();

        if (existingItem.isPresent()) {
            // Product already in cart, increment quantity
            existingItem.get().setQuantity(existingItem.get().getQuantity() + quantity);
            log.debug("Updated product {} quantity in cart", productId);
        } else {
            // New product, add to cart
            ProductList newItem = ProductList.builder()
                    .productId(productId)
                    .productName(productName)
                    .price(price)
                    .quantity(quantity)
                    .build();
            cart.add(newItem);
            log.debug("Added product {} to cart", productId);
        }
    }

    /**
     * Removes a product from the shopping cart.
     *
     * @param productId the ID of the product to remove
     * @return true if the product was found and removed, false otherwise
     */
    public boolean removeFromCart(Integer productId) {
        List<ProductList> cart = getCart();
        boolean removed = cart.removeIf(item -> item.getProductId().equals(productId));
        if (removed) {
            log.debug("Removed product {} from cart", productId);
        }
        return removed;
    }

    /**
     * Updates the quantity of a product in the cart.
     *
     * @param productId the ID of the product
     * @param newQuantity the new quantity
     * @return true if the product was found and updated, false otherwise
     */
    public boolean updateCartItemQuantity(Integer productId, Integer newQuantity) {
        List<ProductList> cart = getCart();

        Optional<ProductList> item = cart.stream()
                .filter(p -> p.getProductId().equals(productId))
                .findFirst();

        if (item.isPresent()) {
            if (newQuantity <= 0) {
                cart.remove(item.get());
                log.debug("Removed product {} from cart (quantity <= 0)", productId);
            } else {
                item.get().setQuantity(newQuantity);
                log.debug("Updated product {} quantity to {}", productId, newQuantity);
            }
            return true;
        }

        return false;
    }

    /**
     * Clears all items from the shopping cart.
     */
    public void clearCart() {
        List<ProductList> cart = getCart();
        cart.clear();
        log.debug("Shopping cart cleared");
    }

    /**
     * Gets the total number of items in the cart.
     * This is the sum of all quantities, not the number of different products.
     *
     * @return the total quantity of items in the cart
     */
    public Integer getCartItemCount() {
        return getCart().stream()
                .mapToInt(ProductList::getQuantity)
                .sum();
    }

    /**
     * Calculates the total price of all items in the cart.
     *
     * @return the total price as Double
     */
    public Double getCartTotal() {
        return getCart().stream()
                .mapToDouble(item -> {
                    Double price = item.getPrice() != null ? item.getPrice() : 0.0;
                    Integer quantity = item.getQuantity() != null ? item.getQuantity() : 0;
                    return price * quantity;
                })
                .sum();
    }

    /**
     * Checks if the shopping cart is empty.
     *
     * @return true if the cart has no items, false otherwise
     */
    public boolean isCartEmpty() {
        return getCart().isEmpty();
    }

    /**
     * Gets a copy of the cart items as an unmodifiable list.
     *
     * @return an unmodifiable list of cart items
     */
    public List<ProductList> getCartItems() {
        return Collections.unmodifiableList(getCart());
    }
}

