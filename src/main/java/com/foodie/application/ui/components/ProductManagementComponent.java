package com.foodie.application.ui.components;

import com.foodie.application.dto.AllergenDto;
import com.foodie.application.dto.ProductDto;
import com.foodie.application.service.AllergenService;
import com.foodie.application.service.ProductService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ProductManagementComponent extends VerticalLayout {

    private final ProductService productService;
    private final AllergenService allergenService;

    private Grid<ProductDto> productsGrid;
    private java.util.List<ProductDto> allProducts = new java.util.ArrayList<>();

    public ProductManagementComponent(ProductService productService, AllergenService allergenService) {
        this.productService = productService;
        this.allergenService = allergenService;

        setPadding(false);
        setSpacing(true);
        setWidthFull();
        setFlexGrow(1, this);

        initializeComponent();
    }

    private void initializeComponent() {
        // Header with add button
        HorizontalLayout headerLayout = new HorizontalLayout();
        H2 title = new H2("Gestión de Productos");
        title.addClassNames(LumoUtility.Margin.Top.NONE);

        Button addProductBtn = new Button("Crear Nuevo Producto", new Icon(VaadinIcon.PLUS));
        addProductBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addProductBtn.addClickListener(e -> openAddProductDialog());

        headerLayout.add(title);
        headerLayout.expand(title);
        headerLayout.add(addProductBtn);
        headerLayout.setWidthFull();
        headerLayout.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);

        add(headerLayout);

        // Search bar
        TextField searchField = new TextField();
        searchField.setPlaceholder("Buscar producto por nombre...");
        searchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        searchField.setWidth("300px");

        HorizontalLayout searchLayout = new HorizontalLayout();
        searchLayout.add(searchField);
        searchLayout.setWidthFull();
        add(searchLayout);

        // Products Grid
        productsGrid = new Grid<>(ProductDto.class, false);
        productsGrid.setWidthFull();
        productsGrid.setHeightFull();
        productsGrid.addThemeVariants(GridVariant.LUMO_COMPACT);
        productsGrid.setSelectionMode(Grid.SelectionMode.NONE);

        searchField.addValueChangeListener(e -> filterProducts(e.getValue()));

        productsGrid.addColumn(ProductDto::getId).setHeader("ID").setWidth("80px");
        productsGrid.addColumn(ProductDto::getName).setHeader("Nombre");
        productsGrid.addColumn(ProductDto::getDescription).setHeader("Descripción");
        productsGrid.addColumn(ProductDto::getPrice).setHeader("Precio");

        productsGrid.addComponentColumn(productDto -> {
            HorizontalLayout actions = new HorizontalLayout();
            actions.setSpacing(true);

            Button editBtn = new Button(new Icon(VaadinIcon.EDIT));
            editBtn.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_CONTRAST);
            editBtn.addClickListener(e -> openEditProductDialog(productDto));

            Button deleteBtn = new Button(new Icon(VaadinIcon.TRASH));
            deleteBtn.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_ERROR);
            deleteBtn.addClickListener(e -> openDeleteProductDialog(productDto));

            actions.add(editBtn, deleteBtn);
            return actions;
        }).setHeader("Acciones").setWidth("150px");

        add(productsGrid);

        loadProducts();
    }

    private void openAddProductDialog() {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Crear Nuevo Producto");
        dialog.setWidth("600px");

        VerticalLayout content = new VerticalLayout();
        content.setSpacing(true);

        TextField nameField = new TextField("Nombre del Producto");
        nameField.setWidthFull();
        nameField.setRequired(true);

        TextField descriptionField = new TextField("Descripción");
        descriptionField.setWidthFull();

        NumberField priceField = new NumberField("Precio");
        priceField.setMin(0);
        priceField.setWidthFull();
        priceField.setRequired(true);

        TextField imageUrlField = new TextField("URL de Imagen");
        imageUrlField.setWidthFull();

        // Alérgenos
        H3 allergenTitle = new H3("Alérgenos");
        allergenTitle.addClassNames(LumoUtility.Margin.Top.MEDIUM);

        List<AllergenDto> allergens = allergenService.getAllAllergens().stream()
                .map(AllergenDto::fromAllergen)
                .toList();
        Set<String> selectedAllergenNames = new HashSet<>();

        VerticalLayout allergenList = new VerticalLayout();
        allergenList.setPadding(false);
        allergenList.setSpacing(false);

        for (AllergenDto allergen : allergens) {
            Checkbox allergenCheckbox = new Checkbox(allergen.getName());
            allergenCheckbox.addValueChangeListener(e -> {
                if (e.getValue()) {
                    selectedAllergenNames.add(allergen.getName());
                } else {
                    selectedAllergenNames.remove(allergen.getName());
                }
            });
            allergenList.add(allergenCheckbox);
        }

        // Campo para agregar nuevo alérgeno
        HorizontalLayout newAllergenLayout = new HorizontalLayout();
        TextField newAllergenField = new TextField();
        newAllergenField.setPlaceholder("Nombre del nuevo alérgeno");
        newAllergenField.setWidth("70%");

        Button addAllergenBtn = new Button("Agregar", e -> {
            if (!newAllergenField.getValue().isEmpty()) {
                try {
                    allergenService.createAllergen(newAllergenField.getValue());
                    selectedAllergenNames.add(newAllergenField.getValue());

                    Checkbox newCheckbox = new Checkbox(newAllergenField.getValue());
                    newCheckbox.setValue(true);
                    allergenList.add(newCheckbox);

                    newAllergenField.clear();
                    Notification.show("Alérgeno agregado", 2000, Notification.Position.TOP_CENTER)
                            .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                } catch (Exception ex) {
                    Notification.show("Error al crear alérgeno", 2000, Notification.Position.TOP_CENTER)
                            .addThemeVariants(NotificationVariant.LUMO_ERROR);
                }
            }
        });
        addAllergenBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SMALL);

        newAllergenLayout.add(newAllergenField, addAllergenBtn);
        newAllergenLayout.setWidthFull();

        content.add(nameField, descriptionField, priceField, imageUrlField, allergenTitle, allergenList, newAllergenLayout);

        HorizontalLayout buttonLayout = new HorizontalLayout();
        Button saveBtn = new Button("Guardar", e -> {
            if (nameField.getValue().isEmpty() || priceField.getValue() == null) {
                Notification.show("Por favor completa los campos obligatorios", 3000, Notification.Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }

            // Create product and add allergens
            try {
                productService.createProductWithAllergenNames(nameField.getValue(), descriptionField.getValue(),
                        priceField.getValue(), imageUrlField.getValue(), selectedAllergenNames);

                Notification.show("Producto creado exitosamente", 3000, Notification.Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                dialog.close();
                loadProducts();
            } catch (Exception ex) {
                Notification.show("Error al crear producto", 3000, Notification.Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });
        saveBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button cancelBtn = new Button("Cancelar", e -> dialog.close());

        buttonLayout.add(saveBtn, cancelBtn);
        buttonLayout.setSpacing(true);

        dialog.add(content);
        dialog.getFooter().add(buttonLayout);
        dialog.open();
    }

    private void openEditProductDialog(ProductDto productDto) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Editar Producto");
        dialog.setWidth("600px");

        VerticalLayout content = new VerticalLayout();
        content.setSpacing(true);

        TextField nameField = new TextField("Nombre del Producto");
        nameField.setValue(productDto.getName());
        nameField.setWidthFull();
        nameField.setRequired(true);

        TextField descriptionField = new TextField("Descripción");
        descriptionField.setValue(productDto.getDescription() != null ? productDto.getDescription() : "");
        descriptionField.setWidthFull();

        NumberField priceField = new NumberField("Precio");
        priceField.setValue(productDto.getPrice());
        priceField.setMin(0);
        priceField.setWidthFull();
        priceField.setRequired(true);

        TextField imageUrlField = new TextField("URL de Imagen");
        imageUrlField.setValue(productDto.getImageUrl() != null ? productDto.getImageUrl() : "");
        imageUrlField.setWidthFull();

        // Alérgenos
        H3 allergenTitle = new H3("Alérgenos");
        allergenTitle.addClassNames(LumoUtility.Margin.Top.MEDIUM);

        List<AllergenDto> allergens = allergenService.getAllAllergens().stream()
                .map(AllergenDto::fromAllergen)
                .toList();

        Set<String> selectedAllergenNames = new HashSet<>(productDto.getAllergenNames() != null ?
                productDto.getAllergenNames() : new HashSet<>());

        VerticalLayout allergenList = new VerticalLayout();
        allergenList.setPadding(false);
        allergenList.setSpacing(false);

        for (AllergenDto allergen : allergens) {
            Checkbox allergenCheckbox = new Checkbox(allergen.getName());
            allergenCheckbox.setValue(selectedAllergenNames.contains(allergen.getName()));
            allergenCheckbox.addValueChangeListener(e -> {
                if (e.getValue()) {
                    selectedAllergenNames.add(allergen.getName());
                } else {
                    selectedAllergenNames.remove(allergen.getName());
                }
            });
            allergenList.add(allergenCheckbox);
        }

        // Campo para agregar nuevo alérgeno
        HorizontalLayout newAllergenLayout = new HorizontalLayout();
        TextField newAllergenField = new TextField();
        newAllergenField.setPlaceholder("Nombre del nuevo alérgeno");
        newAllergenField.setWidth("70%");

        Button addAllergenBtn = new Button("Agregar", e -> {
            if (!newAllergenField.getValue().isEmpty()) {
                try {
                    allergenService.createAllergen(newAllergenField.getValue());
                    selectedAllergenNames.add(newAllergenField.getValue());

                    Checkbox newCheckbox = new Checkbox(newAllergenField.getValue());
                    newCheckbox.setValue(true);
                    allergenList.add(newCheckbox);

                    newAllergenField.clear();
                    Notification.show("Alérgeno agregado", 2000, Notification.Position.TOP_CENTER)
                            .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                } catch (Exception ex) {
                    Notification.show("Error al crear alérgeno", 2000, Notification.Position.TOP_CENTER)
                            .addThemeVariants(NotificationVariant.LUMO_ERROR);
                }
            }
        });
        addAllergenBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SMALL);

        newAllergenLayout.add(newAllergenField, addAllergenBtn);
        newAllergenLayout.setWidthFull();

        content.add(nameField, descriptionField, priceField, imageUrlField, allergenTitle, allergenList, newAllergenLayout);

        HorizontalLayout buttonLayout = new HorizontalLayout();
        Button saveBtn = new Button("Guardar", e -> {
            if (nameField.getValue().isEmpty() || priceField.getValue() == null) {
                Notification.show("Por favor completa los campos obligatorios", 3000, Notification.Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }

            try {
                productService.updateProductWithAllergenNames(productDto.getId(), nameField.getValue(),
                        descriptionField.getValue(), priceField.getValue(), imageUrlField.getValue(),
                        selectedAllergenNames);

                Notification.show("Producto actualizado exitosamente", 3000, Notification.Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                dialog.close();
                loadProducts();
            } catch (Exception ex) {
                Notification.show("Error al actualizar producto", 3000, Notification.Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });
        saveBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button cancelBtn = new Button("Cancelar", e -> dialog.close());

        buttonLayout.add(saveBtn, cancelBtn);
        buttonLayout.setSpacing(true);

        dialog.add(content);
        dialog.getFooter().add(buttonLayout);
        dialog.open();
    }

    private void openDeleteProductDialog(ProductDto productDto) {
        ConfirmDialog dialog = new ConfirmDialog();
        dialog.setHeader("Confirmar eliminación");
        dialog.setText("¿Estás seguro de que deseas eliminar el producto '" + productDto.getName() + "'?");
        dialog.setConfirmText("Eliminar");
        dialog.setCancelText("Cancelar");
        dialog.setConfirmButtonTheme("error primary");

        dialog.addConfirmListener(e -> {
            productService.removeProduct(productDto.getId());
            Notification.show("Producto eliminado exitosamente", 3000, Notification.Position.TOP_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            loadProducts();
        });

        dialog.open();
    }

    private void loadProducts() {
        var products = productService.getAllProducts(null);
        allProducts = products.stream()
                .map(ProductDto::fromProduct)
                .toList();

        if (productsGrid != null) {
            productsGrid.setItems(allProducts);
        }
    }

    private void filterProducts(String searchTerm) {
        if (searchTerm == null || searchTerm.isEmpty()) {
            productsGrid.setItems(allProducts);
        } else {
            java.util.List<ProductDto> filteredProducts = allProducts.stream()
                    .filter(product -> product.getName().toLowerCase().contains(searchTerm.toLowerCase()))
                    .toList();
            productsGrid.setItems(filteredProducts);
        }
    }
}

