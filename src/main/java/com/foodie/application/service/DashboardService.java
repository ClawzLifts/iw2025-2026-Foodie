package com.foodie.application.service;

import com.foodie.application.domain.Order;
import com.foodie.application.domain.Product;
import com.foodie.application.domain.User;
import com.foodie.application.repository.OrderRepository;
import com.foodie.application.repository.ProductRepository;
import com.foodie.application.repository.UserRepository;
import com.foodie.application.repository.MenuRepository;
import com.foodie.application.domain.Menu;
import com.foodie.application.dto.ProductSalesDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private MenuItemService menuItemService;

    @Autowired
    public DashboardService(OrderRepository orderRepository, ProductRepository productRepository, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Order> getOrdersByStatus(String status) {
        if (status == null || status.isBlank() || "ALL".equalsIgnoreCase(status)) {
            return getAllOrders();
        }
        String normalized = status.trim();
        return orderRepository.findAll().stream()
                .filter(o -> o.getStatus() != null && o.getStatus().equalsIgnoreCase(normalized))
                .collect(Collectors.toList());
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

    public List<Menu> getAllMenus() {
        return menuRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Menu> getAllMenusWithItems() {
        List<Menu> menus = menuRepository.findAll();
        menus.forEach(menu -> {
            if (menu.getMenuItems() != null) {
                menu.getMenuItems().size();
            }
        });
        return menus;
    }

    @Transactional(readOnly = true)
    public Menu getMenuById(Integer id) {
        return menuRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Menu no encontrado con ID: " + id));
    }

    @Transactional
    public void addProductToMenu(Integer menuId, Integer productId) {
        Menu menu = menuRepository.findById(menuId).orElseThrow(() -> new IllegalArgumentException("Menu no encontrado con ID: " + menuId));
        menuItemService.addMenuItem(productId, menu, false, 0);
    }

    @Transactional
    public void removeProductFromMenu(Integer menuId, Integer productId) {
        menuItemService.deleteMenuItem(menuId, productId);
    }

    @Transactional
    public void addMenuItem(Integer menuId, Integer productId) {
        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new IllegalArgumentException("Menu no encontrado con ID: " + menuId));
        menuItemService.addMenuItem(productId, menu, false, 0);
    }

    @Transactional
    public void removeMenuItem(Integer menuId, Integer productId) {
        menuItemService.deleteMenuItem(menuId, productId);
    }

    @Transactional(readOnly = true)
    public List<ProductSalesDto> getTopSellingProducts(int topN) {
        List<Order> orders = getAllOrders();
        Map<Integer, ProductSalesDto> agg = new HashMap<>();

        orders.forEach(order -> {
            if (order.getItems() == null) return;
            order.getItems().forEach(item -> {
                int pid = item.getProductId();
                int qty = item.getQuantity() == null ? 0 : item.getQuantity();
                double revenue = (item.getPrice() == null ? 0.0 : item.getPrice()) * qty;
                ProductSalesDto cur = agg.get(pid);
                if (cur == null) {
                    String name = item.getProductName();
                    // try to get fresh name from product repository if available
                    var prodOpt = productRepository.findById(pid);
                    if (prodOpt.isPresent()) name = prodOpt.get().getName();
                    cur = new ProductSalesDto(pid, name, qty, revenue);
                    agg.put(pid, cur);
                } else {
                    cur.setQuantity(cur.getQuantity() + qty);
                    cur.setRevenue(cur.getRevenue() + revenue);
                }
            });
        });

        return agg.values().stream()
                .sorted(Comparator.comparingInt(ProductSalesDto::getQuantity).reversed())
                .limit(topN)
                .collect(Collectors.toList());
    }
}
