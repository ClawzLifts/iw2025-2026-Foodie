package com.foodie.application.domain;

import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "payment")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @JoinColumn(name="order_id")
    @OneToOne (mappedBy = "payment")
    private Order order;

    private Double paymentAmount;
    private String paymentMethod;
    private String paymentStatus;
}