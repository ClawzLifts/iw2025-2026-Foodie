package com.foodie.application.service;

import com.foodie.application.domain.Order;
import com.foodie.application.domain.Product;
import com.foodie.application.domain.User;
import com.foodie.application.repository.OrderRepository;
import com.foodie.application.repository.ProductRepository;
import com.foodie.application.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DashboardService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Autowired
    public DashboardService(OrderRepository orderRepository, ProductRepository productRepository, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public long countProducts() {
        return productRepository.count();
    }

    public long countUsers() {
        return userRepository.count();
    }

    public List<Order> getOrdersByUser(Integer userId) {
        Optional<User> user = userRepository.findById(userId);
        return user.map(orderRepository::findByUser).orElse(List.of());
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
}
