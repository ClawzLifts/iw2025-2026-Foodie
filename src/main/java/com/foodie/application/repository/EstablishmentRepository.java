package com.foodie.application.repository;

import com.foodie.application.domain.Establishment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EstablishmentRepository extends JpaRepository<Establishment, Integer> {
}
