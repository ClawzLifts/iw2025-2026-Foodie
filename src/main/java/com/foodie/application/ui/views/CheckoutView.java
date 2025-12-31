package com.foodie.application.ui.views;

import com.foodie.application.domain.PaymentMethod;
import com.foodie.application.domain.ProductList;
import com.foodie.application.service.CartService;
import com.foodie.application.service.OrderService;
import com.foodie.application.service.UserService;
import com.foodie.application.ui.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.*;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.util.List;

/**
 * Modern checkout view for completing customer orders.
 * Displays cart contents, order summary, and payment options.
 *
 * @author Jesus Rodriguez
 * @version 1.0
 * @since 2025
 */
@Route(value = "checkout", layout = MainLayout.class)
@PageTitle("Checkout | Foodie")
public class CheckoutView extends VerticalLayout implements BeforeEnterObserver {

    private final CartService cartService;
    private final OrderService orderService;
    private final UserService userService;

    private Grid<ProductList> cartGrid;
    private Div totalPriceDiv;
    private ComboBox<PaymentMethod> paymentMethodCombo;
    private ComboBox<String> deliveryTypeCombo;
    private TextField addressField;
    private H4 addressTitle;
    private TextArea notesField;
    private Button proceedButton;

    // Summary elements
    private Div itemCountDiv;
    private Span itemCountValue;
    private Div subtotalDiv;
    private Span subtotalValue;
    private Span totalValue;

    public CheckoutView(CartService cartService, OrderService orderService, UserService userService) {
        this.cartService = cartService;
        this.orderService = orderService;
        this.userService = userService;

        addClassName("checkout-view");
        setSizeFull();
        setPadding(false);
        setSpacing(false);

        // Main container with two columns
        HorizontalLayout mainContent = new HorizontalLayout();
        mainContent.setWidthFull();
        mainContent.setSpacing(true);
        mainContent.addClassName(LumoUtility.Padding.MEDIUM);
        mainContent.getStyle().set("gap", "2rem");

        // Left side: Order items
        VerticalLayout leftSection = createLeftSection();
        leftSection.setFlexGrow(1, leftSection);

        // Right side: Order summary and payment
        VerticalLayout rightSection = createRightSection();
        rightSection.setWidth("400px");
        rightSection.setMinWidth("350px");

        mainContent.add(leftSection, rightSection);
        add(mainContent);
    }

    /**
     * Creates the left section showing cart items
     */
    private VerticalLayout createLeftSection() {
        VerticalLayout section = new VerticalLayout();
        section.setPadding(false);
        section.setSpacing(true);

        H2 title = new H2("Detalle de tu Pedido");
        title.addClassNames(
                LumoUtility.Margin.Bottom.MEDIUM,
                LumoUtility.TextColor.PRIMARY
        );

        // Cart items grid
        cartGrid = new Grid<>(ProductList.class, false);
        cartGrid.addColumn(ProductList::getProductName)
                .setHeader("Producto")
                .setAutoWidth(true)
                .setFlexGrow(2);

        cartGrid.addColumn(ProductList::getQuantity)
                .setHeader("Cantidad")
                .setAutoWidth(true)
                .setTextAlign(ColumnTextAlign.CENTER);

        cartGrid.addColumn(item -> String.format("%.2f €", item.getPrice()))
                .setHeader("Precio Unitario")
                .setAutoWidth(true)
                .setTextAlign(ColumnTextAlign.END);

        cartGrid.addColumn(item -> String.format("%.2f €", item.getPrice() * item.getQuantity()))
                .setHeader("Subtotal")
                .setAutoWidth(true)
                .setTextAlign(ColumnTextAlign.END);

        // Add remove button
        cartGrid.addComponentColumn(item -> {
            Button removeBtn = new Button(new Icon(VaadinIcon.TRASH));
            removeBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE, ButtonVariant.LUMO_ERROR);
            removeBtn.addClickListener(e -> {
                cartService.removeFromCart(item.getProductId());
                refreshCart();
            });
            return removeBtn;
        }).setHeader("Acción").setAutoWidth(true);

        cartGrid.setAllRowsVisible(true);
        cartGrid.addClassNames(
                LumoUtility.Border.ALL,
                LumoUtility.BorderRadius.MEDIUM
        );

        section.add(title, cartGrid);
        return section;
    }

    /**
     * Creates the right section showing order summary and payment options
     */
    private VerticalLayout createRightSection() {
        VerticalLayout section = new VerticalLayout();
        section.addClassNames(
                LumoUtility.Background.BASE,
                LumoUtility.BorderRadius.MEDIUM,
                LumoUtility.BoxShadow.MEDIUM,
                LumoUtility.Padding.LARGE
        );
        section.setSpacing(true);

        // Order Summary
        H3 summaryTitle = new H3("Resumen del Pedido");
        summaryTitle.addClassNames(LumoUtility.Margin.Top.NONE);

        Div summaryContainer = new Div();
        summaryContainer.addClassNames(
                LumoUtility.Border.TOP,
                LumoUtility.Border.BOTTOM,
                LumoUtility.Padding.Vertical.MEDIUM
        );
        summaryContainer.getStyle().set("display", "grid");
        summaryContainer.getStyle().set("gap", "0.5rem");

        // Item count
        itemCountDiv = new Div();
        itemCountDiv.getStyle().set("display", "flex");
        itemCountDiv.getStyle().set("justify-content", "space-between");
        Span itemCountLabel = new Span("Artículos:");
        itemCountLabel.addClassNames(LumoUtility.TextColor.SECONDARY);
        itemCountValue = new Span("0");
        itemCountDiv.add(itemCountLabel, itemCountValue);

        // Subtotal
        subtotalDiv = new Div();
        subtotalDiv.getStyle().set("display", "flex");
        subtotalDiv.getStyle().set("justify-content", "space-between");
        Span subtotalLabel = new Span("Subtotal:");
        subtotalLabel.addClassNames(LumoUtility.TextColor.SECONDARY);
        subtotalValue = new Span("0.00 €");
        subtotalDiv.add(subtotalLabel, subtotalValue);

        // Shipping (simulated)
        Div shippingDiv = new Div();
        shippingDiv.getStyle().set("display", "flex");
        shippingDiv.getStyle().set("justify-content", "space-between");
        Span shippingLabel = new Span("Envío:");
        shippingLabel.addClassNames(LumoUtility.TextColor.SECONDARY);
        Span shippingValue = new Span("Gratis");
        shippingValue.addClassNames(LumoUtility.TextColor.SUCCESS);
        shippingDiv.add(shippingLabel, shippingValue);

        summaryContainer.add(itemCountDiv, subtotalDiv, shippingDiv);

        // Total Price (highlighted)
        totalPriceDiv = new Div();
        totalPriceDiv.getStyle().set("display", "flex");
        totalPriceDiv.getStyle().set("justify-content", "space-between");
        totalPriceDiv.getStyle().set("align-items", "center");
        totalPriceDiv.getStyle().set("font-size", "1.5rem");
        totalPriceDiv.getStyle().set("font-weight", "bold");
        totalPriceDiv.addClassNames(
                LumoUtility.Padding.Vertical.MEDIUM,
                LumoUtility.TextColor.PRIMARY
        );
        Span totalLabel = new Span("TOTAL");
        totalValue = new Span("0.00 €");
        totalPriceDiv.add(totalLabel, totalValue);

        // Delivery Type
        H4 deliveryTitle = new H4("Tipo de Entrega");
        deliveryTitle.addClassNames(LumoUtility.Margin.Top.LARGE, LumoUtility.Margin.Bottom.SMALL);

        deliveryTypeCombo = new ComboBox<>("Selecciona tipo de entrega");
        deliveryTypeCombo.setItems("Recoger/Mesa", "A Domicilio");
        deliveryTypeCombo.setWidthFull();
        deliveryTypeCombo.setRequiredIndicatorVisible(true);
        deliveryTypeCombo.addValueChangeListener(e -> {
            boolean isDelivery = "A Domicilio".equals(e.getValue());
            addressTitle.setVisible(isDelivery);
            addressField.setVisible(isDelivery);
        });
        deliveryTypeCombo.addClassNames(LumoUtility.Margin.Bottom.MEDIUM);

        // Delivery Address
        addressTitle = new H4("Dirección de Entrega");
        addressTitle.addClassNames(LumoUtility.Margin.Top.LARGE, LumoUtility.Margin.Bottom.SMALL);
        addressTitle.setVisible(false);

        addressField = new TextField();
        addressField.setPlaceholder("Calle, número y piso");
        addressField.setWidthFull();
        addressField.setVisible(false);
        addressField.addClassNames(LumoUtility.Margin.Bottom.MEDIUM);

        // Notes
        H4 notesTitle = new H4("Notas Especiales");
        notesTitle.addClassNames(LumoUtility.Margin.Top.SMALL, LumoUtility.Margin.Bottom.SMALL);

        notesField = new TextArea();
        notesField.setPlaceholder("Alergias, instrucciones especiales, etc.");
        notesField.setWidthFull();
        notesField.setHeight("80px");
        notesField.addClassNames(LumoUtility.Margin.Bottom.MEDIUM);

        // Payment Method
        H4 paymentTitle = new H4("Método de Pago");
        paymentTitle.addClassNames(LumoUtility.Margin.Top.SMALL, LumoUtility.Margin.Bottom.SMALL);

        paymentMethodCombo = new ComboBox<>("Selecciona método de pago");
        paymentMethodCombo.setItems(PaymentMethod.values());
        paymentMethodCombo.setItemLabelGenerator(method -> {
            return switch (method) {
                case CASH -> "Efectivo";
                case CARD -> "Tarjeta";
                case BIZUM -> "Bizum";
                case PAYPAL -> "PayPal";
            };
        });
        paymentMethodCombo.setWidthFull();
        paymentMethodCombo.setRequiredIndicatorVisible(true);
        paymentMethodCombo.addClassNames(LumoUtility.Margin.Bottom.LARGE);

        // Action buttons
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setWidthFull();
        buttonLayout.setSpacing(true);
        buttonLayout.getStyle().set("gap", "0.5rem");

        Button backButton = new Button("Volver al Menú", e -> {
            getUI().ifPresent(ui -> ui.navigate("foodmenu"));
        });
        backButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        proceedButton = new Button("Completar Pedido", e -> completeOrder());
        proceedButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
        proceedButton.setIcon(new Icon(VaadinIcon.CREDIT_CARD));
        proceedButton.getStyle().set("flex-grow", "1");

        buttonLayout.add(backButton, proceedButton);

        section.add(
                summaryTitle,
                summaryContainer,
                totalPriceDiv,
                new Hr(),
                new H4("Opciones de Entrega"),
                deliveryTypeCombo,
                addressTitle,
                addressField,
                notesTitle,
                notesField,
                paymentTitle,
                paymentMethodCombo,
                buttonLayout
        );

        return section;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        List<ProductList> cart = cartService.getCart();

        // If cart is empty, redirect to menu
        if (cart == null || cart.isEmpty()) {
            event.forwardTo(MenuView.class);
            Notification.show("Tu carrito está vacío")
                    .addThemeVariants(NotificationVariant.LUMO_WARNING);
            return;
        }

        refreshCart();
    }

    /**
     * Refreshes the cart display
     */
    private void refreshCart() {
        List<ProductList> cart = cartService.getCart();
        cartGrid.setItems(cart);

        // Calculate totals
        double total = cart.stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();

        int itemCount = cart.stream()
                .mapToInt(ProductList::getQuantity)
                .sum();

        // Update summary
        updateSummary(itemCount, total);
    }

    /**
     * Updates the order summary display
     */
    private void updateSummary(int itemCount, double total) {
        // Update item count
        itemCountValue.setText(String.valueOf(itemCount));

        // Update subtotal (same as total since shipping is free)
        subtotalValue.setText(String.format("%.2f €", total));

        // Update total
        totalValue.setText(String.format("%.2f €", total));
    }


    /**
     * Completes the order and processes payment
     */
    private void completeOrder() {
        // Validate delivery type
        if (deliveryTypeCombo.isEmpty()) {
            Notification.show("Por favor, selecciona un tipo de entrega")
                    .addThemeVariants(NotificationVariant.LUMO_WARNING);
            return;
        }

        // Validate address only if delivery type is "A Domicilio"
        if ("A Domicilio".equals(deliveryTypeCombo.getValue()) && addressField.isEmpty()) {
            Notification.show("Por favor, ingresa una dirección de entrega")
                    .addThemeVariants(NotificationVariant.LUMO_WARNING);
            return;
        }

        if (paymentMethodCombo.isEmpty()) {
            Notification.show("Por favor, selecciona un método de pago")
                    .addThemeVariants(NotificationVariant.LUMO_WARNING);
            return;
        }

        List<ProductList> cart = cartService.getCart();
        if (cart == null || cart.isEmpty()) {
            Notification.show("Tu carrito está vacío")
                    .addThemeVariants(NotificationVariant.LUMO_WARNING);
            return;
        }

        // Calculate total for confirmation
        double totalAmount = cart.stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();

        // Show confirmation dialog
        ConfirmDialog dialog = new ConfirmDialog();
        dialog.setHeader("Confirmar Pedido");
        dialog.setText(String.format(
                "¿Confirmas tu pedido por %.2f €?\n\nEntrega: %s",
                totalAmount,
                addressField.getValue()
        ));
        dialog.setConfirmText("Confirmar");
        dialog.setCancelText("Cancelar");

        dialog.addConfirmListener(e -> {
            try {
                // Get current user
                var currentUser = userService.getCurrentUser();
                if (currentUser == null) {
                    Notification.show("Debes iniciar sesión para completar el pedido")
                            .addThemeVariants(NotificationVariant.LUMO_ERROR);
                    getUI().ifPresent(ui -> ui.navigate("login"));
                    return;
                }

                // Create order
                Integer orderId = orderService.addOrder(
                        currentUser.getId(),
                        cart.stream()
                                .map(item -> new com.foodie.application.dto.ProductListDto(
                                        item.getProductId(),
                                        item.getProductName(),
                                        item.getPrice(),
                                        item.getQuantity()
                                ))
                                .toList(),
                        paymentMethodCombo.getValue().toString(),
                        addressField.getValue(),
                        notesField.getValue()
                );

                // Clear cart
                cartService.clearCart();

                // Show success message
                Notification success = Notification.show(
                        "¡Pedido completado exitosamente! Tu número de pedido es: #" + orderId,
                        5000,
                        Notification.Position.MIDDLE
                );
                success.addThemeVariants(NotificationVariant.LUMO_SUCCESS);

                // Redirect to order confirmation
                getUI().ifPresent(ui -> ui.navigate("myorders"));

            } catch (Exception ex) {
                Notification.show("Error al procesar el pedido: " + ex.getMessage())
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });

        dialog.open();
    }
}

