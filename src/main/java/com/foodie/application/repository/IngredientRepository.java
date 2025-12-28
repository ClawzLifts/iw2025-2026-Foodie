package com.foodie.application.repository;

import com.foodie.application.domain.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;

public interface IngredientRepository extends JpaRepository<Ingredient,Integer> {
    Optional<Ingredient> findByName(String name);
    Set<Ingredient> findByNameIn(Set<String> names);
}
