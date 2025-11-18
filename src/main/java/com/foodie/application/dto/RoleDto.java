package com.foodie.application.dto;

import com.foodie.application.domain.Role;
import java.io.Serializable;

/**
 * DTO for {@link Role}
 */
@lombok.Value
public class RoleDto implements Serializable {
    Integer id;
    String name;

    public RoleDto(Role role) {
        this.id = role.getId();
        this.name = role.getName();
    }
}
