package com.foodie.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductSalesDto {
    private Integer productId;
    private String name;
    private Integer quantity;
    private Double revenue;
}

