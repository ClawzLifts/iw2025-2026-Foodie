package com.foodie.application.domain;

import com.foodie.application.domain.Product;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "menu_item")
public class MenuItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne
    @JoinColumn(name = "menu_id", nullable = false)
    private Menu menu;

    private Boolean featured;
    private Integer discountPercentage;

    public String getName() {
        return product != null ? product.getName() : "Sin nombre";
    }

    public Double getPrice() {
        return product != null ? product.getPrice() : 0.0;
    }
}