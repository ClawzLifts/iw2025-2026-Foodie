package com.foodie.application.dto;

import com.foodie.application.domain.ProductList;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * DTO for {@link ProductList}
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductListDto implements Serializable {
    private Integer productId;
    private String productName;
    private Double price;
    private Integer quantity;

    /**
     * Converts a ProductList entity to ProductListDto
     */
    public static ProductListDto fromProductList(ProductList productList) {
        return ProductListDto.builder()
                .productId(productList.getProductId())
                .productName(productList.getProductName())
                .price(productList.getPrice())
                .quantity(productList.getQuantity())
                .build();
    }
}