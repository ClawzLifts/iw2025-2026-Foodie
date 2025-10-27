package com.foodie.application.ui;

import com.foodie.application.service.DashboardService;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;

@Route("dashboard")
@PageTitle("Dashboard | Foodie")
@RolesAllowed("MANAGER")
public class DashboardView extends VerticalLayout {

    private final DashboardService dashboardService;

    @Autowired
    public DashboardView(DashboardService dashboardService) {
        this.dashboardService = dashboardService;

        setSizeFull();
        setAlignItems(Alignment.CENTER);

        H2 titulo = new H2("Panel de Manager");
        H2 productos = new H2("Total productos: " + dashboardService.countProducts());
        H2 usuarios = new H2("Total usuarios: " + dashboardService.countUsers());

        add(titulo, productos, usuarios);

        // Puedes añadir tablas, estadísticas, grids de pedidos, etc.
    }
}
