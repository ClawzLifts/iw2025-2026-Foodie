package com.foodie.application.dto;

import com.foodie.application.domain.Ingredient;

/**
 * DTO for {@link Ingredient}
 */
@lombok.Value
public class IngredientDto {

    Integer id;
    String name;

    public IngredientDto(Ingredient ingredient) {
        this.id = ingredient.getId();
        this.name = ingredient.getName();
    }
}
