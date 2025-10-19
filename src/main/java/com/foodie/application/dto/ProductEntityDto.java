package com.foodie.application.dto;

import com.foodie.application.domain.Product;
import lombok.Value;

import java.io.Serializable;
import java.util.Set;

/**
 * DTO for {@link Product}
 */
@Value
public class ProductEntityDto implements Serializable {
    int id;
    String name;
    double price;
    String description;
    Set<Set<String>> allergens;
}