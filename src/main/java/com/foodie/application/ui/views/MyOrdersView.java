package com.foodie.application.ui.views;

import com.foodie.application.domain.OrderStatus;
import com.foodie.application.dto.OrderDto;
import com.foodie.application.dto.ProductListDto;
import com.foodie.application.service.OrderService;
import com.foodie.application.service.UserService;
import com.foodie.application.ui.MainLayout;
import com.foodie.application.ui.components.PaymentGatewayComponent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.util.List;

/**
 * View for displaying user's orders.
 * Shows all orders placed by the authenticated user with details and actions.
 *
 * @author Jesus Rodriguez
 * @version 1.0
 * @since 2025
 */
@Route(value = "myorders", layout = MainLayout.class)
@PageTitle("Mis Pedidos | Foodie")
public class MyOrdersView extends VerticalLayout {

    private final OrderService orderService;
    private final UserService userService;

    private Grid<OrderDto> ordersGrid;
    private Div emptyStateDiv;

    public MyOrdersView(OrderService orderService, UserService userService) {
        this.orderService = orderService;
        this.userService = userService;

        addClassName("my-orders-view");
        setSizeFull();
        setPadding(true);
        setSpacing(true);

        // Header
        add(createHeader());

        // Content
        add(createContent());

        // Load orders
        loadOrders();
    }

    /**
     * Creates the header section
     */
    private HorizontalLayout createHeader() {
        H1 title = new H1("Mis Pedidos");
        title.addClassNames(
                LumoUtility.Margin.Top.NONE,
                LumoUtility.TextColor.PRIMARY
        );

        Button backButton = new Button("Volver al Menú", new Icon(VaadinIcon.ARROW_LEFT));
        backButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        backButton.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate("foodmenu")));

        HorizontalLayout header = new HorizontalLayout(title);
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.setWidthFull();
        header.expand(title);
        header.add(backButton);

        return header;
    }

    /**
     * Creates the main content area
     */
    private VerticalLayout createContent() {
        VerticalLayout content = new VerticalLayout();
        content.setPadding(false);
        content.setSpacing(true);
        content.setWidthFull();
        content.setFlexGrow(1, content);

        // Empty state message
        emptyStateDiv = new Div();
        emptyStateDiv.addClassNames(
                LumoUtility.TextAlignment.CENTER,
                LumoUtility.Padding.XLARGE
        );

        Div iconDiv = new Div();
        iconDiv.getStyle().set("font-size", "48px");
        iconDiv.getStyle().set("margin-bottom", "16px");
        iconDiv.setText("🛒");

        H2 emptyTitle = new H2("No tienes pedidos");
        emptyTitle.addClassNames(LumoUtility.TextColor.SECONDARY);

        Paragraph emptyText = new Paragraph("Aún no has realizado ningún pedido. ¡Haz uno ahora!");
        emptyText.addClassNames(LumoUtility.TextColor.SECONDARY);

        Button orderButton = new Button("Ir al Menú", new Icon(VaadinIcon.CART));
        orderButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        orderButton.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate("foodmenu")));

        emptyStateDiv.add(iconDiv, emptyTitle, emptyText, orderButton);

        // Orders grid
        ordersGrid = new Grid<>(OrderDto.class, false);
        ordersGrid.addColumn(order -> {
            if (order.getDate() != null) {
                return order.getDate().toString();
            }
            return "N/A";
        }).setHeader("Fecha").setAutoWidth(true);

        ordersGrid.addColumn(order -> "#" + order.getId())
                .setHeader("ID Pedido")
                .setAutoWidth(true);

        ordersGrid.addColumn(order -> {
            if (order.getItems() != null) {
                return order.getItems().size() + " artículo(s)";
            }
            return "0 artículos";
        }).setHeader("Artículos").setAutoWidth(true);

        ordersGrid.addColumn(order -> {
            if (order.getPayment() != null && order.getPayment().getPaymentAmount() != null) {
                return String.format("%.2f €", order.getPayment().getPaymentAmount());
            }
            return "N/A";
        }).setHeader("Total").setAutoWidth(true);

        ordersGrid.addColumn(order -> {
            OrderStatus status = order.getStatus();
            String statusText = getStatusLabel(status);
            return statusText;
        }).setHeader("Estado").setAutoWidth(true);

        ordersGrid.addComponentColumn(order -> {
            HorizontalLayout actions = new HorizontalLayout();
            actions.setSpacing(true);

            // Show pay button only for PENDING orders
            if (order.getStatus() == OrderStatus.PENDING) {
                Button payButton = new Button("Pagar", new Icon(VaadinIcon.CREDIT_CARD));
                payButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_SUCCESS);
                payButton.addClickListener(e -> openPaymentGateway(order));
                actions.add(payButton);
            }

            Button detailsButton = new Button("Detalles", new Icon(VaadinIcon.EYE));
            detailsButton.addThemeVariants(ButtonVariant.LUMO_SMALL);
            detailsButton.addClickListener(e -> showOrderDetails(order));
            actions.add(detailsButton);

            return actions;
        }).setHeader("Acción").setAutoWidth(true);

        ordersGrid.setAllRowsVisible(true);
        ordersGrid.addClassNames(
                LumoUtility.Border.ALL,
                LumoUtility.BorderRadius.MEDIUM
        );

        content.add(emptyStateDiv, ordersGrid);

        return content;
    }

    /**
     * Loads the user's orders from the database
     */
    private void loadOrders() {
        var currentUser = userService.getCurrentUser();

        if (currentUser == null) {
            Notification.show("Debes iniciar sesión")
                    .addThemeVariants(NotificationVariant.LUMO_WARNING);
            getUI().ifPresent(ui -> ui.navigate("login"));
            return;
        }

        // Get all orders as DTOs
        List<OrderDto> allOrders = orderService.getAllOrdersAsDto();

        // Filter by current user
        List<OrderDto> userOrders = allOrders.stream()
                .filter(order -> order.getUsername().equals(currentUser.getUsername()))
                .toList();

        if (userOrders == null || userOrders.isEmpty()) {
            // Show empty state
            emptyStateDiv.setVisible(true);
            ordersGrid.setVisible(false);
        } else {
            // Show orders
            emptyStateDiv.setVisible(false);
            ordersGrid.setVisible(true);
            ordersGrid.setItems(userOrders);
        }
    }

    /**
     * Shows details dialog for a specific order
     */
    private void showOrderDetails(OrderDto order) {
        Dialog detailsDialog = new Dialog();
        detailsDialog.setHeaderTitle("Detalles del Pedido #" + order.getId());
        detailsDialog.setWidth("600px");
        detailsDialog.setMaxWidth("90vw");

        VerticalLayout content = new VerticalLayout();
        content.setPadding(false);
        content.setSpacing(true);

        // Order info section
        H3 infoTitle = new H3("Información del Pedido");
        infoTitle.addClassNames(LumoUtility.Margin.Top.NONE);

        HorizontalLayout infoLayout = new HorizontalLayout();
        infoLayout.setWidthFull();
        infoLayout.setSpacing(true);

        Div orderIdDiv = createInfoDiv("ID Pedido", "#" + order.getId());
        Div dateDiv = createInfoDiv("Fecha", formatDate(order.getDate()));
        Div statusDiv = createInfoDiv("Estado", getStatusLabel(order.getStatus()));

        infoLayout.add(orderIdDiv, dateDiv, statusDiv);

        // Payment info section
        H3 paymentTitle = new H3("Información de Pago");
        paymentTitle.addClassNames(LumoUtility.Margin.Top.MEDIUM);

        Div paymentLayout = new Div();
        paymentLayout.getStyle().set("display", "grid");
        paymentLayout.getStyle().set("grid-template-columns", "1fr 1fr");
        paymentLayout.getStyle().set("gap", "1rem");

        if (order.getPayment() != null) {
            Div methodDiv = createInfoDiv("Método",
                    getPaymentMethodLabel(order.getPayment().getPaymentMethod().toString()));
            Div amountDiv = createInfoDiv("Monto",
                    String.format("%.2f €", order.getPayment().getPaymentAmount()));
            Div paymentStatusDiv = createInfoDiv("Estado Pago",
                    getPaymentStatusLabel(order.getPayment().getPaymentStatus().toString()));

            paymentLayout.add(methodDiv, amountDiv, paymentStatusDiv);
        }

        // Items section
        H3 itemsTitle = new H3("Artículos");
        itemsTitle.addClassNames(LumoUtility.Margin.Top.MEDIUM);

        VerticalLayout itemsList = new VerticalLayout();
        itemsList.setPadding(false);
        itemsList.setSpacing(true);

        if (order.getItems() != null && !order.getItems().isEmpty()) {
            for (ProductListDto item : order.getItems()) {
                Div itemDiv = createItemDiv(item);
                itemsList.add(itemDiv);
            }
        } else {
            itemsList.add(new Paragraph("No hay artículos en este pedido"));
        }

        // Action buttons
        HorizontalLayout actionLayout = new HorizontalLayout();
        actionLayout.setWidthFull();
        actionLayout.setSpacing(true);
        actionLayout.getStyle().set("margin-top", "1.5rem");

        Button closeButton = new Button("Cerrar");
        closeButton.addClickListener(e -> detailsDialog.close());

        if (order.getStatus() == OrderStatus.PENDING || order.getStatus() == OrderStatus.CONFIRMED) {
            Button cancelButton = new Button("Cancelar Pedido", new Icon(VaadinIcon.TRASH));
            cancelButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
            cancelButton.addClickListener(e -> confirmCancelOrder(order, detailsDialog));
            actionLayout.add(cancelButton);
        }

        actionLayout.add(closeButton);

        content.add(
                infoTitle,
                infoLayout,
                paymentTitle,
                paymentLayout,
                itemsTitle,
                itemsList,
                actionLayout
        );

        detailsDialog.add(content);
        detailsDialog.open();
    }

    /**
     * Creates an info div with label and value
     */
    private Div createInfoDiv(String label, String value) {
        Div div = new Div();
        div.addClassNames(
                LumoUtility.Background.CONTRAST_5,
                LumoUtility.Padding.MEDIUM,
                LumoUtility.BorderRadius.MEDIUM
        );

        Span labelSpan = new Span(label);
        labelSpan.addClassNames(LumoUtility.TextColor.SECONDARY, LumoUtility.FontSize.SMALL);

        Span valueSpan = new Span(value);
        valueSpan.addClassNames(LumoUtility.FontWeight.BOLD);

        VerticalLayout layout = new VerticalLayout(labelSpan, valueSpan);
        layout.setSpacing(false);
        layout.setPadding(false);

        div.add(layout);
        return div;
    }

    /**
     * Creates a visual representation of an order item
     */
    private Div createItemDiv(ProductListDto item) {
        Div itemDiv = new Div();
        itemDiv.addClassNames(
                LumoUtility.Border.ALL,
                LumoUtility.BorderRadius.MEDIUM,
                LumoUtility.Padding.MEDIUM
        );
        itemDiv.getStyle().set("display", "flex");
        itemDiv.getStyle().set("justify-content", "space-between");
        itemDiv.getStyle().set("align-items", "center");

        Div infoDiv = new Div();
        infoDiv.getStyle().set("flex", "1");

        Span nameSpan = new Span(item.getProductName());
        nameSpan.getStyle().set("font-weight", "bold");

        Span quantitySpan = new Span(
                String.format("Cantidad: %d × %.2f €", item.getQuantity(), item.getPrice())
        );
        quantitySpan.addClassNames(LumoUtility.TextColor.SECONDARY);

        VerticalLayout textLayout = new VerticalLayout(nameSpan, quantitySpan);
        textLayout.setSpacing(false);
        textLayout.setPadding(false);

        infoDiv.add(textLayout);

        Span subtotalSpan = new Span(String.format("%.2f €", item.getPrice() * item.getQuantity()));
        subtotalSpan.getStyle().set("font-weight", "bold");
        subtotalSpan.addClassNames(LumoUtility.TextColor.PRIMARY);

        itemDiv.add(infoDiv, subtotalSpan);
        return itemDiv;
    }

    /**
     * Shows confirmation dialog for canceling an order
     */
    private void confirmCancelOrder(OrderDto order, Dialog parentDialog) {
        ConfirmDialog cancelDialog = new ConfirmDialog();
        cancelDialog.setHeader("Cancelar Pedido");
        cancelDialog.setText("¿Estás seguro de que deseas cancelar este pedido? Esta acción no se puede deshacer.");
        cancelDialog.setConfirmText("Cancelar Pedido");
        cancelDialog.setCancelText("No, Volver");
        cancelDialog.setConfirmButtonTheme("error primary");

        cancelDialog.addConfirmListener(e -> {
            try {
                // Update order status to CANCELLED using OrderService
                orderService.updateOrder(order.getId(), OrderStatus.CANCELLED);

                Notification.show("Pedido cancelado exitosamente")
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);

                parentDialog.close();
                loadOrders();
            } catch (Exception ex) {
                Notification.show("Error al cancelar el pedido: " + ex.getMessage())
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });

        cancelDialog.open();
    }

    /**
     * Formats a date to a readable string
     */
    private String formatDate(java.time.LocalDate date) {
        if (date == null) return "N/A";
        return date.toString();
    }

    /**
     * Gets a user-friendly label for order status
     */
    private String getStatusLabel(OrderStatus status) {
        if (status == null) return "Desconocido";

        return switch (status) {
            case PENDING -> "⏳ Pendiente";
            case CONFIRMED -> "✓ Confirmado";
            case PREPARING -> "👨‍🍳 Preparando";
            case READY -> "✓✓ Listo";
            case ON_THE_WAY -> "🚚 En camino";
            case COMPLETED -> "📦 Completado";
            case CANCELLED -> "✕ Cancelado";
            case FAILED -> "✕ Error";
        };
    }

    /**
     * Gets a user-friendly label for payment method
     */
    private String getPaymentMethodLabel(String method) {
        return switch (method) {
            case "CASH" -> "💵 Efectivo";
            case "CARD" -> "💳 Tarjeta";
            case "BIZUM" -> "📱 Bizum";
            case "PAYPAL" -> "🌐 PayPal";
            default -> method;
        };
    }

    /**
     * Gets a user-friendly label for payment status
     */
    private String getPaymentStatusLabel(String status) {
        return switch (status) {
            case "PENDING" -> "⏳ Pendiente";
            case "COMPLETED" -> "✓ Completado";
            case "FAILED" -> "✕ Falló";
            case "REFUNDED" -> "↩ Reembolsado";
            default -> status;
        };
    }

    /**
     * Opens the payment gateway dialog for a specific order
     */
    private void openPaymentGateway(OrderDto order) {
        PaymentGatewayComponent paymentGateway = new PaymentGatewayComponent(
                order,
                orderService,
                this::loadOrders
        );
        paymentGateway.open();
    }
}

