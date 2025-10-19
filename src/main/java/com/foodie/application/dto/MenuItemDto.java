package com.foodie.application.dto;

import com.foodie.application.domain.MenuItem;
import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link MenuItem}
 */
@Value
public class MenuItemDto implements Serializable {
    Integer id;
    Integer productId;
    Integer menuId;
    Boolean featured;
    Integer discountPercentage;
}