package com.foodie.application.dto;

import com.foodie.application.domain.Allergen;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * DTO for displaying allergen information in the UI.
 * Transfers allergen data from service to presentation layer without exposing domain model.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AllergenDto implements Serializable {
    private Integer id;
    private String name;

    /**
     * Converts an Allergen entity to AllergenDisplayDto
     */
    public static AllergenDto fromAllergen(Allergen allergen) {
        return AllergenDto.builder()
                .id(allergen.getId())
                .name(allergen.getName())
                .build();
    }
}

