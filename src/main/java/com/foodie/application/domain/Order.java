package com.foodie.application.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"user", "payment"})
@Table(name = "orders", indexes = {
    @Index(name = "idx_user_id", columnList = "user_id"),
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_date", columnList = "date"),
    @Index(name = "idx_user_date", columnList = "user_id, date"),
    @Index(name = "idx_status_date", columnList = "status, date")
})
public class Order {
    @Id
    @Column(unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(optional = false)
    @JoinColumn(name="user_id")
    private User user;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private List<ProductList> items = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="payment_id")
    private Payment payment;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private OrderStatus status;

    @Column
    private String notes;

    @Column
    private String deliveryAddress;





}
