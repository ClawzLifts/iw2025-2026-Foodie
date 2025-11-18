package com.foodie.application.repository;

import com.foodie.application.domain.Allergen;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AllergenRepository extends JpaRepository<Allergen, Integer> {
}
