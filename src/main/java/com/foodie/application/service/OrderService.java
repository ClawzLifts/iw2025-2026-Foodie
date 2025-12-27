package com.foodie.application.service;


import com.foodie.application.domain.Order;
import com.foodie.application.domain.OrderStatus;
import com.foodie.application.domain.ProductList;
import com.foodie.application.dto.OrderFilterDto;
import com.foodie.application.dto.ProductListDto;
import com.foodie.application.repository.OrderRepository;
import com.foodie.application.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;


/**
 * Service class for managing order operations in the Foodie application.
 * <p>
 * This service handles all business logic related to orders, including:
 * creating orders, managing items within orders, updating order status,
 * and retrieving order information.
 * </p>
 *
 * @author Foodie Team
 */
@Service
public class OrderService {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final PaymentService paymentService;

    /**
     * Constructs an OrderService with the required repositories and services.
     *
     * @param userRepository the user repository for database access
     * @param orderRepository the order repository for database access
     * @param paymentService the payment service for payment operations
     */
    public OrderService(UserRepository userRepository,
                        OrderRepository orderRepository,
                        PaymentService paymentService) {
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.paymentService = paymentService;
    }

    /**
     * Creates a new order for a user with the specified products and payment method.
     *
     * @param userID the ID of the user placing the order
     * @param products the list of products to add to the order
     * @param paymentMethod the payment method to use for this order
     * @return the ID of the newly created order
     * @throws EntityNotFoundException if the user is not found
     */
    @Transactional
    public Integer addOrder(Integer userID, List<ProductListDto> products, String paymentMethod) {
        Order newOrder = new Order();
        newOrder.setUser(userRepository.findById(userID).orElseThrow());
        List<ProductList> productList = products.stream().map(productDto ->
                ProductList.builder()
                        .productId(productDto.getProductId())
                        .productName(productDto.getProductName())
                        .price(productDto.getPrice())
                        .quantity(productDto.getQuantity())
                        .build()
        ).toList();
        newOrder.setItems(productList);
        newOrder = orderRepository.save(newOrder);
        newOrder.setPayment(paymentService.createPayment(newOrder, paymentMethod));

        return newOrder.getId();
    }


    /**
     * Adds a product item to an existing order.
     * If the product already exists in the order, its quantity is incremented.
     * If it doesn't exist, a new item is added to the order.
     *
     * @param orderID the ID of the order
     * @param productId the ID of the product to add
     * @param productName the name of the product
     * @param price the price of the product
     * @param quantity the quantity to add
     * @throws EntityNotFoundException if the order is not found
     */
    @Transactional
    public void addItem(Integer orderID, Integer productId, String productName, Double price, Integer quantity) {
        Order order = orderRepository.findById(orderID)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + productId));
        if (order.getItems() == null)
            order.setItems(new java.util.ArrayList<>());

        order.getItems().stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst()
                .ifPresentOrElse( item -> item.setQuantity(item.getQuantity() + quantity),
                        () -> {
                            // Si no existe, lo añade
                            ProductList newItem = ProductList.builder()
                                    .productId(productId)
                                    .productName(productName)
                                    .price(price)
                                    .quantity(quantity)
                                    .build();
                            order.getItems().add(newItem);
                        }
                );
    }


    /**
     * Updates the quantity of a product in an order.
     *
     * @param orderID the ID of the order
     * @param productId the ID of the product to update
     * @param price the price of the product (for reference)
     * @param name the name of the product (for reference)
     * @param newQuantity the new quantity for the product
     * @return true if the product was found and updated, false otherwise
     * @throws EntityNotFoundException if the order is not found
     */
    @Transactional
    public boolean updateProductQuantity(Integer orderID, Integer productId,
                                         Double price, String name, Integer newQuantity) {
        Order order = orderRepository.findById(orderID)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + orderID));

        if (order.getItems() == null || order.getItems().isEmpty()) {
            return false;
        }

        return order.getItems().stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst()
                .map(item -> {
                    item.setQuantity(newQuantity);
                    return true;
                })
                .orElse(false);
    }


    /**
     * Removes a product item from an order.
     *
     * @param orderID the ID of the order as a String
     * @param productID the ID of the product to remove
     * @return true if the product was found and removed, false otherwise
     * @throws EntityNotFoundException if the order is not found
     */
    @Transactional
    public boolean removeProduct(String orderID, Integer productID) {
        Order order = orderRepository.findById(Integer.parseInt(orderID))
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + orderID));

        if (order.getItems() == null || order.getItems().isEmpty()) {
            return false;
        }

        return order.getItems().removeIf(item -> item.getProductId().equals(productID));
    }


    /**
     * Clears all items from an order.
     *
     * @param orderID the ID of the order
     * @throws EntityNotFoundException if the order is not found
     */
    @Transactional
    public void clearItems(Integer orderID) {
        Order order = orderRepository.findById(orderID)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + orderID));
        if (order.getItems() != null && !order.getItems().isEmpty()){
            order.getItems().clear();
        }
    }


    /**
     * Retrieves all orders from the database.
     *
     * @return a list of all orders
     */
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    /**
     * Filters orders based on the provided filter criteria.
     * <p>
     * This is the main method for flexible filtering. It accepts an OrderFilterDto
     * with optional parameters, allowing the following combinations:
     * - Filter by status only (startDate and endDate are null)
     * - Filter by date range only (status is null)
     * - Filter by both status and date range
     * - Retrieve all orders (all parameters are null)
     *
     * All filtering is performed at the database level for optimal performance.
     * </p>
     *
     * @param filter the OrderFilterDto containing filter criteria (can have null values)
     * @return a list of orders matching the specified criteria
     * @throws IllegalArgumentException if the status string is invalid or startDate is after endDate
     */
    public List<Order> filterOrders(OrderFilterDto filter) {
        if (filter == null) {
            return getAllOrders();
        }

        boolean hasStatus = filter.getStatus() != null && !filter.getStatus().isEmpty();
        boolean hasDateRange = filter.getStartDate() != null && filter.getEndDate() != null;

        // Validate dates if provided
        if (hasDateRange && filter.getStartDate().after(filter.getEndDate())) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }

        // Convert status to enum if provided
        OrderStatus orderStatus = null;
        if (hasStatus) {
            try {
                orderStatus = OrderStatus.valueOf(filter.getStatus().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid order status: " + filter.getStatus() +
                        ". Valid statuses are: " + java.util.Arrays.toString(OrderStatus.values()), e);
            }
        }

        // Execute the appropriate query based on provided parameters
        if (hasStatus && hasDateRange) {
            // Filter by both status and date range
            return orderRepository.findByStatusAndDateRange(orderStatus, filter.getStartDate(), filter.getEndDate());
        } else if (hasStatus) {
            // Filter by status only
            return orderRepository.findByStatus(orderStatus);
        } else if (hasDateRange) {
            // Filter by date range only
            return orderRepository.findByDateRange(filter.getStartDate(), filter.getEndDate());
        } else {
            // No filters, return all orders
            return getAllOrders();
        }
    }

    /**
     * Retrieves a specific order by its ID.
     *
     * @param orderID the ID of the order to retrieve
     * @return the Order object
     * @throws EntityNotFoundException if the order is not found
     */
    public Order getOrderById(Integer orderID) {
        return orderRepository.findById(orderID)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + orderID));
    }

    /**
     * Retrieves all orders placed by a specific user.
     *
     * @param userID the ID of the user
     * @return a list of orders for the specified user
     */
    public List<Order> getOrdersByUserId(Integer userID) {
        return orderRepository.findByUserId(userID);
    }

    /**
     * Deletes an order from the database.
     *
     * @param orderID the ID of the order to delete
     * @throws EntityNotFoundException if the order is not found
     */
    @Transactional
    public void deleteOrder(Integer orderID) {
        Order order = orderRepository.findById(orderID)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + orderID));
        orderRepository.delete(order);
    }


    /**
     * Retrieves all orders that match the specified status.
     * <p>
     * This method acts as a bridge between the presentation layer (Vaadin)
     * and the domain layer by accepting a String status and converting it
     * to the OrderStatus enum for database filtering.
     * Query is executed at the database level for optimal performance.
     * </p>
     *
     * @param status the status as a String (e.g., "PENDING", "COMPLETED")
     * @return a list of orders matching the specified status
     * @throws IllegalArgumentException if the status string is not a valid OrderStatus
     */
    public List<Order> getOrdersByStatus(String status) {
        try {
            OrderStatus orderStatus = OrderStatus.valueOf(status.toUpperCase());
            return orderRepository.findByStatus(orderStatus);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid order status: " + status +
                    ". Valid statuses are: " + java.util.Arrays.toString(OrderStatus.values()), e);
        }
    }

    /**
     * Retrieves all orders that match the specified status.
     * <p>
     * This overloaded method accepts an OrderStatus enum directly and is intended
     * for internal use between service layers, not for direct calls from the presentation layer.
     * Query is executed at the database level for optimal performance.
     * </p>
     *
     * @param status the OrderStatus enum to filter by
     * @return a list of orders matching the specified status
     */
    public List<Order> getOrdersByStatus(OrderStatus status) {
        return orderRepository.findByStatus(status);
    }

    /**
     * Retrieves all orders within a specified date range.
     * <p>
     * This method filters orders by their creation date between the start and end dates (inclusive).
     * Query is executed at the database level for optimal performance.
     * </p>
     *
     * @param startDate the start date of the range (inclusive)
     * @param endDate the end date of the range (inclusive)
     * @return a list of orders created within the specified date range
     * @throws IllegalArgumentException if startDate is after endDate
     */
    public List<Order> getOrdersByDateRange(Date startDate, Date endDate) {
        if (startDate.after(endDate)) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }

        return orderRepository.findByDateRange(startDate, endDate);
    }

    /**
     * Retrieves all orders that match the specified status and are within a date range.
     * <p>
     * This method combines status and date range filtering for more precise queries.
     * Acts as a bridge between the presentation layer (Vaadin) and the domain layer.
     * Query is executed at the database level for optimal performance.
     * </p>
     *
     * @param status the status as a String (e.g., "PENDING", "COMPLETED")
     * @param startDate the start date of the range (inclusive)
     * @param endDate the end date of the range (inclusive)
     * @return a list of orders matching both the status and date range
     * @throws IllegalArgumentException if the status string is invalid or startDate is after endDate
     */
    public List<Order> getOrdersByStatusAndDateRange(String status, Date startDate, Date endDate) {
        if (startDate.after(endDate)) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }

        try {
            OrderStatus orderStatus = OrderStatus.valueOf(status.toUpperCase());
            return orderRepository.findByStatusAndDateRange(orderStatus, startDate, endDate);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid order status: " + status +
                    ". Valid statuses are: " + java.util.Arrays.toString(OrderStatus.values()), e);
        }
    }

    /**
     * Retrieves all orders that match the specified status and are within a date range.
     * <p>
     * This overloaded method accepts an OrderStatus enum directly and is intended
     * for internal use between service layers, not for direct calls from the presentation layer.
     * Query is executed at the database level for optimal performance.
     * </p>
     *
     * @param status the OrderStatus enum to filter by
     * @param startDate the start date of the range (inclusive)
     * @param endDate the end date of the range (inclusive)
     * @return a list of orders matching both the status and date range
     * @throws IllegalArgumentException if startDate is after endDate
     */
    public List<Order> getOrdersByStatusAndDateRange(OrderStatus status, Date startDate, Date endDate) {
        if (startDate.after(endDate)) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }

        return orderRepository.findByStatusAndDateRange(status, startDate, endDate);
    }




}
