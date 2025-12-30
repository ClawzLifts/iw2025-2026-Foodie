package com.foodie.application.ui.components;

import com.foodie.application.dto.MenuDto;
import com.foodie.application.dto.MenuItemDisplayDto;
import com.foodie.application.dto.ProductDto;
import com.foodie.application.service.MenuItemService;
import com.foodie.application.service.MenuService;
import com.foodie.application.service.ProductService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.util.ArrayList;
import java.util.List;

/**
 * Component for managing menus and menu items.
 * Provides functionality to create, edit, delete menus, and manage menu items.
 */
public class MenuManagementComponent extends VerticalLayout {

    private final MenuService menuService;
    private final MenuItemService menuItemService;
    private final ProductService productService;

    private Grid<MenuDto> menusGrid;
    private List<MenuDto> allMenus = new ArrayList<>();

    public MenuManagementComponent(MenuService menuService, MenuItemService menuItemService, ProductService productService) {
        this.menuService = menuService;
        this.menuItemService = menuItemService;
        this.productService = productService;

        setPadding(false);
        setSpacing(true);
        setWidthFull();
        setFlexGrow(1, this);

        initializeComponent();
    }

    private void initializeComponent() {
        // Header with add button
        H2 title = new H2("Gestión de Menús");
        title.addClassNames(LumoUtility.Margin.Top.NONE);

        HorizontalLayout titleLayout = new HorizontalLayout();
        titleLayout.setWidthFull();
        titleLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        titleLayout.add(title);
        add(titleLayout);

        // Search bar and add button
        TextField searchField = new TextField();
        searchField.setPlaceholder("Buscar menú por nombre...");
        searchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        searchField.setWidth("300px");

        Button addMenuBtn = new Button("Crear Nuevo Menú", new Icon(VaadinIcon.PLUS));
        addMenuBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addMenuBtn.addClickListener(e -> openAddMenuDialog());

        HorizontalLayout searchLayout = new HorizontalLayout();
        searchLayout.setWidthFull();
        searchLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        searchLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        searchLayout.setSpacing(true);
        searchLayout.add(searchField, addMenuBtn);
        add(searchLayout);

        // Menus Grid
        menusGrid = new Grid<>(MenuDto.class, false);
        menusGrid.setWidthFull();
        menusGrid.setHeightFull();
        menusGrid.addThemeVariants(GridVariant.LUMO_COMPACT);
        menusGrid.setSelectionMode(Grid.SelectionMode.NONE);

        searchField.addValueChangeListener(e -> filterMenus(e.getValue()));

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

        add(menusGrid);

        loadMenus();
    }

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
        Grid<MenuItemDisplayDto> itemsGrid = new Grid<>(MenuItemDisplayDto.class, false);
        itemsGrid.setWidthFull();
        itemsGrid.addThemeVariants(GridVariant.LUMO_COMPACT);
        itemsGrid.setSelectionMode(Grid.SelectionMode.NONE);

        itemsGrid.addColumn(MenuItemDisplayDto::getProductName).setHeader("Producto");
        itemsGrid.addColumn(MenuItemDisplayDto::getOriginalPrice).setHeader("Precio");
        itemsGrid.addColumn(MenuItemDisplayDto::getFeatured).setHeader("Destacado");
        itemsGrid.addColumn(MenuItemDisplayDto::getDiscountPercentage).setHeader("Descuento %");

        itemsGrid.addComponentColumn(item -> {
            HorizontalLayout actions = new HorizontalLayout();
            actions.setSpacing(true);

            Button editBtn = new Button(new Icon(VaadinIcon.EDIT));
            editBtn.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_CONTRAST);
            editBtn.addClickListener(e -> openEditMenuItemDialog(menu, item, itemsGrid));

            Button removeBtn = new Button(new Icon(VaadinIcon.TRASH));
            removeBtn.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_ERROR);
            removeBtn.addClickListener(e -> {
                menuItemService.deleteMenuItem(menu.getId(), item.getProductId());
                Notification.show("Ítem eliminado", 3000, Notification.Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                loadMenuItemsInDialog(itemsGrid, menu);
            });

            actions.add(editBtn, removeBtn);
            return actions;
        }).setHeader("Acciones").setWidth("150px");

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

    private void loadMenuItemsInDialog(Grid<MenuItemDisplayDto> grid, MenuDto menu) {
        List<MenuItemDisplayDto> items = menuService.getMenuItemsForDisplay(menu.getId());
        grid.setItems(items);
    }

    private void openAddMenuItemDialog(MenuDto menu, Grid<MenuItemDisplayDto> parentGrid) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Agregar Ítem al Menú");

        VerticalLayout content = new VerticalLayout();
        content.setSpacing(true);

        com.vaadin.flow.component.select.Select<ProductDto> productSelect =
                new com.vaadin.flow.component.select.Select<>();
        productSelect.setLabel("Producto");
        productSelect.setWidthFull();

        List<ProductDto> products = productService.getAllProductsAsDto();
        productSelect.setItems(products);
        productSelect.setItemLabelGenerator(ProductDto::getName);

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

            menuItemService.addMenuItem(productSelect.getValue().getId(), menu.getId(),
                    featuredCheckbox.getValue(), (int) discountField.getValue().doubleValue());
            Notification.show("Ítem agregado", 3000, Notification.Position.TOP_CENTER)
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

    private void openEditMenuItemDialog(MenuDto menu, MenuItemDisplayDto menuItem, Grid<MenuItemDisplayDto> parentGrid) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Editar Ítem del Menú");

        VerticalLayout content = new VerticalLayout();
        content.setSpacing(true);

        TextField productNameField = new TextField("Producto");
        productNameField.setValue(menuItem.getProductName());
        productNameField.setReadOnly(true);

        NumberField priceField = new NumberField("Precio Original");
        priceField.setValue(menuItem.getOriginalPrice());
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
            menuItemService.updateMenuItemByMenuAndProduct(
                    menu.getId(),
                    menuItem.getProductId(),
                    featuredCheckbox.getValue(),
                    (int) discountField.getValue().doubleValue()
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

    private void loadMenus() {
        allMenus = menuService.getMenus();
        if (menusGrid != null) {
            menusGrid.setItems(allMenus);
        }
    }

    private void filterMenus(String searchTerm) {
        if (searchTerm == null || searchTerm.isEmpty()) {
            menusGrid.setItems(allMenus);
        } else {
            List<MenuDto> filteredMenus = allMenus.stream()
                    .filter(menu -> menu.getName().toLowerCase().contains(searchTerm.toLowerCase()))
                    .toList();
            menusGrid.setItems(filteredMenus);
        }
    }
}

