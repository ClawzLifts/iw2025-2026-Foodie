package com.foodie.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * DTO for sales statistics data.
 * Contains aggregated sales information for a specific date or product.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalesStatisticsDto implements Serializable {
    private LocalDate date;
    private String productName;
    private Integer productId;
    private Integer quantitySold;
    private Double totalRevenue;
    private Integer numberOfOrders;
    private Double averageOrderValue;

    public SalesStatisticsDto(LocalDate date, Integer quantitySold, Double totalRevenue) {
        this.date = date;
        this.quantitySold = quantitySold;
        this.totalRevenue = totalRevenue;
    }

    public SalesStatisticsDto(String productName, Integer productId, Integer quantitySold, Double totalRevenue) {
        this.productName = productName;
        this.productId = productId;
        this.quantitySold = quantitySold;
        this.totalRevenue = totalRevenue;
    }
}

