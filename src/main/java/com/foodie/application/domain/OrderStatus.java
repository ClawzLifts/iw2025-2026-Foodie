package com.foodie.application.domain;

/**
 * Enumeration representing the possible statuses of an order.
 * <p>
 * This enum defines the lifecycle states that an order can transition through
 * from creation to completion or cancellation.
 * </p>
 */
public enum OrderStatus {
    /**
     * The order has been created but not yet confirmed.
     */
    PENDING,

    /**
     * The order has been confirmed and is being prepared.
     */
    CONFIRMED,

    /**
     * The order is currently being prepared by the restaurant.
     */
    PREPARING,

    /**
     * The order is ready for pickup or delivery.
     */
    READY,

    /**
     * The order is on the way to the customer (for delivery orders).
     */
    ON_THE_WAY,

    /**
     * The order has been delivered or picked up successfully.
     */
    COMPLETED,

    /**
     * The order has been cancelled by the customer or restaurant.
     */
    CANCELLED,

    /**
     * The order has failed or encountered issues.
     */
    FAILED
}

