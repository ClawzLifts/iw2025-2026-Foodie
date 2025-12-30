package com.foodie.application.ui.components;

import com.foodie.application.dto.AllergenDto;
import com.foodie.application.dto.ProductDto;
import com.foodie.application.service.AllergenService;
import com.foodie.application.service.IngredientService;
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
    private final IngredientService ingredientService;

    private Grid<ProductDto> productsGrid;
    private java.util.List<ProductDto> allProducts = new java.util.ArrayList<>();

    public ProductManagementComponent(ProductService productService, AllergenService allergenService, IngredientService ingredientService) {
        this.productService = productService;
        this.allergenService = allergenService;
        this.ingredientService = ingredientService;

        setPadding(false);
        setSpacing(true);
        setWidthFull();
        setFlexGrow(1, this);

        initializeComponent();
    }

    private void initializeComponent() {
        // Header with title
        H2 title = new H2("Gestión de Productos");
        title.addClassNames(LumoUtility.Margin.Top.NONE);

        HorizontalLayout titleLayout = new HorizontalLayout();
        titleLayout.setWidthFull();
        titleLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        titleLayout.add(title);
        add(titleLayout);

        // Search bar and add button
        TextField searchField = new TextField();
        searchField.setPlaceholder("Buscar producto por nombre...");
        searchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        searchField.setWidth("300px");

        Button addProductBtn = new Button("Crear Nuevo Producto", new Icon(VaadinIcon.PLUS));
        addProductBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addProductBtn.addClickListener(e -> openAddProductDialog());

        HorizontalLayout searchLayout = new HorizontalLayout();
        searchLayout.setWidthFull();
        searchLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        searchLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        searchLayout.setSpacing(true);
        searchLayout.add(searchField, addProductBtn);
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

        // Columna de ingredientes
        productsGrid.addComponentColumn(productDto -> {
            if (productDto.getIngredients() != null && !productDto.getIngredients().isEmpty()) {
                return new com.vaadin.flow.component.html.Span(
                        String.join(", ", productDto.getIngredients())
                );
            }
            com.vaadin.flow.component.html.Span emptySpan = new com.vaadin.flow.component.html.Span("Sin ingredientes");
            emptySpan.addClassNames(LumoUtility.TextColor.SECONDARY);
            return emptySpan;
        }).setHeader("Ingredientes").setFlexGrow(1);

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

        // Ingredientes
        H3 ingredientTitle = new H3("Ingredientes");
        ingredientTitle.addClassNames(LumoUtility.Margin.Top.MEDIUM);

        Set<String> selectedIngredients = new HashSet<>();

        VerticalLayout ingredientList = new VerticalLayout();
        ingredientList.setPadding(false);
        ingredientList.setSpacing(false);

        // Campo para agregar ingredientes
        HorizontalLayout newIngredientLayout = new HorizontalLayout();
        TextField newIngredientField = new TextField();
        newIngredientField.setPlaceholder("Nombre del ingrediente");
        newIngredientField.setWidth("70%");

        Button addIngredientBtn = new Button("Agregar", e -> {
            if (!newIngredientField.getValue().isEmpty()) {
                try {
                    String ingredientName = newIngredientField.getValue();
                    selectedIngredients.add(ingredientName);

                    // Crear un label para mostrar el ingrediente con opción de borrar
                    HorizontalLayout ingredientItem = new HorizontalLayout();
                    ingredientItem.setAlignItems(FlexComponent.Alignment.CENTER);
                    ingredientItem.setSpacing(true);

                    com.vaadin.flow.component.html.Span ingredientLabel =
                            new com.vaadin.flow.component.html.Span(ingredientName);

                    Button removeIngredientBtn = new Button("", new Icon(VaadinIcon.CLOSE));
                    removeIngredientBtn.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_ERROR);
                    removeIngredientBtn.addClickListener(event -> {
                        selectedIngredients.remove(ingredientName);
                        ingredientList.remove(ingredientItem);
                    });

                    ingredientItem.add(ingredientLabel, removeIngredientBtn);
                    ingredientList.add(ingredientItem);

                    newIngredientField.clear();
                    Notification.show("Ingrediente agregado", 2000, Notification.Position.TOP_CENTER)
                            .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                } catch (Exception ex) {
                    Notification.show("Error al agregar ingrediente", 2000, Notification.Position.TOP_CENTER)
                            .addThemeVariants(NotificationVariant.LUMO_ERROR);
                }
            }
        });
        addIngredientBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SMALL);

        newIngredientLayout.add(newIngredientField, addIngredientBtn);
        newIngredientLayout.setWidthFull();

        content.add(nameField, descriptionField, priceField, imageUrlField, allergenTitle, allergenList, newAllergenLayout, ingredientTitle, ingredientList, newIngredientLayout);

        HorizontalLayout buttonLayout = new HorizontalLayout();
        Button saveBtn = new Button("Guardar", e -> {
            if (nameField.getValue().isEmpty() || priceField.getValue() == null) {
                Notification.show("Por favor completa los campos obligatorios", 3000, Notification.Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }

            // Create product with allergens and ingredients
            try {
                productService.createProductWithAllergenNamesAndIngredients(nameField.getValue(), descriptionField.getValue(),
                        priceField.getValue(), imageUrlField.getValue(), selectedAllergenNames, selectedIngredients);

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

        // Ingredientes
        H3 ingredientTitle = new H3("Ingredientes");
        ingredientTitle.addClassNames(LumoUtility.Margin.Top.MEDIUM);

        Set<String> selectedIngredients = new HashSet<>(productDto.getIngredients() != null ?
                productDto.getIngredients() : new HashSet<>());

        VerticalLayout ingredientList = new VerticalLayout();
        ingredientList.setPadding(false);
        ingredientList.setSpacing(false);

        // Mostrar ingredientes existentes
        for (String ingredient : selectedIngredients) {
            HorizontalLayout ingredientItem = new HorizontalLayout();
            ingredientItem.setAlignItems(FlexComponent.Alignment.CENTER);
            ingredientItem.setSpacing(true);

            com.vaadin.flow.component.html.Span ingredientLabel =
                    new com.vaadin.flow.component.html.Span(ingredient);

            Button removeIngredientBtn = new Button("", new Icon(VaadinIcon.CLOSE));
            removeIngredientBtn.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_ERROR);
            removeIngredientBtn.addClickListener(event -> {
                selectedIngredients.remove(ingredient);
                ingredientList.remove(ingredientItem);
            });

            ingredientItem.add(ingredientLabel, removeIngredientBtn);
            ingredientList.add(ingredientItem);
        }

        // Campo para agregar ingredientes
        HorizontalLayout newIngredientLayout = new HorizontalLayout();
        TextField newIngredientField = new TextField();
        newIngredientField.setPlaceholder("Nombre del ingrediente");
        newIngredientField.setWidth("70%");

        Button addIngredientBtn = new Button("Agregar", e -> {
            if (!newIngredientField.getValue().isEmpty()) {
                try {
                    String ingredientName = newIngredientField.getValue();
                    selectedIngredients.add(ingredientName);

                    HorizontalLayout ingredientItem = new HorizontalLayout();
                    ingredientItem.setAlignItems(FlexComponent.Alignment.CENTER);
                    ingredientItem.setSpacing(true);

                    com.vaadin.flow.component.html.Span ingredientLabel =
                            new com.vaadin.flow.component.html.Span(ingredientName);

                    Button removeIngredientBtn = new Button("", new Icon(VaadinIcon.CLOSE));
                    removeIngredientBtn.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_ERROR);
                    removeIngredientBtn.addClickListener(event -> {
                        selectedIngredients.remove(ingredientName);
                        ingredientList.remove(ingredientItem);
                    });

                    ingredientItem.add(ingredientLabel, removeIngredientBtn);
                    ingredientList.add(ingredientItem);

                    newIngredientField.clear();
                    Notification.show("Ingrediente agregado", 2000, Notification.Position.TOP_CENTER)
                            .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                } catch (Exception ex) {
                    Notification.show("Error al agregar ingrediente", 2000, Notification.Position.TOP_CENTER)
                            .addThemeVariants(NotificationVariant.LUMO_ERROR);
                }
            }
        });
        addIngredientBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SMALL);

        newIngredientLayout.add(newIngredientField, addIngredientBtn);
        newIngredientLayout.setWidthFull();

        content.add(nameField, descriptionField, priceField, imageUrlField, allergenTitle, allergenList, newAllergenLayout, ingredientTitle, ingredientList, newIngredientLayout);

        HorizontalLayout buttonLayout = new HorizontalLayout();
        Button saveBtn = new Button("Guardar", e -> {
            if (nameField.getValue().isEmpty() || priceField.getValue() == null) {
                Notification.show("Por favor completa los campos obligatorios", 3000, Notification.Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }

            try {
                productService.updateProductWithAllergenNamesAndIngredients(productDto.getId(), nameField.getValue(),
                        descriptionField.getValue(), priceField.getValue(), imageUrlField.getValue(),
                        selectedAllergenNames, selectedIngredients);

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
        var products = productService.getAllProductsAsDto();
        allProducts = products;

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

