package com.foodie.application.dto;

import com.foodie.application.domain.Product;
import lombok.*;

import java.io.Serializable;
import java.util.Set;

/**
 * DTO for {@link Product}
 */
@Value
@Data
@AllArgsConstructor
@Builder
public class ProductDto implements Serializable {
    Integer id;
    String name;
    Double price;
    String description;
    Set<String> allergens;
    String imageUrl;

    public ProductDto(Product product){
        this.id = product.getId();
        this.name = product.getName();
        this.price = product.getPrice();
        this.description = product.getDescription();
        this.allergens = product.getAllergens();
        this.imageUrl = product.getImageUrl();
    }
}