package com.foodie.application.domain;

import com.foodie.application.dto.ProductDto;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "product")
@Getter
@Setter
@ToString(exclude = "allergens") // Exclude from toString
@EqualsAndHashCode(onlyExplicitlyIncluded = true) // Only use explicitly included fields
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include // Only include ID in equals/hashCode
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

    public ProductDto toDto(){
        return new ProductDto(this);
    }
}