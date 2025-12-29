package com.foodie.application.service;

import com.foodie.application.domain.Order;
import com.foodie.application.domain.Product;
import com.foodie.application.dto.ProductSalesDto;
import com.foodie.application.repository.AllergenRepository;
import com.foodie.application.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final final ProductRepository productRepository;
    private OrderService orderService;
    private final AllergenRepository allergenRepository;

    public ProductService(ProductRepository productRepository, AllergenRepository allergenRepository) {
        this.allergenRepository = allergenRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public void removeProduct(Integer productId) {
        if (!productRepository.existsById(productId)) {
            throw new EntityNotFoundException("Product not found with id: " + productId);
        }
        productRepository.deleteById(productId);
    }

    public List<Product> getAllProducts(Integer menuId) {
        return productRepository.findAll();
    }

    @Transactional
    public void updateProductPrice(Integer productId, Double newPrice) {
        var product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + productId));
        product.setPrice(newPrice);
    }

    @Transactional
    public void updateProductDescription(Integer productId, String newDescription) {
        var product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + productId));
        product.setDescription(newDescription);
    }

    @Transactional
    public void updateProductName(Integer productId, String newName) {
        var product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + productId));
        product.setName(newName);
    }

    @Transactional
    public void addProductAllergens(Integer productId, java.util.Set<String> newAllergens) {
        var product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + productId));
        var allergens = allergenRepository.findByNameIn(newAllergens);

        product.getAllergens().addAll(allergens);
    }

    @Transactional
    public void removeProductAllergens(Integer productId, java.util.Set<String> allergensToRemove) {
        var product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + productId));
        var allergens = allergenRepository.findByNameIn(allergensToRemove);

        product.getAllergens().removeAll(allergens);
    }

    public List<Product> getProducts(int i) {
        return null;
    }

    @Transactional
    public void addProduct(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("El producto no puede ser nulo");
        }
        productRepository.save(product);
    }
    public long countProducts() {
        return productRepository.count();
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Transactional
    public List<ProductSalesDto> getTopSellingProducts(int topN) {
        List<Order> orders = orderService.getAllOrders();
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
