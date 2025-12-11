package com.foodie.application.ui;

import com.foodie.application.domain.Order;
import com.foodie.application.domain.Product;
import com.foodie.application.domain.User;
import com.foodie.application.domain.Menu;
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
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import com.vaadin.flow.component.combobox.ComboBox;

@Route("dashboard")
@PageTitle("Dashboard Manager | Foodie")
@RolesAllowed("MANAGER")
public class DashboardView extends VerticalLayout {

    private final DashboardService dashboardService;
    private final ProductService productService;
    private final UserService userService;

    private final VerticalLayout contentLayout;
    private final Grid<Order> ordersGrid = new Grid<>(Order.class);
    private final Grid<User> userGrid = new Grid<>(User.class, false);

    @Autowired
    public DashboardView(DashboardService dashboardService, ProductService productService, UserService userService) {
        this.dashboardService = dashboardService;
        this.productService = productService;
        this.userService = userService;

        setSizeFull();
        setPadding(true);

        // Título principal
        H2 titulo = new H2("Panel de Manager");
        titulo.getStyle().set("margin-bottom", "20px");
        titulo.getStyle().set("text-align", "center");

        // Crear pestañas
        Tab overviewTab = new Tab("Resumen");
        overviewTab.getStyle().set("cursor", "pointer");
        Tab ordersTab = new Tab("Órdenes");
        ordersTab.getStyle().set("cursor", "pointer");
        Tab productsTab = new Tab("Productos");
        productsTab.getStyle().set("cursor", "pointer");
        Tab menuTab = new Tab("Menú");
        menuTab.getStyle().set("cursor", "pointer");
        Tab usersTab = new Tab("Usuarios");
        usersTab.getStyle().set("cursor", "pointer");
        Tab analyticsTab = new Tab("Análisis de Negocio");
        analyticsTab.getStyle().set("cursor", "pointer");

        Tabs tabs = new Tabs(overviewTab, ordersTab, productsTab, menuTab, usersTab, analyticsTab);
        tabs.setWidthFull();

        // Contenedor para el contenido dinámico
        contentLayout = new VerticalLayout();
        contentLayout.setSizeFull();
        contentLayout.setPadding(false);
        contentLayout.setAlignItems(Alignment.CENTER);

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
        title.getStyle().set("text-align", "center");

        // KPIs principales
        HorizontalLayout kpisLayout = new HorizontalLayout();
        kpisLayout.setWidthFull();
        kpisLayout.setSpacing(true);
        kpisLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        kpisLayout.setAlignItems(Alignment.CENTER);

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
        title.getStyle().set("text-align", "center");

        ComboBox<String> statusFilter = new ComboBox<>("Filtrar por Estado");
        statusFilter.getStyle().set("cursor", "pointer");
        statusFilter.setItems("TODOS", "EN_PROCESO", "COMPLETADO");
        statusFilter.addValueChangeListener(event -> {
            String status = event.getValue();
            ordersGrid.setItems(dashboardService.getOrdersByStatus(status));
        });

        ordersGrid.addColumn(Order::getId).setHeader("ID").setSortable(true);
        ordersGrid.addColumn(order -> order.getUser().getUsername()).setHeader("Usuario");
        ordersGrid.addColumn(Order::getStatus).setHeader("Estado");
        ordersGrid.addColumn(order -> String.format("%.2f €", order.getTotalAmount()))
                .setHeader("Total");
        ordersGrid.addColumn(order -> order.getItems().size()).setHeader("Items");

        // Botón para ver detalles
        ordersGrid.addComponentColumn(order -> {
            Button detailsBtn = new Button("Detalles");
            detailsBtn.getStyle().set("cursor", "pointer");
            detailsBtn.addThemeVariants(ButtonVariant.LUMO_SMALL);
            detailsBtn.addClickListener(e -> showOrderDetails(order));
            return detailsBtn;
        }).setHeader("Acciones");

        List<Order> orders = dashboardService.getAllOrders();
        ordersGrid.setItems(orders);
        ordersGrid.setHeight("500px");
        ordersGrid.setWidthFull();

        VerticalLayout filterLayout = new VerticalLayout(statusFilter);
        filterLayout.setAlignItems(Alignment.CENTER);

        contentLayout.add(title, filterLayout, ordersGrid);
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
        title.getStyle().set("text-align", "center");

        Button addProductBtn = new Button("Añadir Producto");
        addProductBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addProductBtn.getStyle().set("cursor", "pointer");
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
            editBtn.getStyle().set("cursor", "pointer");

            Button deleteBtn = new Button("Eliminar");
            deleteBtn.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_ERROR);
            deleteBtn.getStyle().set("cursor", "pointer");
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
        productsGrid.setWidthFull();

        VerticalLayout buttonWrapper = new VerticalLayout();
        buttonWrapper.setAlignItems(Alignment.CENTER);
        buttonWrapper.add(addProductBtn);

        contentLayout.add(title, buttonWrapper, productsGrid);
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
            try {
                if (nameField.isEmpty() || priceField.isEmpty()) {
                    showNotification("El nombre y el precio son obligatorios.", NotificationVariant.LUMO_ERROR);
                    return;
                }

                Product newProduct = new Product();
                newProduct.setName(nameField.getValue());
                newProduct.setPrice(priceField.getValue());
                newProduct.setDescription(descriptionField.getValue());
                newProduct.setImageUrl(imageUrlField.getValue());

                productService.addProduct(newProduct);
                showNotification("Producto añadido correctamente", NotificationVariant.LUMO_SUCCESS);

                contentLayout.removeAll();
                showProducts();
                dialog.close();
            } catch (Exception ex) {
                showNotification("Error al añadir producto: " + ex.getMessage(), NotificationVariant.LUMO_ERROR);
            }
        });
        saveBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveBtn.getStyle().set("cursor", "pointer");

        Button cancelBtn = new Button("Cancelar", e -> dialog.close());
        cancelBtn.getStyle().set("cursor", "pointer");

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
        TextField emailField = new TextField("Correo electrónico");
        PasswordField passwordField = new PasswordField("Contraseña");

        // Mostramos los roles disponibles en el backend
        ComboBox<String> roleSelector = new ComboBox<>("Rol");
        // Log available roles for debugging
        List<String> roles = userService.getAllRoles();
        System.out.println("Available roles: " + roles);
        roleSelector.setItems(roles);
        roleSelector.setPlaceholder("Selecciona un rol");
        roleSelector.setWidthFull();
        roleSelector.setRequired(true);
        roleSelector.setAllowCustomValue(false);

        formLayout.add(nameField, emailField, passwordField, roleSelector);

        Button saveBtn = new Button("Guardar", e -> {
            try {
                if (roleSelector.getValue() == null) {
                    showNotification("Por favor, selecciona un rol.", NotificationVariant.LUMO_ERROR);
                    return;
                }
                User newUser = userService.registerUser(
                    nameField.getValue(),
                    emailField.getValue(),
                    passwordField.getValue(),
                    roleSelector.getValue()
                );
                showNotification("Usuario '" + newUser.getUsername() + "' creado correctamente", NotificationVariant.LUMO_SUCCESS);
                dialog.close();
            } catch (Exception ex) {
                showNotification("Error al crear usuario: " + ex.getMessage(), NotificationVariant.LUMO_ERROR);
            }
        });
        saveBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveBtn.getStyle().set("cursor", "pointer");

        Button cancelBtn = new Button("Cancelar", e -> dialog.close());
        cancelBtn.getStyle().set("cursor", "pointer");
        HorizontalLayout buttons = new HorizontalLayout(saveBtn, cancelBtn);

        VerticalLayout layout = new VerticalLayout(new H3("Añadir Usuario"), formLayout, buttons);
        layout.getStyle().set("cursor", "pointer");
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
        saveBtn.getStyle().set("cursor", "pointer");

        Button cancelBtn = new Button("Cancelar", e -> dialog.close());
        cancelBtn.getStyle().set("cursor", "pointer");

        HorizontalLayout buttons = new HorizontalLayout(saveBtn, cancelBtn);

        VerticalLayout layout = new VerticalLayout(new H3("Editar Producto"), formLayout, buttons);
        dialog.add(layout);
        dialog.open();
    }

    private void showMenu() {
        H3 title = new H3("Gestión de Menú");
        title.getStyle().set("text-align", "center");

        Span info = new Span("Aquí puedes ver todos los menús disponibles.");
        info.getStyle().set("text-align", "center");

        // Crear grid para mostrar los menús
        Grid<Menu> menuGrid = new Grid<>(Menu.class, false);
        menuGrid.addColumn(Menu::getId).setHeader("ID").setWidth("80px");
        menuGrid.addColumn(Menu::getName).setHeader("Nombre");

        // Agregar listener para abrir lista de comidas al hacer clic en un menú
        menuGrid.addItemClickListener(event -> {
            Menu selectedMenu = event.getItem();
            showMenuItemsDialog(selectedMenu);
        });

        // Obtener menús desde el servicio
        List<Menu> menus = dashboardService.getAllMenusWithItems();
        menuGrid.setItems(menus);
        menuGrid.setWidthFull();

        contentLayout.add(title, info, menuGrid);
    }

    private void showMenuItemsDialog(Menu menu) {
        Dialog dialog = new Dialog();
        dialog.setWidth("600px");

        VerticalLayout layout = new VerticalLayout();
        layout.add(new H3("Comidas en el menú: " + menu.getName()));

        // Volver a cargar el menú desde el servicio para garantizar que esté completamente inicializado
        Menu reloadedMenu = dashboardService.getMenuById(menu.getId());

        try {
            if (reloadedMenu.getMenuItems() != null && !reloadedMenu.getMenuItems().isEmpty()) {
                reloadedMenu.getMenuItems().forEach(item -> {
                    HorizontalLayout itemLayout = new HorizontalLayout();
                    itemLayout.add(new Span(item.getName() + " (" + item.getPrice() + " €)"));

                    Button deleteBtn = new Button("Eliminar", e -> {
                        try {
                            dashboardService.removeMenuItem(menu.getId(), item.getId());
                            showNotification("Producto eliminado del menú", NotificationVariant.LUMO_SUCCESS);
                            dialog.close();
                            showMenuItemsDialog(menu);
                        } catch (Exception ex) {
                            showNotification("Error al eliminar producto: " + ex.getMessage(), NotificationVariant.LUMO_ERROR);
                        }
                    });
                    deleteBtn.addThemeVariants(ButtonVariant.LUMO_ERROR);
                    deleteBtn.getStyle().set("cursor", "pointer");

                    itemLayout.add(deleteBtn);
                    layout.add(itemLayout);
                });
            } else {
                layout.add(new Span("No hay comidas en este menú."));
            }
        } catch (Exception e) {
            layout.add(new Span("Error al cargar las comidas: " + e.getMessage()));
        }

        // Botón para añadir productos al menú
        Button addProductBtn = new Button("Añadir Producto", e -> showAddProductToMenuDialog(menu, dialog));
        addProductBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addProductBtn.getStyle().set("cursor", "pointer");
        layout.add(addProductBtn);

        Button closeBtn = new Button("Cerrar", e -> dialog.close());
        layout.add(closeBtn);
        closeBtn.getStyle().set("cursor", "pointer");

        dialog.add(layout);
        dialog.open();
    }

    private void showAddProductToMenuDialog(Menu menu, Dialog parentDialog) {
        Dialog dialog = new Dialog();
        dialog.setWidth("500px");

        ComboBox<Product> productComboBox = new ComboBox<>("Seleccionar Producto");
        productComboBox.setItems(dashboardService.getAllProducts());
        productComboBox.setItemLabelGenerator(Product::getName);

        Button addBtn = new Button("Añadir", e -> {
            Product selectedProduct = productComboBox.getValue();
            if (selectedProduct != null) {
                try {
                    dashboardService.addMenuItem(menu.getId(), selectedProduct.getId());
                    showNotification("Producto añadido al menú", NotificationVariant.LUMO_SUCCESS);
                    dialog.close();
                    parentDialog.close();
                    showMenuItemsDialog(menu);
                } catch (Exception ex) {
                    showNotification("Error al añadir producto: " + ex.getMessage(), NotificationVariant.LUMO_ERROR);
                }
            } else {
                showNotification("Por favor, selecciona un producto.", NotificationVariant.LUMO_ERROR);
            }
        });
        addBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button cancelBtn = new Button("Cancelar", e -> dialog.close());

        HorizontalLayout buttons = new HorizontalLayout(addBtn, cancelBtn);

        VerticalLayout layout = new VerticalLayout(new H3("Añadir Producto al Menú"), productComboBox, buttons);
        dialog.add(layout);
        dialog.open();
    }

    private void showUsers() {
        H3 title = new H3("Gestión de Usuarios");
        title.getStyle().set("text-align", "center");

        Button addUserButton = new Button("Añadir Usuario");
        addUserButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addUserButton.addClickListener(e -> showAddUserDialog());

        // Grid de usuarios
        userGrid.addColumn(User::getUsername).setHeader("Usuario");
        userGrid.addColumn(User::getEmail).setHeader("Correo Electrónico");
        userGrid.addColumn(User::getRole).setHeader("Rol");
        userGrid.addComponentColumn(user -> {
            HorizontalLayout actions = new HorizontalLayout();

            Button editBtn = new Button("Editar");
            editBtn.addThemeVariants(ButtonVariant.LUMO_SMALL);
            editBtn.addClickListener(e -> showEditUserDialog(user));
            editBtn.getStyle().set("cursor", "pointer");

            Button deleteBtn = new Button("Eliminar");
            deleteBtn.getStyle().set("cursor", "pointer");
            deleteBtn.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_ERROR);
            deleteBtn.addClickListener(e -> {
                try {
                    userService.deleteUser(user.getId());
                    userGrid.setItems(userService.getAllUsers());
                    showNotification("Usuario eliminado correctamente", NotificationVariant.LUMO_SUCCESS);
                } catch (Exception ex) {
                    showNotification("Error al eliminar usuario: " + ex.getMessage(), NotificationVariant.LUMO_ERROR);
                }
            });

            actions.add(editBtn, deleteBtn);
            return actions;
        }).setHeader("Acciones");

        userGrid.setItems(userService.getAllUsers());
        userGrid.setHeight("500px");
        userGrid.setWidthFull();

        VerticalLayout buttonWrapper = new VerticalLayout();
        buttonWrapper.setAlignItems(Alignment.CENTER);
        buttonWrapper.add(addUserButton);

        contentLayout.add(title, buttonWrapper, userGrid);
    }

    private void showEditUserDialog(User user) {
        Dialog dialog = new Dialog();
        dialog.setWidth("500px");

        FormLayout formLayout = new FormLayout();

        TextField nameField = new TextField("Nombre");
        nameField.setValue(user.getUsername());

        TextField emailField = new TextField("Correo electrónico");
        emailField.setValue(user.getEmail());

        formLayout.add(nameField, emailField);

        Button saveBtn = new Button("Guardar", e -> {
            try {
                userService.updateUser(user.getId(), nameField.getValue(), emailField.getValue());
                showNotification("Usuario actualizado correctamente", NotificationVariant.LUMO_SUCCESS);
                dialog.close();
                userGrid.setItems(userService.getAllUsers());
            } catch (Exception ex) {
                showNotification("Error al actualizar usuario: " + ex.getMessage(), NotificationVariant.LUMO_ERROR);
            }
        });
        saveBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveBtn.getStyle().set("cursor", "pointer");

        Button cancelBtn = new Button("Cancelar", e -> dialog.close());
        cancelBtn.getStyle().set("cursor", "pointer");

        HorizontalLayout buttons = new HorizontalLayout(saveBtn, cancelBtn);

        VerticalLayout layout = new VerticalLayout(new H3("Editar Usuario"), formLayout, buttons);
        dialog.add(layout);
        dialog.open();
    }

    private void showAnalytics() {
        H3 title = new H3("Análisis de Negocio");
        title.getStyle().set("text-align", "center");

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
        analyticsKpis.setJustifyContentMode(JustifyContentMode.CENTER);
        analyticsKpis.setAlignItems(Alignment.CENTER);

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
                .set("margin-top", "20px")
                .set("text-align", "center");

        contentLayout.add(title, analyticsKpis, premiumInfo);
    }

    private void showNotification(String message, NotificationVariant variant) {
        Notification notification = new Notification(message, 3000);
        notification.addThemeVariants(variant);
        notification.setPosition(Notification.Position.TOP_CENTER);
        notification.open();
    }
}
