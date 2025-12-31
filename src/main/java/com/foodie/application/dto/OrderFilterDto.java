package com.foodie.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Data Transfer Object for order filtering parameters.
 * <p>
 * This DTO is used to pass filter criteria from the presentation layer (Vaadin)
 * to the service layer. All parameters are optional, allowing flexible filtering:
 * - Filter by status alone
 * - Filter by date range alone
 * - Filter by both status and date range
 * - Retrieve all orders (if all parameters are null)
 * </p>
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderFilterDto {

    /**
     * The status to filter by as a String (e.g., "PENDING", "COMPLETED").
     * If null, no status filtering is applied.
     */
    private String status;

    /**
     * The start date of the range (inclusive).
     * If null, no start date filtering is applied.
     */
    private LocalDate startDate;

    /**
     * The end date of the range (inclusive).
     * If null, no end date filtering is applied.
     */
    private LocalDate endDate;
}

