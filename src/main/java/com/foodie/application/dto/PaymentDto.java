package com.foodie.application.dto;

import com.foodie.application.domain.Payment;
import com.foodie.application.domain.PaymentMethod;
import com.foodie.application.domain.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * DTO for displaying payment information in the UI.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDto implements Serializable {
    private Integer id;
    private Double paymentAmount;
    private PaymentMethod paymentMethod;
    private PaymentStatus paymentStatus;

    /**
     * Converts a Payment entity to PaymentDisplayDto
     */
    public static PaymentDto fromPayment(Payment payment) {
        return PaymentDto.builder()
                .id(payment.getId())
                .paymentAmount(payment.getPaymentAmount())
                .paymentMethod(payment.getPaymentMethod())
                .paymentStatus(payment.getPaymentStatus())
                .build();
    }
}

