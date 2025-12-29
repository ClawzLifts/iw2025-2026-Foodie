package com.foodie.application.dto;

import com.foodie.application.domain.Allergen;

import java.io.Serializable;

/**
 * DTO for {@link Allergen}
 */
@lombok.Value
public class AllergenDto implements Serializable {
    Integer id;
    String name;

    public AllergenDto(Allergen allergen) {
        this.id = allergen.getId();
        this.name = allergen.getName();
    }
}
