package com.foodie.application.ui.views;

import com.foodie.application.domain.*;
import com.foodie.application.dto.MenuDto;
import com.foodie.application.service.MenuItemService;
import com.foodie.application.service.MenuService;
import com.foodie.application.service.OrderService;
import com.foodie.application.service.ProductService;
import com.foodie.application.ui.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import java.util.*;

/**
 * Admin Panel View for managing menus and orders.
 * Provides functionality to view, create, edit, and delete menus,
 * as well as manage orders and their statuses.
 *
 * @author Foodie Admin Team
 * @version 1.0
 * @since 2025
 */
@Route(value = "admin", layout = MainLayout.class)
@PageTitle("Panel de Administración | Foodie")
public class AdminPanelView extends VerticalLayout {

    private final MenuService menuService;
    private final MenuItemService menuItemService;
    private final ProductService productService;
    private final OrderService orderService;

    private Grid<MenuDto> menusGrid;
    private Grid<Order> ordersGrid;
    private VerticalLayout contentContainer;

    public AdminPanelView(MenuService menuService, MenuItemService menuItemService,
                         ProductService productService, OrderService orderService) {
        this.menuService = menuService;
        this.menuItemService = menuItemService;
        this.productService = productService;
        this.orderService = orderService;

        addClassName("admin-panel-view");
        setSizeFull();
        setPadding(true);
        setSpacing(true);

        add(createHeader());
        add(createTabNavigation());

        // Crear el contenedor de contenido
        contentContainer = new VerticalLayout();
        contentContainer.setPadding(false);
        contentContainer.setSpacing(true);
        contentContainer.setWidthFull();
        contentContainer.setFlexGrow(1, contentContainer);
        add(contentContainer);

        // Mostrar el contenido inicial (Menús)
        showMenusTab();
    }

    /**
     * Creates the header section with title
     */
    private HorizontalLayout createHeader() {
        H1 title = new H1("Panel de Administración");
        title.addClassNames(
                LumoUtility.Margin.Top.NONE,
                LumoUtility.TextColor.PRIMARY,
                LumoUtility.FontSize.XLARGE
        );

        HorizontalLayout header = new HorizontalLayout(title);
        header.setWidthFull();
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);

        return header;
    }

    /**
     * Creates the tabs navigation for switching between sections
     */
    private Tabs createTabNavigation() {
        Tab menusTab = new Tab();
        menusTab.add(new Icon(VaadinIcon.MENU), new Span("Menús"));

        Tab ordersTab = new Tab();
        ordersTab.add(new Icon(VaadinIcon.PACKAGE), new Span("Pedidos"));

        Tabs tabs = new Tabs(menusTab, ordersTab);
        tabs.addClassNames(
                LumoUtility.BorderRadius.MEDIUM,
                LumoUtility.Background.BASE,
                LumoUtility.BoxShadow.SMALL
        );

        tabs.addSelectedChangeListener(event -> {
            Tab selectedTab = event.getSelectedTab();
            if (selectedTab == menusTab) {
                showMenusTab();
            } else if (selectedTab == ordersTab) {
                showOrdersTab();
            }
        });

        return tabs;
    }


    /**
     * Displays the menus management tab
     */
    private void showMenusTab() {
        contentContainer.removeAll();

        VerticalLayout content = new VerticalLayout();
        content.setPadding(false);
        content.setSpacing(true);
        content.setWidthFull();
        content.setFlexGrow(1, content);

        // Header with add button
        HorizontalLayout headerLayout = new HorizontalLayout();
        H2 title = new H2("Gestión de Menús");
        title.addClassNames(LumoUtility.Margin.Top.NONE);

        Button addMenuBtn = new Button("Crear Nuevo Menú", new Icon(VaadinIcon.PLUS));
        addMenuBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addMenuBtn.addClickListener(e -> openAddMenuDialog());

        headerLayout.add(title);
        headerLayout.expand(title);
        headerLayout.add(addMenuBtn);
        headerLayout.setWidthFull();
        headerLayout.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);

        content.add(headerLayout);

        // Menus Grid
        menusGrid = new Grid<>(MenuDto.class, false);
        menusGrid.setWidthFull();
        menusGrid.setHeightFull();
        menusGrid.addThemeVariants(GridVariant.LUMO_COMPACT);
        menusGrid.setSelectionMode(Grid.SelectionMode.NONE);

        menusGrid.addColumn(MenuDto::getId).setHeader("ID").setWidth("80px");
        menusGrid.addColumn(MenuDto::getName).setHeader("Nombre");

        menusGrid.addComponentColumn(menu -> {
            HorizontalLayout actions = new HorizontalLayout();
            actions.setSpacing(true);

            Button editBtn = new Button(new Icon(VaadinIcon.EDIT));
            editBtn.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_CONTRAST);
            editBtn.addClickListener(e -> openEditMenuDialog(menu));

            Button deleteBtn = new Button(new Icon(VaadinIcon.TRASH));
            deleteBtn.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_ERROR);
            deleteBtn.addClickListener(e -> openDeleteMenuDialog(menu));

            Button itemsBtn = new Button("Ver Ítems", new Icon(VaadinIcon.LIST));
            itemsBtn.addThemeVariants(ButtonVariant.LUMO_SMALL);
            itemsBtn.addClickListener(e -> openMenuItemsDialog(menu));

            actions.add(itemsBtn, editBtn, deleteBtn);
            return actions;
        }).setHeader("Acciones").setWidth("300px");

        content.add(menusGrid);

        loadMenus();

        contentContainer.add(content);
    }

    /**
     * Displays the orders management tab
     */
    private void showOrdersTab() {
        contentContainer.removeAll();

        VerticalLayout content = new VerticalLayout();
        content.setPadding(false);
        content.setSpacing(true);
        content.setWidthFull();
        content.setFlexGrow(1, content);

        H2 title = new H2("Gestión de Pedidos");
        title.addClassNames(LumoUtility.Margin.Top.NONE);
        content.add(title);

        // Orders Grid
        ordersGrid = new Grid<>(Order.class, false);
        ordersGrid.setWidthFull();
        ordersGrid.setHeightFull();
        ordersGrid.addThemeVariants(GridVariant.LUMO_COMPACT);
        ordersGrid.setSelectionMode(Grid.SelectionMode.NONE);

        ordersGrid.addColumn(Order::getId).setHeader("ID").setWidth("80px");
        ordersGrid.addColumn(order -> order.getUser().getUsername()).setHeader("Usuario");
        ordersGrid.addColumn(Order::getDate).setHeader("Fecha");
        ordersGrid.addColumn(order -> order.getStatus().toString()).setHeader("Estado");

        ordersGrid.addComponentColumn(order -> {
            HorizontalLayout statusLayout = new HorizontalLayout();
            statusLayout.setSpacing(true);

            com.vaadin.flow.component.select.Select<OrderStatus> statusSelect =
                    new com.vaadin.flow.component.select.Select<>();
            statusSelect.setItems(OrderStatus.values());
            statusSelect.setValue(order.getStatus());
            statusSelect.setWidth("150px");

            Button updateBtn = new Button("Actualizar", new Icon(VaadinIcon.CHECK));
            updateBtn.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_PRIMARY);
            updateBtn.addClickListener(e -> {
                orderService.updateOrder(order.getId(), statusSelect.getValue());
                Notification.show("Pedido actualizado", 3000, Notification.Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                loadOrders();
            });

            Button viewBtn = new Button("Ver Detalles", new Icon(VaadinIcon.EYE));
            viewBtn.addThemeVariants(ButtonVariant.LUMO_SMALL);
            viewBtn.addClickListener(e -> openOrderDetailsDialog(order));

            statusLayout.add(statusSelect, updateBtn, viewBtn);
            return statusLayout;
        }).setHeader("Acciones").setWidth("400px");

        content.add(ordersGrid);

        loadOrders();

        contentContainer.add(content);
    }

    /**
     * Opens dialog to add a new menu
     */
    private void openAddMenuDialog() {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Crear Nuevo Menú");

        VerticalLayout content = new VerticalLayout();
        content.setSpacing(true);

        TextField menuNameField = new TextField("Nombre del Menú");
        menuNameField.setWidthFull();
        menuNameField.setRequired(true);

        content.add(menuNameField);

        HorizontalLayout buttonLayout = new HorizontalLayout();
        Button saveBtn = new Button("Guardar", e -> {
            if (menuNameField.getValue().isEmpty()) {
                Notification.show("Por favor ingresa un nombre", 3000, Notification.Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }
            menuService.addMenu(menuNameField.getValue(), new ArrayList<>());
            Notification.show("Menú creado exitosamente", 3000, Notification.Position.TOP_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            loadMenus();
            dialog.close();
        });
        saveBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button cancelBtn = new Button("Cancelar", e -> dialog.close());

        buttonLayout.add(saveBtn, cancelBtn);
        buttonLayout.setSpacing(true);

        dialog.add(content);
        dialog.getFooter().add(buttonLayout);
        dialog.open();
    }

    /**
     * Opens dialog to edit a menu
     */
    private void openEditMenuDialog(MenuDto menu) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Editar Menú");

        VerticalLayout content = new VerticalLayout();
        content.setSpacing(true);

        TextField menuNameField = new TextField("Nombre del Menú");
        menuNameField.setValue(menu.getName());
        menuNameField.setWidthFull();
        menuNameField.setRequired(true);

        content.add(menuNameField);

        HorizontalLayout buttonLayout = new HorizontalLayout();
        Button saveBtn = new Button("Guardar", e -> {
            if (menuNameField.getValue().isEmpty()) {
                Notification.show("Por favor ingresa un nombre", 3000, Notification.Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }
            menuService.updateMenuName(menu.getId(), menuNameField.getValue());
            Notification.show("Menú actualizado exitosamente", 3000, Notification.Position.TOP_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            loadMenus();
            dialog.close();
        });
        saveBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button cancelBtn = new Button("Cancelar", e -> dialog.close());

        buttonLayout.add(saveBtn, cancelBtn);
        buttonLayout.setSpacing(true);

        dialog.add(content);
        dialog.getFooter().add(buttonLayout);
        dialog.open();
    }

    /**
     * Opens dialog to confirm menu deletion
     */
    private void openDeleteMenuDialog(MenuDto menu) {
        ConfirmDialog dialog = new ConfirmDialog();
        dialog.setHeader("Confirmar eliminación");
        dialog.setText("¿Estás seguro de que deseas eliminar el menú '" + menu.getName() + "'?");
        dialog.setConfirmText("Eliminar");
        dialog.setCancelText("Cancelar");
        dialog.setConfirmButtonTheme("error primary");

        dialog.addConfirmListener(e -> {
            menuService.deleteMenu(menu.getId());
            Notification.show("Menú eliminado exitosamente", 3000, Notification.Position.TOP_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            loadMenus();
        });

        dialog.open();
    }

    /**
     * Opens dialog to view and manage menu items
     */
    private void openMenuItemsDialog(MenuDto menu) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Ítems del Menú: " + menu.getName());
        dialog.setWidth("800px");
        dialog.setHeight("600px");

        VerticalLayout content = new VerticalLayout();
        content.setPadding(true);
        content.setSpacing(true);
        content.setWidthFull();
        content.setHeightFull();

        // Header with add button
        HorizontalLayout headerLayout = new HorizontalLayout();
        Button addItemBtn = new Button("Agregar Ítem", new Icon(VaadinIcon.PLUS));
        addItemBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        // Items Grid
        Grid<MenuItem> itemsGrid = new Grid<>(MenuItem.class, false);
        itemsGrid.setWidthFull();
        itemsGrid.addThemeVariants(GridVariant.LUMO_COMPACT);
        itemsGrid.setSelectionMode(Grid.SelectionMode.NONE);

        itemsGrid.addColumn(item -> item.getProduct().getName()).setHeader("Producto");
        itemsGrid.addColumn(item -> item.getProduct().getPrice()).setHeader("Precio");
        itemsGrid.addColumn(MenuItem::getFeatured).setHeader("Destacado");
        itemsGrid.addColumn(MenuItem::getDiscountPercentage).setHeader("Descuento %");

        itemsGrid.addComponentColumn(item -> {
            HorizontalLayout actions = new HorizontalLayout();
            actions.setSpacing(true);

            Button editBtn = new Button(new Icon(VaadinIcon.EDIT));
            editBtn.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_CONTRAST);
            editBtn.addClickListener(e -> openEditMenuItemDialog(menu, item, itemsGrid));

            Button removeBtn = new Button(new Icon(VaadinIcon.TRASH));
            removeBtn.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_ERROR);
            removeBtn.addClickListener(e -> {
                menuItemService.deleteMenuItem(menu.getId(), item.getProduct().getId());
                Notification.show("Ítem eliminado", 3000, Notification.Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                loadMenuItemsInDialog(itemsGrid, menu);
            });

            actions.add(editBtn, removeBtn);
            return actions;
        }).setHeader("Acciones").setWidth("150px");

        // Ahora agregamos el listener del botón después de crear la grid
        addItemBtn.addClickListener(e -> openAddMenuItemDialog(menu, itemsGrid));

        headerLayout.add(addItemBtn);
        headerLayout.setWidthFull();

        content.add(headerLayout);
        content.add(itemsGrid);
        content.setFlexGrow(1, itemsGrid);

        loadMenuItemsInDialog(itemsGrid, menu);

        dialog.add(content);
        dialog.open();
    }

    /**
     * Loads menu items into the grid
     */
    private void loadMenuItemsInDialog(Grid<MenuItem> grid, MenuDto menu) {
        Menu fullMenu = new Menu();
        fullMenu.setId(menu.getId());
        fullMenu.setName(menu.getName());

        List<MenuItem> items = menuItemService.getMenuItems(menu.getId());
        grid.setItems(items);
    }

    /**
     * Opens dialog to add a new menu item
     */
    private void openAddMenuItemDialog(MenuDto menu, Grid<MenuItem> parentGrid) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Agregar Ítem al Menú");

        VerticalLayout content = new VerticalLayout();
        content.setSpacing(true);

        com.vaadin.flow.component.select.Select<Product> productSelect =
                new com.vaadin.flow.component.select.Select<>();
        productSelect.setLabel("Producto");
        productSelect.setWidthFull();

        List<Product> products = productService.getAllProducts(menu.getId());
        productSelect.setItems(products);
        productSelect.setItemLabelGenerator(Product::getName);

        Checkbox featuredCheckbox = new Checkbox("Producto Destacado");
        NumberField discountField = new NumberField("Descuento (%)");
        discountField.setMin(0);
        discountField.setMax(100);
        discountField.setValue(0.0);

        content.add(productSelect, featuredCheckbox, discountField);

        HorizontalLayout buttonLayout = new HorizontalLayout();
        Button saveBtn = new Button("Guardar", e -> {
            if (productSelect.getValue() == null) {
                Notification.show("Por favor selecciona un producto", 3000, Notification.Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }

            Menu fullMenu = new Menu();
            fullMenu.setId(menu.getId());
            fullMenu.setName(menu.getName());

            menuItemService.addMenuItem(productSelect.getValue().getId(), fullMenu,
                    featuredCheckbox.getValue(), discountField.getValue().intValue());
            Notification.show("Ítem agregado", 3000, Notification.Position.TOP_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);

            // Recargar la grid del diálogo padre
            loadMenuItemsInDialog(parentGrid, menu);
            dialog.close();
        });
        saveBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button cancelBtn = new Button("Cancelar", e -> dialog.close());

        buttonLayout.add(saveBtn, cancelBtn);
        buttonLayout.setSpacing(true);

        dialog.add(content);
        dialog.getFooter().add(buttonLayout);
        dialog.open();
    }

    /**
     * Opens dialog to edit an existing menu item
     */
    private void openEditMenuItemDialog(MenuDto menu, MenuItem menuItem, Grid<MenuItem> parentGrid) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Editar Ítem del Menú");

        VerticalLayout content = new VerticalLayout();
        content.setSpacing(true);

        // Mostrar el producto (solo lectura)
        TextField productNameField = new TextField("Producto");
        productNameField.setValue(menuItem.getProduct().getName());
        productNameField.setReadOnly(true);

        NumberField priceField = new NumberField("Precio Original");
        priceField.setValue(menuItem.getProduct().getPrice().doubleValue());
        priceField.setReadOnly(true);

        Checkbox featuredCheckbox = new Checkbox("Producto Destacado");
        featuredCheckbox.setValue(menuItem.getFeatured() != null && menuItem.getFeatured());

        NumberField discountField = new NumberField("Descuento (%)");
        discountField.setMin(0);
        discountField.setMax(100);
        discountField.setValue(menuItem.getDiscountPercentage() != null ? menuItem.getDiscountPercentage().doubleValue() : 0.0);

        content.add(productNameField, priceField, featuredCheckbox, discountField);

        HorizontalLayout buttonLayout = new HorizontalLayout();
        Button saveBtn = new Button("Guardar", e -> {
            // Actualizar el menuItem usando menu y product id (más seguro que usar el menuItem id directamente)
            menuItemService.updateMenuItemByMenuAndProduct(
                    menu.getId(),
                    menuItem.getProduct().getId(),
                    featuredCheckbox.getValue(),
                    discountField.getValue().intValue()
            );

            Notification.show("Ítem actualizado", 3000, Notification.Position.TOP_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            loadMenuItemsInDialog(parentGrid, menu);
            dialog.close();
        });
        saveBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button cancelBtn = new Button("Cancelar", e -> dialog.close());

        buttonLayout.add(saveBtn, cancelBtn);
        buttonLayout.setSpacing(true);

        dialog.add(content);
        dialog.getFooter().add(buttonLayout);
        dialog.open();
    }

    /**
     * Opens dialog to view order details
     */
    private void openOrderDetailsDialog(Order order) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Detalles del Pedido #" + order.getId());
        dialog.setWidth("600px");

        VerticalLayout content = new VerticalLayout();
        content.setSpacing(true);

        // Order info
        HorizontalLayout infoLayout = new HorizontalLayout();
        Div userDiv = new Div();
        userDiv.add(new Span("Usuario: "));
        userDiv.add(new Span(order.getUser().getUsername()));
        infoLayout.add(userDiv);

        Div dateDiv = new Div();
        dateDiv.add(new Span("Fecha: "));
        dateDiv.add(new Span(order.getDate().toString()));
        infoLayout.add(dateDiv);

        Div statusDiv = new Div();
        statusDiv.add(new Span("Estado: "));
        statusDiv.add(new Span(order.getStatus().toString()));
        infoLayout.add(statusDiv);

        content.add(infoLayout);

        // Items list
        H3 itemsTitle = new H3("Productos:");
        content.add(itemsTitle);

        VerticalLayout itemsList = new VerticalLayout();
        itemsList.setSpacing(true);
        itemsList.setPadding(false);

        for (ProductList item : order.getItems()) {
            HorizontalLayout itemLayout = new HorizontalLayout();
            itemLayout.add(new Span(item.getProductName()));
            itemLayout.add(new Span("Cantidad: " + item.getQuantity()));
            itemLayout.add(new Span("Precio: €" + item.getPrice()));

            Div total = new Div(new Span("Total: €" + (item.getPrice() * item.getQuantity())));
            itemLayout.add(total);
            itemLayout.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);

            itemsList.add(itemLayout);
        }

        content.add(itemsList);

        // Payment info
        if (order.getPayment() != null) {
            H3 paymentTitle = new H3("Pago:");
            content.add(paymentTitle);

            VerticalLayout paymentInfo = new VerticalLayout();
            paymentInfo.setPadding(false);
            paymentInfo.setSpacing(false);
            paymentInfo.add(new Span("Método: " + order.getPayment().getPaymentMethod()));
            paymentInfo.add(new Span("Estado: " + order.getPayment().getPaymentStatus()));
            paymentInfo.add(new Span("Total: €" + order.getPayment().getPaymentAmount()));

            content.add(paymentInfo);
        }

        Button closeBtn = new Button("Cerrar", e -> dialog.close());
        closeBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        dialog.add(content);
        dialog.getFooter().add(closeBtn);
        dialog.open();
    }

    /**
     * Loads menus into the grid
     */
    private void loadMenus() {
        List<MenuDto> menus = menuService.getMenus();
        if (menusGrid != null) {
            menusGrid.setItems(menus);
        }
    }

    /**
     * Loads orders into the grid
     */
    private void loadOrders() {
        List<Order> orders = orderService.getAllOrders();
        if (ordersGrid != null) {
            ordersGrid.setItems(orders);
        }
    }
}

