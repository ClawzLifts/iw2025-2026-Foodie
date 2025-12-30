package com.foodie.application.dto;

import com.foodie.application.domain.Order;
import com.foodie.application.domain.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO for displaying order information in the UI.
 * This class is used to transfer order data from the service layer to the presentation layer
 * without exposing the internal domain model.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto implements Serializable {
    private Integer id;
    private String username;
    private LocalDate date;
    private OrderStatus status;
    private Double totalAmount;
    private List<ProductListDto> items;
    private PaymentDto payment;

    /**
     * Converts an Order entity to OrderDisplayDto
     */
    public static OrderDto fromOrder(Order order) {
        return OrderDto.builder()
                .id(order.getId())
                .username(order.getUser().getUsername())
                .date(order.getDate())
                .status(order.getStatus())
                .items(order.getItems().stream()
                        .map(ProductListDto::fromProductList)
                        .collect(java.util.stream.Collectors.toList()))
                .payment(order.getPayment() != null ? PaymentDto.fromPayment(order.getPayment()) : null)
                .build();
    }
}

