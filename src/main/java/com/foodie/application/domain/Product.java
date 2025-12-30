package com.foodie.application.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

/**
 * Product entity representing a menu item.
 * Products can have multiple allergens associated with them.
 */
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "product")
@Getter
@Setter
@ToString(exclude = "allergens")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false)
    private String description;

    private String imageUrl;

    @ManyToMany
    @JoinTable(
            name = "product_allergen",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "allergen_id")
    )
    private Set<Allergen> allergens;
}

