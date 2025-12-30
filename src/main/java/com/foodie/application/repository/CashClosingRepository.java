package com.foodie.application.repository;

import com.foodie.application.domain.CashClosing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository for CashClosing entity
 * Provides database access methods for cash closing operations
 */
@Repository
public interface CashClosingRepository extends JpaRepository<CashClosing, Integer> {
    /**
     * Find the most recent cash closing by date
     */
    @Query(value = "SELECT * FROM cash_closings WHERE date = ?1 ORDER BY id DESC LIMIT 1", nativeQuery = true)
    Optional<CashClosing> findLatestByDate(LocalDate date);

    /**
     * Find all closed cash closings
     */
    List<CashClosing> findByIsClosedTrue();

    /**
     * Find all open cash closings
     */
    List<CashClosing> findByIsClosedFalse();

    /**
     * Find cash closings within a date range
     */
    List<CashClosing> findByDateBetween(LocalDate startDate, LocalDate endDate);
}

