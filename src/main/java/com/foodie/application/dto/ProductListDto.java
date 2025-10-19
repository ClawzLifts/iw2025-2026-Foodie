package com.foodie.application.dto;

import com.foodie.application.domain.ProductList;
import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link ProductList}
 */
@Value
public class ProductListDto implements Serializable {
    Integer productId;
    String productName;
    Double price;
    Integer quantity;
}