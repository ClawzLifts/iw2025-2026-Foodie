package com.foodie.application.repository;

import com.foodie.application.domain.Allergen;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface AllergenRepository extends JpaRepository<Allergen, Integer> {
    Set<Allergen> findByNameIn(Set<String> names);
}
