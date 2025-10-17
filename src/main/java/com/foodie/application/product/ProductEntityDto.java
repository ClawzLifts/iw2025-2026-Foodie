package com.foodie.application.product;

import lombok.Value;

import java.io.Serializable;
import java.util.Set;

/**
 * DTO for {@link ProductEntity}
 */
@Value
public class ProductEntityDto implements Serializable {
    int id;
    String name;
    double price;
    String description;
    Set<Set<String>> allergens;
}