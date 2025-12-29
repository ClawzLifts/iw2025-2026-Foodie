package com.foodie.application.domain;

import com.foodie.application.dto.MenuDto;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "menu")
public class Menu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "menu", fetch = FetchType.EAGER)
    private List<MenuItem> menuItems;

    public MenuDto toDto(){
        return new MenuDto(this);
    }
}