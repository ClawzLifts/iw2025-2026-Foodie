package com.foodie.application.dto;

import com.foodie.application.domain.CashClosing;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * DTO for CashClosing entity
 * Used for transferring cash closing data to the presentation layer
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CashClosingDto implements Serializable {
    private Integer id;
    private LocalDate date;
    private Map<String, Double> openingBalance = new HashMap<>();
    private Map<String, Double> expectedAmount = new HashMap<>();
    private Map<String, Double> realAmount = new HashMap<>();
    private Map<String, Double> difference = new HashMap<>();
    private String notes;
    private Boolean isClosed;

    /**
     * Converts a CashClosing entity to CashClosingDto
     */
    public static CashClosingDto fromCashClosing(CashClosing cashClosing) {
        return CashClosingDto.builder()
                .id(cashClosing.getId())
                .date(cashClosing.getDate())
                .openingBalance(new HashMap<>(cashClosing.getOpeningBalance()))
                .expectedAmount(new HashMap<>(cashClosing.getExpectedAmount()))
                .realAmount(new HashMap<>(cashClosing.getRealAmount()))
                .difference(new HashMap<>(cashClosing.getDifference()))
                .notes(cashClosing.getNotes())
                .isClosed(cashClosing.getIsClosed())
                .build();
    }

    /**
     * Converts a CashClosingDto to CashClosing entity
     */
    public CashClosing toEntity() {
        return CashClosing.builder()
                .id(this.id)
                .date(this.date)
                .openingBalance(new HashMap<>(this.openingBalance))
                .expectedAmount(new HashMap<>(this.expectedAmount))
                .realAmount(new HashMap<>(this.realAmount))
                .difference(new HashMap<>(this.difference))
                .notes(this.notes)
                .isClosed(this.isClosed)
                .build();
    }
}

