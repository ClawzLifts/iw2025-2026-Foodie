package com.foodie.application.payment;

import com.foodie.application.order.OrderEntity;
import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "payment")
public class PaymentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @JoinColumn(name="order_id")
    @OneToOne (mappedBy = "payment")
    private OrderEntity order;

    private Double paymentAmount;
    private String paymentMethod;
    private String paymentStatus;
}