package com.foodie.application.dto;

import com.foodie.application.domain.Establishment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalTime;

/**
 * DTO for displaying and managing establishment information in the UI.
 * Transfers establishment data from service to presentation layer without exposing domain model.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EstablishmentDto implements Serializable {
    private Integer id;
    private String name;
    private String description;
    private String address;
    private String phone;
    private LocalTime openingTime;
    private LocalTime closingTime;

    /**
     * Converts an Establishment entity to EstablishmentDto
     */
    public static EstablishmentDto fromEstablishment(Establishment establishment) {
        return EstablishmentDto.builder()
                .id(establishment.getId())
                .name(establishment.getName())
                .description(establishment.getDescription())
                .address(establishment.getAddress())
                .phone(establishment.getPhone())
                .openingTime(establishment.getOpeningTime())
                .closingTime(establishment.getClosingTime())
                .build();
    }

    /**
     * Converts EstablishmentDto to Establishment entity
     */
    public static Establishment toEstablishment(EstablishmentDto dto) {
        return Establishment.builder()
                .id(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .address(dto.getAddress())
                .phone(dto.getPhone())
                .openingTime(dto.getOpeningTime())
                .closingTime(dto.getClosingTime())
                .build();
    }
}

