package com.foodie.application.dto;

import com.foodie.application.domain.Menu;
import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link Menu}
 */
@Value
public class MenuDto implements Serializable {
    Integer id;
    String name;

    public MenuDto(Menu menu){
        this.id = menu.getId();
        this.name = menu.getName();
    }
}