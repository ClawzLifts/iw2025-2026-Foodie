package com.foodie.application.service;

import com.foodie.application.domain.Product;
import com.foodie.application.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class CartaService {

    private final ProductRepository productRepository;

    @Autowired
    public CartaService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getAllProductos() {
        return productRepository.findAll();
    }

    public Optional<Product> getProductoPorId(Integer id) {
        return productRepository.findById(id);
    }

    public Product guardarProducto(Product producto) {
        return productRepository.save(producto);
    }

    public void eliminarProducto(Integer id) {
        productRepository.deleteById(id);
    }

    // Métodos adicionales según tu lógica
}
