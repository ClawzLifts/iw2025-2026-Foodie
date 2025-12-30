package com.foodie.application.ui.components;

import com.foodie.application.domain.OrderStatus;
import com.foodie.application.dto.OrderDto;
import com.foodie.application.dto.OrderFilterDto;
import com.foodie.application.dto.ProductListDto;
import com.foodie.application.service.OrderService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.time.LocalDate;
import java.util.List;

/**
 * Component for managing orders with filtering and status visualization.
 * Provides functionality to view orders, update order status, view order details,
 * and filter orders by date range and status.
 * Uses DTOs instead of domain entities for proper separation of concerns.
 */
public class OrderManagementComponent extends VerticalLayout {

    private final OrderService orderService;
    private Grid<OrderDto> ordersGrid;
    private List<OrderDto> allOrders;

    public OrderManagementComponent(OrderService orderService) {
        this.orderService = orderService;

        setPadding(false);
        setSpacing(true);
        setWidthFull();
        setFlexGrow(1, this);

        initializeComponent();
    }

    private void initializeComponent() {
        H2 title = new H2("Gestión de Pedidos");
        title.addClassNames(LumoUtility.Margin.Top.NONE);

        HorizontalLayout titleLayout = new HorizontalLayout();
        titleLayout.setWidthFull();
        titleLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        titleLayout.add(title);
        add(titleLayout);

        // Filtros
        HorizontalLayout filterLayout = new HorizontalLayout();
        filterLayout.setSpacing(true);
        filterLayout.setAlignItems(FlexComponent.Alignment.END);
        filterLayout.setWidthFull();
        filterLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        // Filtro de fecha desde
        DatePicker fromDatePicker = new DatePicker("Desde");
        fromDatePicker.setWidth("150px");

        // Filtro de fecha hasta
        DatePicker toDatePicker = new DatePicker("Hasta");
        toDatePicker.setWidth("150px");

        // Filtro de estado
        Select<OrderStatus> statusFilter = new Select<>();
        statusFilter.setLabel("Estado");
        statusFilter.setItems(OrderStatus.values());
        statusFilter.setWidth("150px");
        statusFilter.setEmptySelectionAllowed(true);
        statusFilter.setEmptySelectionCaption("Todos");

        // Botón para filtrar
        Button filterBtn = new Button("Filtrar", new Icon(VaadinIcon.FILTER));
        filterBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        filterBtn.addClickListener(e -> filterOrders(fromDatePicker.getValue(), toDatePicker.getValue(), statusFilter.getValue()));

        // Botón para limpiar filtros
        Button clearBtn = new Button("Limpiar", new Icon(VaadinIcon.REFRESH));
        clearBtn.addClickListener(e -> {
            fromDatePicker.clear();
            toDatePicker.clear();
            statusFilter.clear();
            loadOrders();
        });

        filterLayout.add(fromDatePicker, toDatePicker, statusFilter, filterBtn, clearBtn);
        add(filterLayout);

        // Orders Grid
        ordersGrid = new Grid<>(OrderDto.class, false);
        ordersGrid.setWidthFull();
        ordersGrid.setHeightFull();
        ordersGrid.addThemeVariants(GridVariant.LUMO_COMPACT);
        ordersGrid.setSelectionMode(Grid.SelectionMode.NONE);

        ordersGrid.addColumn(OrderDto::getId).setHeader("ID").setWidth("80px");
        ordersGrid.addColumn(OrderDto::getUsername).setHeader("Usuario");
        ordersGrid.addColumn(OrderDto::getDate).setHeader("Fecha");

        // Columna de Estado con Chip de color
        ordersGrid.addComponentColumn(orderDto -> createStatusChip(orderDto.getStatus())).setHeader("Estado").setWidth("120px");

        ordersGrid.addComponentColumn(orderDto -> {
            HorizontalLayout statusLayout = new HorizontalLayout();
            statusLayout.setSpacing(true);

            Select<OrderStatus> statusSelect = new Select<>();
            statusSelect.setItems(OrderStatus.values());
            statusSelect.setValue(orderDto.getStatus());
            statusSelect.setWidth("150px");

            Button updateBtn = new Button("Actualizar", new Icon(VaadinIcon.CHECK));
            updateBtn.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_PRIMARY);
            updateBtn.addClickListener(e -> {
                orderService.updateOrder(orderDto.getId(), statusSelect.getValue());
                Notification.show("Pedido actualizado", 3000, Notification.Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                loadOrders();
            });

            Button viewBtn = new Button("Ver Detalles", new Icon(VaadinIcon.EYE));
            viewBtn.addThemeVariants(ButtonVariant.LUMO_SMALL);
            viewBtn.addClickListener(e -> openOrderDetailsDialog(orderDto));

            // Agregar botón de pago solo para pedidos PENDING
            if (orderDto.getStatus() == OrderStatus.PENDING) {
                Button payBtn = new Button("Pagar", new Icon(VaadinIcon.CREDIT_CARD));
                payBtn.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_SUCCESS);
                payBtn.addClickListener(e -> openPaymentGateway(orderDto));
                statusLayout.add(payBtn);
            }

            statusLayout.add(statusSelect, updateBtn, viewBtn);
            return statusLayout;
        }).setHeader("Acciones").setWidth("500px");

        add(ordersGrid);

        loadOrders();
    }

    /**
     * Creates a status chip with color based on order status
     */
    private Div createStatusChip(OrderStatus status) {
        Div chip = new Div();
        chip.addClassName("order-status-chip");

        String statusText = status.toString();
        Span statusSpan = new Span(statusText);
        statusSpan.getStyle().set("color", "white").set("font-weight", "bold");

        chip.add(statusSpan);
        chip.getStyle()
                .set("display", "inline-block")
                .set("padding", "6px 12px")
                .set("border-radius", "16px")
                .set("font-size", "12px")
                .set("font-weight", "bold")
                .set("text-align", "center")
                .set("min-width", "80px");

        // Asignar color según estado
        switch (status) {
            case PENDING:
                chip.getStyle().set("background-color", "#FFA500");
                break;
            case CONFIRMED:
                chip.getStyle().set("background-color", "#4CAF50");
                break;
            case PREPARING:
                chip.getStyle().set("background-color", "#2196F3");
                break;
            case READY:
                chip.getStyle().set("background-color", "#FF9800");
                break;
            case COMPLETED:
                chip.getStyle().set("background-color", "#4CAF50");
                break;
            case CANCELLED:
                chip.getStyle().set("background-color", "#F44336");
                break;
            default:
                chip.getStyle().set("background-color", "#757575");
        }

        return chip;
    }

    /**
     * Filters orders by date range and status using OrderFilterDto
     */
    private void filterOrders(LocalDate fromDate, LocalDate toDate, OrderStatus statusFilter) {
        OrderFilterDto filterDto = OrderFilterDto.builder()
                .startDate(fromDate)
                .endDate(toDate)
                .status(statusFilter != null ? statusFilter.toString() : null)
                .build();

        List<OrderDto> filteredOrders = orderService.getFilteredOrders(filterDto);
        ordersGrid.setItems(filteredOrders);
    }

    /**
     * Opens a dialog to view order details
     */
    private void openOrderDetailsDialog(OrderDto orderDto) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Detalles del Pedido #" + orderDto.getId());
        dialog.setWidth("900px");
        dialog.setHeight("700px");

        VerticalLayout content = new VerticalLayout();
        content.setSpacing(true);
        content.setPadding(true);
        content.setHeightFull();

        // Order info
        HorizontalLayout infoLayout = new HorizontalLayout();
        infoLayout.setSpacing(true);
        Div userDiv = new Div();
        userDiv.add(new Span("Usuario: "));
        userDiv.add(new Span(orderDto.getUsername()));
        infoLayout.add(userDiv);

        Div dateDiv = new Div();
        dateDiv.add(new Span("Fecha: "));
        dateDiv.add(new Span(orderDto.getDate().toString()));
        infoLayout.add(dateDiv);

        Div statusDiv = new Div();
        statusDiv.add(new Span("Estado: "));
        statusDiv.add(createStatusChip(orderDto.getStatus()));
        infoLayout.add(statusDiv);

        content.add(infoLayout);

        // Delivery Address Section
        H3 deliveryTitle = new H3("Dirección de Entrega:");
        content.add(deliveryTitle);

        VerticalLayout deliveryInfo = new VerticalLayout();
        deliveryInfo.setPadding(false);
        deliveryInfo.setSpacing(false);
        deliveryInfo.addClassName(LumoUtility.Border.ALL);
        deliveryInfo.addClassName(LumoUtility.Padding.MEDIUM);
        deliveryInfo.addClassName(LumoUtility.BorderRadius.MEDIUM);

        if (orderDto.getDeliveryAddress() != null && !orderDto.getDeliveryAddress().isEmpty()) {
            deliveryInfo.add(new Span(orderDto.getDeliveryAddress()));
        } else {
            Span emptyAddress = new Span("No especificada");
            emptyAddress.addClassNames(LumoUtility.TextColor.SECONDARY);
            deliveryInfo.add(emptyAddress);
        }
        content.add(deliveryInfo);

        // Notes Section
        H3 notesTitle = new H3("Notas Especiales:");
        content.add(notesTitle);

        VerticalLayout notesInfo = new VerticalLayout();
        notesInfo.setPadding(false);
        notesInfo.setSpacing(false);
        notesInfo.addClassName(LumoUtility.Border.ALL);
        notesInfo.addClassName(LumoUtility.Padding.MEDIUM);
        notesInfo.addClassName(LumoUtility.BorderRadius.MEDIUM);

        if (orderDto.getNotes() != null && !orderDto.getNotes().isEmpty()) {
            notesInfo.add(new Span(orderDto.getNotes()));
        } else {
            Span emptyNotes = new Span("Sin notas");
            emptyNotes.addClassNames(LumoUtility.TextColor.SECONDARY);
            notesInfo.add(emptyNotes);
        }
        content.add(notesInfo);

        // Items Grid
        H3 itemsTitle = new H3("Productos:");
        content.add(itemsTitle);

        Grid<ProductListDto> itemsGrid = new Grid<>(ProductListDto.class, false);
        itemsGrid.setWidthFull();
        itemsGrid.setHeightFull();
        itemsGrid.addThemeVariants(GridVariant.LUMO_COMPACT);
        itemsGrid.setSelectionMode(Grid.SelectionMode.NONE);

        itemsGrid.addColumn(ProductListDto::getProductName)
                .setHeader("Nombre")
                .setFlexGrow(1)
                .setSortable(true);
        itemsGrid.addColumn(ProductListDto::getQuantity)
                .setHeader("Cantidad")
                .setWidth("100px")
                .setFlexGrow(0);
        itemsGrid.addColumn(ProductListDto::getPrice)
                .setHeader("Precio (€)")
                .setWidth("120px")
                .setFlexGrow(0);
        itemsGrid.addComponentColumn(item -> {
            Double total = item.getPrice() * item.getQuantity();
            return new Span(String.format("%.2f", total));
        }).setHeader("Total (€)")
                .setWidth("120px")
                .setFlexGrow(0);

        itemsGrid.setItems(orderDto.getItems());
        content.add(itemsGrid);
        content.setFlexGrow(1, itemsGrid);

        // Payment info
        if (orderDto.getPayment() != null) {
            H3 paymentTitle = new H3("Pago:");
            content.add(paymentTitle);

            VerticalLayout paymentInfo = new VerticalLayout();
            paymentInfo.setPadding(false);
            paymentInfo.setSpacing(false);
            paymentInfo.add(new Span("Método: " + orderDto.getPayment().getPaymentMethod()));
            paymentInfo.add(new Span("Estado: " + orderDto.getPayment().getPaymentStatus()));
            paymentInfo.add(new Span("Total: €" + orderDto.getPayment().getPaymentAmount()));

            content.add(paymentInfo);
        }

        Button closeBtn = new Button("Cerrar", e -> dialog.close());
        closeBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        dialog.add(content);
        dialog.getFooter().add(closeBtn);
        dialog.open();
    }

    /**
     * Loads all orders as DTOs
     */
    private void loadOrders() {
        allOrders = orderService.getAllOrdersAsDto();

        if (ordersGrid != null) {
            ordersGrid.setItems(allOrders);
        }
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

