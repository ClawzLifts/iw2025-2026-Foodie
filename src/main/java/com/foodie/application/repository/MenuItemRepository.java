package com.foodie.application.repository;

import com.foodie.application.domain.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MenuItemRepository extends JpaRepository<MenuItem, Integer> {
    List<MenuItem> findByMenuId(Integer menuId);
}