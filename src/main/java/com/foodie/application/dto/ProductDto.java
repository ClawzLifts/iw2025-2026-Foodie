package com.foodie.application.dto;

import com.foodie.application.domain.Allergen;
import com.foodie.application.domain.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * DTO for displaying product information in the UI.
 * Transfers product data from service to presentation layer without exposing domain model.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto implements Serializable {
    private Integer id;
    private String name;
    private String description;
    private Double price;
    private String imageUrl;
    private Set<String> allergenNames;

    /**
     * Converts a Product entity to ProductDisplayDto
     */
    public static ProductDto fromProduct(Product product) {
        Set<String> allergenNames = product.getAllergens() != null ?
                product.getAllergens().stream()
                        .map(Allergen::getName)
                        .collect(Collectors.toSet()) :
                java.util.Collections.emptySet();

        return ProductDto.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .imageUrl(product.getImageUrl())
                .allergenNames(allergenNames)
                .build();
    }
}

