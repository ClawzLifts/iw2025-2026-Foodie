package com.foodie.application.ui;

import com.foodie.application.domain.Product;
import com.foodie.application.service.CartaService;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;

@Route("carta")
@PageTitle("Carta | Foodie")
@RolesAllowed("USER")
public class CartaView extends VerticalLayout {

    private final CartaService cartaService;

    @Autowired
    public CartaView(CartaService cartaService) {
        this.cartaService = cartaService;

        setSizeFull();
        setAlignItems(Alignment.CENTER);

        Grid<Product> grid = new Grid<>(Product.class); // Ajusta las columnas según tu modelo
        grid.setItems(cartaService.getAllProductos());
        grid.setColumns("id", "name", "price", "allergens"); // Ajusta según tus campos reales

        add(grid);
    }
}
