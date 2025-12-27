package com.foodie.application.repository;

import com.foodie.application.domain.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface MenuItemRepository extends JpaRepository<MenuItem, Integer> {
    List<MenuItem> findByMenuId(Integer menuId);

    @Modifying
    @Transactional
    @Query("DELETE FROM MenuItem m WHERE m.id = :id")
    void deleteMenuItemById(Integer id);
}