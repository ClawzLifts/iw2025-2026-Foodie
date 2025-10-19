package com.foodie.application.service;

import com.foodie.application.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

    private ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional
    public void removeProduct(Integer productId) {
        if (!productRepository.existsById(productId)) {
            throw new EntityNotFoundException("Product not found with id: " + productId);
        }
        productRepository.deleteById(productId);
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
        product.getAllergens().addAll(newAllergens);
    }

    @Transactional
    public void removeProductAllergens(Integer productId, java.util.Set<String> allergensToRemove) {
        var product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + productId));
        product.getAllergens().removeAll(allergensToRemove);
    }

}
