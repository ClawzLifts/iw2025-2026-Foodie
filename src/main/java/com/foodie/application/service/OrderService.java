package com.foodie.application.service;


import com.foodie.application.domain.Order;
import com.foodie.application.domain.ProductList;
import com.foodie.application.dto.ProductListDto;
import com.foodie.application.repository.OrderRepository;
import com.foodie.application.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import java.util.List;


@Service
public class OrderService {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final PaymentService paymentService;

    public OrderService(UserRepository userRepository,
                        OrderRepository orderRepository,
                        PaymentService paymentService) {
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.paymentService = paymentService;
    }

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


    @Transactional
    public boolean removeProduct(String orderID, Integer productID) {
        Order order = orderRepository.findById(Integer.parseInt(orderID))
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + orderID));

        if (order.getItems() == null || order.getItems().isEmpty()) {
            return false;
        }

        return order.getItems().removeIf(item -> item.getProductId().equals(productID));
    }


    @Transactional
    public void clearItems(Integer orderID) {
        Order order = orderRepository.findById(orderID)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + orderID));
        if (order.getItems() != null && !order.getItems().isEmpty()){
            order.getItems().clear();
        }
    }
}
