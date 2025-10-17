package com.foodie.application.order;

import com.foodie.application.payment.PaymentEntity;
import com.foodie.application.user.UserEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "orders")
public class OrderEntity {
    @Id
    @Column(unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(optional = false)
    @JoinColumn(name="user_id")
    private UserEntity user;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private List<ProductListEntity> items;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="payment_id")
    private PaymentEntity payment;
}
