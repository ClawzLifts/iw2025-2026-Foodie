package com.foodie.application.service;

import com.foodie.application.domain.Product;
import com.foodie.application.repository.AllergenRepository;
import com.foodie.application.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
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
}
