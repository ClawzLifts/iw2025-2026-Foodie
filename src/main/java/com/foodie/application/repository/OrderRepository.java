package com.foodie.application.repository;

import com.foodie.application.domain.Order;
import com.foodie.application.domain.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

/**
 * Repository interface for Order entity.
 * <p>
 * Provides database access methods for Order entities with various filtering options.
 * All queries are executed at the database level for optimal performance.
 * </p>
 */
public interface OrderRepository extends JpaRepository<Order, Integer> {

    /**
     * Finds all orders placed by a specific user.
     *
     * @param userID the ID of the user
     * @return a list of orders for the specified user
     */
    List<Order> findByUserId(Integer userID);

    /**
     * Finds all orders with a specific status.
     *
     * @param status the OrderStatus to filter by
     * @return a list of orders matching the specified status
     */
    List<Order> findByStatus(OrderStatus status);

    /**
     * Finds all orders created within a specified date range.
     * <p>
     * The date range is inclusive on both ends.
     * </p>
     *
     * @param startDate the start date (inclusive)
     * @param endDate the end date (inclusive)
     * @return a list of orders created within the date range
     */
    @Query("SELECT o FROM Order o WHERE o.date >= :startDate AND o.date <= :endDate")
    List<Order> findByDateRange(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    /**
     * Finds all orders with a specific status created within a date range.
     * <p>
     * Both filters are applied together for precise results.
     * </p>
     *
     * @param status the OrderStatus to filter by
     * @param startDate the start date (inclusive)
     * @param endDate the end date (inclusive)
     * @return a list of orders matching both criteria
     */
    @Query("SELECT o FROM Order o WHERE o.status = :status AND o.date >= :startDate AND o.date <= :endDate")
    List<Order> findByStatusAndDateRange(@Param("status") OrderStatus status,
                                         @Param("startDate") Date startDate,
                                         @Param("endDate") Date endDate);
}