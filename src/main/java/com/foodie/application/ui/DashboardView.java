package com.foodie.application.ui;

import com.foodie.application.domain.Order;
import com.foodie.application.domain.Product;
import com.foodie.application.domain.User;
import com.foodie.application.service.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import com.vaadin.flow.component.UI;

import java.util.List;

@Route("dashboard")
@PageTitle("Dashboard Manager | Foodie")
@RolesAllowed("MANAGER")
public class DashboardView extends VerticalLayout {

    private final DashboardService dashboardService;
    private final ProductService productService;
    private final OrderService orderService;
    private final MenuService menuService;
    private final UserService userService;

    private final VerticalLayout contentLayout;

    @Autowired
    public DashboardView(DashboardService dashboardService, ProductService productService,
                         OrderService orderService, MenuService menuService, UserService userService) {
        this.dashboardService = dashboardService;
        this.productService = productService;
        this.orderService = orderService;
        this.menuService = menuService;
        this.userService = userService;

        setSizeFull();
        setPadding(true);

        // Título principal
        H2 titulo = new H2("Panel de Manager");
        titulo.getStyle().set("margin-bottom", "20px");

        // Crear pestañas
        Tab overviewTab = new Tab("Resumen");
        Tab ordersTab = new Tab("Órdenes");
        Tab productsTab = new Tab("Productos");
        Tab menuTab = new Tab("Menú");
        Tab usersTab = new Tab("Usuarios");
        Tab analyticsTab = new Tab("Análisis de Negocio");

        Tabs tabs = new Tabs(overviewTab, ordersTab, productsTab, menuTab, usersTab, analyticsTab);
        tabs.setWidthFull();

        // Contenedor para el contenido dinámico
        contentLayout = new VerticalLayout();
        contentLayout.setSizeFull();
        contentLayout.setPadding(false);

        // Mostrar vista inicial
        showOverview();

        // Listener para cambio de pestañas
        tabs.addSelectedChangeListener(event -> {
            Tab selectedTab = event.getSelectedTab();
            contentLayout.removeAll();

            if (selectedTab == overviewTab) {
                showOverview();
            } else if (selectedTab == ordersTab) {
                showOrders();
            } else if (selectedTab == productsTab) {
                showProducts();
            } else if (selectedTab == menuTab) {
                showMenu();
            } else if (selectedTab == usersTab) {
                showUsers();
            } else if (selectedTab == analyticsTab) {
                showAnalytics();
            }
        });

        add(titulo, tabs, contentLayout);
    }

    private void showOverview() {
        H3 title = new H3("Resumen General");

        // KPIs principales
        HorizontalLayout kpisLayout = new HorizontalLayout();
        kpisLayout.setWidthFull();
        kpisLayout.setSpacing(true);

        VerticalLayout productsKpi = createKpiCard("Productos Totales",
                String.valueOf(dashboardService.countProducts()), "📦");
        VerticalLayout usersKpi = createKpiCard("Usuarios Totales",
                String.valueOf(dashboardService.countUsers()), "👥");
        VerticalLayout ordersKpi = createKpiCard("Órdenes Totales",
                String.valueOf(dashboardService.getAllOrders().size()), "🛒");

        kpisLayout.add(productsKpi, usersKpi, ordersKpi);

        contentLayout.add(title, kpisLayout);
    }

    private VerticalLayout createKpiCard(String label, String value, String icon) {
        VerticalLayout card = new VerticalLayout();
        card.setWidth("300px");
        card.getStyle()
                .set("border", "1px solid #e0e0e0")
                .set("border-radius", "8px")
                .set("padding", "20px")
                .set("background-color", "#f5f5f5");

        Span iconSpan = new Span(icon);
        iconSpan.getStyle().set("font-size", "40px");

        Span valueSpan = new Span(value);
        valueSpan.getStyle().set("font-size", "32px").set("font-weight", "bold");

        Span labelSpan = new Span(label);
        labelSpan.getStyle().set("color", "#666");

        card.add(iconSpan, valueSpan, labelSpan);
        card.setAlignItems(Alignment.CENTER);

        return card;
    }

    private void showOrders() {
        H3 title = new H3("Gestión de Órdenes");

        // Crear grid de órdenes
        Grid<Order> ordersGrid = new Grid<>(Order.class, false);
        ordersGrid.addColumn(Order::getId).setHeader("ID").setSortable(true);
        ordersGrid.addColumn(order -> order.getUser().getUsername()).setHeader("Usuario");
        ordersGrid.addColumn(Order::getStatus).setHeader("Estado");
        ordersGrid.addColumn(order -> String.format("%.2f €", order.getTotalAmount()))
                .setHeader("Total");
        ordersGrid.addColumn(order -> order.getItems().size()).setHeader("Items");

        // Botón para ver detalles
        ordersGrid.addComponentColumn(order -> {
            Button detailsBtn = new Button("Detalles");
            detailsBtn.addThemeVariants(ButtonVariant.LUMO_SMALL);
            detailsBtn.addClickListener(e -> showOrderDetails(order));
            return detailsBtn;
        }).setHeader("Acciones");

        List<Order> orders = dashboardService.getAllOrders();
        ordersGrid.setItems(orders);
        ordersGrid.setHeight("500px");

        contentLayout.add(title, ordersGrid);
    }

    private void showOrderDetails(Order order) {
        Dialog dialog = new Dialog();
        dialog.setWidth("600px");

        VerticalLayout layout = new VerticalLayout();
        layout.add(new H3("Detalles de la Orden #" + order.getId()));
        layout.add(new Span("Usuario: " + order.getUser().getUsername()));
        layout.add(new Span("Estado: " + order.getStatus()));
        layout.add(new Span("Total: " + String.format("%.2f €", order.getTotalAmount())));

        // Lista de productos
        H3 itemsTitle = new H3("Productos:");
        layout.add(itemsTitle);

        order.getItems().forEach(item ->
                layout.add(new Span("- " + item.getProductName() +
                        " (x" + item.getQuantity() + ") - " +
                        String.format("%.2f €", item.getPrice()))));

        Button closeBtn = new Button("Cerrar", e -> dialog.close());
        layout.add(closeBtn);

        dialog.add(layout);
        dialog.open();
    }

    private void showProducts() {
        H3 title = new H3("Gestión de Productos");

        Button addProductBtn = new Button("Añadir Producto");
        addProductBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addProductBtn.addClickListener(e -> showAddProductDialog());

        // Grid de productos
        Grid<Product> productsGrid = new Grid<>(Product.class, false);
        productsGrid.addColumn(Product::getId).setHeader("ID").setWidth("80px");
        productsGrid.addColumn(Product::getName).setHeader("Nombre");
        productsGrid.addColumn(product -> String.format("%.2f €", product.getPrice()))
                .setHeader("Precio");
        productsGrid.addColumn(Product::getDescription).setHeader("Descripción");

        productsGrid.addComponentColumn(product -> {
            HorizontalLayout actions = new HorizontalLayout();

            Button editBtn = new Button("Editar");
            editBtn.addThemeVariants(ButtonVariant.LUMO_SMALL);
            editBtn.addClickListener(e -> showEditProductDialog(product, productsGrid));

            Button deleteBtn = new Button("Eliminar");
            deleteBtn.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_ERROR);
            deleteBtn.addClickListener(e -> {
                try {
                    productService.removeProduct(product.getId());
                    productsGrid.setItems(dashboardService.getAllProducts());
                    showNotification("Producto eliminado correctamente", NotificationVariant.LUMO_SUCCESS);
                } catch (Exception ex) {
                    showNotification("Error al eliminar producto: " + ex.getMessage(), NotificationVariant.LUMO_ERROR);
                }
            });

            actions.add(editBtn, deleteBtn);
            return actions;
        }).setHeader("Acciones");

        productsGrid.setItems(dashboardService.getAllProducts());
        productsGrid.setHeight("500px");

        contentLayout.add(title, addProductBtn, productsGrid);
    }

    private void showAddProductDialog() {
        Dialog dialog = new Dialog();
        dialog.setWidth("500px");

        FormLayout formLayout = new FormLayout();

        TextField nameField = new TextField("Nombre");
        NumberField priceField = new NumberField("Precio (€)");
        TextArea descriptionField = new TextArea("Descripción");
        TextField imageUrlField = new TextField("URL Imagen");

        formLayout.add(nameField, priceField, descriptionField, imageUrlField);

        Button saveBtn = new Button("Guardar", e -> {
            // Aquí deberías crear el producto
            showNotification("Funcionalidad de añadir producto pendiente de implementar", NotificationVariant.LUMO_CONTRAST);
            dialog.close();
        });
        saveBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button cancelBtn = new Button("Cancelar", e -> dialog.close());

        HorizontalLayout buttons = new HorizontalLayout(saveBtn, cancelBtn);

        VerticalLayout layout = new VerticalLayout(new H3("Añadir Producto"), formLayout, buttons);
        dialog.add(layout);
        dialog.open();
    }

    private void showAddUserDialog() {
        Dialog dialog = new Dialog();
        dialog.setWidth("500px");

        FormLayout formLayout = new FormLayout();

        TextField nameField = new TextField("Nombre");
        TextArea emailField = new TextArea("Correo electrónico");
        TextField passwordField = new TextField("Contraseña");
        TextField rolField = new TextField("Rol");

        formLayout.add(nameField, emailField, passwordField, rolField);

        Button saveBtn = new Button("Guardar", e -> {
            try {
                User newUser = userService.registerUserByManager(
                    nameField.getValue(),
                    passwordField.getValue(),
                    rolField.getValue(),
                    emailField.getValue()
                );
                showNotification("Usuario '" + newUser.getUsername() + "' creado correctamente", NotificationVariant.LUMO_SUCCESS);
                dialog.close();
            } catch (Exception ex) {
                showNotification("Error al crear usuario: " + ex.getMessage(), NotificationVariant.LUMO_ERROR);
            }
        });
        saveBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button cancelBtn = new Button("Cancelar", e -> dialog.close());

        HorizontalLayout buttons = new HorizontalLayout(saveBtn, cancelBtn);

        VerticalLayout layout = new VerticalLayout(new H3("Añadir Usuario"), formLayout, buttons);
        dialog.add(layout);
        dialog.open();
    }


    private void showEditProductDialog(Product product, Grid<Product> grid) {
        Dialog dialog = new Dialog();
        dialog.setWidth("500px");

        FormLayout formLayout = new FormLayout();

        TextField nameField = new TextField("Nombre");
        nameField.setValue(product.getName());

        NumberField priceField = new NumberField("Precio (€)");
        priceField.setValue(product.getPrice());

        TextArea descriptionField = new TextArea("Descripción");
        descriptionField.setValue(product.getDescription() != null ? product.getDescription() : "");

        formLayout.add(nameField, priceField, descriptionField);

        Button saveBtn = new Button("Guardar", e -> {
            try {
                productService.updateProductName(product.getId(), nameField.getValue());
                productService.updateProductPrice(product.getId(), priceField.getValue());
                productService.updateProductDescription(product.getId(), descriptionField.getValue());

                grid.setItems(dashboardService.getAllProducts());
                showNotification("Producto actualizado correctamente", NotificationVariant.LUMO_SUCCESS);
                dialog.close();
            } catch (Exception ex) {
                showNotification("Error al actualizar producto: " + ex.getMessage(), NotificationVariant.LUMO_ERROR);
            }
        });
        saveBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button cancelBtn = new Button("Cancelar", e -> dialog.close());

        HorizontalLayout buttons = new HorizontalLayout(saveBtn, cancelBtn);

        VerticalLayout layout = new VerticalLayout(new H3("Editar Producto"), formLayout, buttons);
        dialog.add(layout);
        dialog.open();
    }

    private void showMenu() {
        H3 title = new H3("Gestión de Menú");
        Span info = new Span("Aquí puedes añadir y eliminar productos de la carta del restaurante.");

        contentLayout.add(title, info);
    }

    private void showUsers() {
        H3 title = new H3("Gestión de Usuarios");

        Button addUserButton = new Button("Añadir Usuario");
        addUserButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addUserButton.addClickListener(e -> showAddUserDialog());

        // Grid de usuarios
        Grid<User> userGrid = new Grid<>(User.class, false);
        userGrid.addColumn(User::getUsername).setHeader("Usuario");
        userGrid.addColumn(User::getEmail).setHeader("Correo Electrónico");
        userGrid.addColumn(User::getRole).setHeader("Rol");
        userGrid.addComponentColumn(user -> {
            HorizontalLayout actions = new HorizontalLayout();

            Button editBtn = new Button("Editar");
            editBtn.addThemeVariants(ButtonVariant.LUMO_SMALL);
            editBtn.addClickListener(e -> showNotification("Funcionalidad de editar usuario pendiente", NotificationVariant.LUMO_CONTRAST));

            Button deleteBtn = new Button("Eliminar");
            deleteBtn.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_ERROR);
            deleteBtn.addClickListener(e -> showNotification("Funcionalidad de eliminar usuario pendiente", NotificationVariant.LUMO_CONTRAST));

            actions.add(editBtn, deleteBtn);
            return actions;
        }).setHeader("Acciones");

        userGrid.setItems(userService.getAllUsers());
        userGrid.setHeight("500px");

        contentLayout.add(title, addUserButton, userGrid);
    }

    private void showAnalytics() {
        H3 title = new H3("Análisis de Negocio");

        List<Order> orders = dashboardService.getAllOrders();

        // Calcular métricas
        double totalRevenue = orders.stream()
                .mapToDouble(Order::getTotalAmount)
                .sum();

        long completedOrders = orders.stream()
                .filter(o -> "COMPLETED".equalsIgnoreCase(o.getStatus()))
                .count();

        // KPIs de análisis
        HorizontalLayout analyticsKpis = new HorizontalLayout();
        analyticsKpis.setWidthFull();

        VerticalLayout revenueKpi = createKpiCard("Ingresos Totales",
                String.format("%.2f €", totalRevenue), "💰");
        VerticalLayout completedKpi = createKpiCard("Órdenes Completadas",
                String.valueOf(completedOrders), "✅");
        VerticalLayout avgKpi = createKpiCard("Ticket Promedio",
                orders.isEmpty() ? "0.00 €" : String.format("%.2f €", totalRevenue / orders.size()), "📊");

        analyticsKpis.add(revenueKpi, completedKpi, avgKpi);

        Span premiumInfo = new Span(" Análisis avanzado y gráficas disponibles en versión PREMIUM");
        premiumInfo.getStyle()
                .set("color", "#ff9800")
                .set("font-weight", "bold")
                .set("font-size", "16px")
                .set("margin-top", "20px");

        contentLayout.add(title, analyticsKpis, premiumInfo);
    }

    private void showNotification(String message, NotificationVariant variant) {
        Notification notification = new Notification(message, 3000);
        notification.addThemeVariants(variant);
        notification.setPosition(Notification.Position.TOP_CENTER);
        notification.open();
    }
}
