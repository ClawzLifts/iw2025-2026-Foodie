package com.foodie.application.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;
import java.util.Set;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Check;

/**
 * Represents a user entity in the Foodie application.
 * This class maps to the "user" table in the database and contains
 * all user-related information including personal details, authentication,
 * and order history.
 *
 * @author Jesus Rodriguez
 * @version 1.0
 * @since 2025
 */
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "users")
@Check(constraints = "role IN ('USUARIO', 'MANAGER')")
public class User {

    @Id
    @Column(unique = true , nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true)
    private String username;
    private String password;
    @Column(unique = true)
    private String email;
    private String profilePictureUrl;
    private Long phoneNumber;
    private String address;
    private String fullName;
    private Set<String> allergies;

    private String role;

    @JsonIgnore
    @OneToMany
    private List<Order> orders;
}
