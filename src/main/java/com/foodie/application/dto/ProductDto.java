package com.foodie.application.dto;

import com.foodie.application.domain.Allergen;
import com.foodie.application.domain.Ingredient;
import com.foodie.application.domain.Product;
import lombok.Value;

import java.io.Serializable;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * DTO for {@link Product}
 */
@Value
public class ProductDto implements Serializable {
    Integer id;
    String name;
    Double price;
    String description;
    Set<String> allergens;
    Set<String> ingredients;
    String imageUrl;

    public ProductDto(Product product){
        this.id = product.getId();
        this.name = product.getName();
        this.price = product.getPrice();
        this.description = product.getDescription();
        Set<Allergen> allergenSet = product.getAllergens();
        this.allergens = allergenSet == null ? Set.of() : allergenSet.stream()
                .map(Allergen::getName)
                .collect(Collectors.toSet());
        Set<Ingredient> ingredientSet = product.getIngredients();
        this.ingredients = ingredientSet == null ? Set.of() : ingredientSet.stream()
                .map(Ingredient::getName)
                .collect(Collectors.toSet());
        this.imageUrl = product.getImageUrl();

    }
}