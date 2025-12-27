package com.foodie.application.dto;

import com.foodie.application.domain.Order;
import com.foodie.application.domain.OrderStatus;
import com.foodie.application.domain.ProductList;
import lombok.Value;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO for {@link Order}
 */
@Value
public class OrderDto implements Serializable {
    Integer id;
    UserEntityDto user;
    List<List<ProductList>> items;
    LocalDate date;
    OrderStatus status;

    public OrderDto(Order order){
        this.id = order.getId();
        this.user = new UserEntityDto(order.getUser());
        this.items = List.of(order.getItems());
        this.date = order.getDate();
        this.status = order.getStatus();
    }
}