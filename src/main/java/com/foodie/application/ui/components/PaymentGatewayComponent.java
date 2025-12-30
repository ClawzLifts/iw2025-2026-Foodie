package com.foodie.application.ui.components;

import com.foodie.application.domain.OrderStatus;
import com.foodie.application.dto.OrderDto;
import com.foodie.application.service.OrderService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.theme.lumo.LumoUtility;

/**
 * Modern payment gateway component for processing order payments.
 * Provides a user-friendly interface to select payment method and complete payments.
 * Uses OrderDto to avoid coupling with business layer entities.
 */
public class PaymentGatewayComponent extends Dialog {

    private final OrderDto order;
    private final OrderService orderService;
    private final Runnable onPaymentSuccess;

    private RadioButtonGroup<String> paymentMethodGroup;
    private Button payButton;

    public PaymentGatewayComponent(OrderDto order, OrderService orderService, Runnable onPaymentSuccess) {
        this.order = order;
        this.orderService = orderService;
        this.onPaymentSuccess = onPaymentSuccess;

        setupDialog();
        buildContent();
    }

    private void setupDialog() {
        setHeaderTitle("Pasarela de Pago");
        setWidth("500px");
        setMaxWidth("90vw");
        setModal(true);
        setCloseOnEsc(true);
    }

    private void buildContent() {
        VerticalLayout content = new VerticalLayout();
        content.setPadding(false);
        content.setSpacing(true);

        // Order Summary Card
        content.add(createOrderSummary());

        // Divider
        Hr divider = new Hr();
        divider.addClassNames(LumoUtility.Margin.Vertical.MEDIUM);
        content.add(divider);

        // Payment Methods Section
        content.add(createPaymentMethodsSection());

        // Divider
        Hr divider2 = new Hr();
        divider2.addClassNames(LumoUtility.Margin.Vertical.MEDIUM);
        content.add(divider2);

        // Security Info
        content.add(createSecurityInfo());

        // Action Buttons
        content.add(createActionButtons());

        add(content);
    }

    private VerticalLayout createOrderSummary() {
        VerticalLayout summaryCard = new VerticalLayout();
        summaryCard.addClassNames(
                LumoUtility.Background.BASE,
                LumoUtility.Padding.MEDIUM,
                LumoUtility.BorderRadius.MEDIUM,
                LumoUtility.BoxShadow.SMALL
        );
        summaryCard.setSpacing(false);

        H3 summaryTitle = new H3("Resumen del Pedido");
        summaryTitle.addClassNames(LumoUtility.Margin.Top.NONE, LumoUtility.Margin.Bottom.MEDIUM);

        // Order ID and Items count
        HorizontalLayout summaryLine1 = new HorizontalLayout();
        summaryLine1.setSpacing(true);
        summaryLine1.setAlignItems(FlexComponent.Alignment.CENTER);

        Span orderIdSpan = new Span("Pedido #" + order.getId());
        orderIdSpan.addClassNames(LumoUtility.TextColor.SECONDARY, LumoUtility.FontSize.SMALL);

        int itemCount = order.getItems() != null ? order.getItems().size() : 0;
        Span itemsSpan = new Span(itemCount + " artículo(s)");
        itemsSpan.addClassNames(LumoUtility.TextColor.SECONDARY, LumoUtility.FontSize.SMALL);

        summaryLine1.add(orderIdSpan, itemsSpan);

        // Total Amount
        HorizontalLayout summaryLine2 = new HorizontalLayout();
        summaryLine2.setWidthFull();
        summaryLine2.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        summaryLine2.setAlignItems(FlexComponent.Alignment.CENTER);

        Span totalLabelSpan = new Span("Total a Pagar:");
        totalLabelSpan.addClassNames(LumoUtility.FontWeight.BOLD);

        Double totalAmount = order.getPayment() != null ? order.getPayment().getPaymentAmount() : 0.0;
        Span totalAmountSpan = new Span(String.format("%.2f €", totalAmount));
        totalAmountSpan.addClassNames(
                LumoUtility.FontSize.XLARGE,
                LumoUtility.FontWeight.BOLD,
                LumoUtility.TextColor.PRIMARY
        );

        summaryLine2.add(totalLabelSpan, totalAmountSpan);

        summaryCard.add(summaryTitle, summaryLine1, summaryLine2);
        return summaryCard;
    }

    private VerticalLayout createPaymentMethodsSection() {
        VerticalLayout section = new VerticalLayout();
        section.setSpacing(true);
        section.setPadding(false);

        H3 methodsTitle = new H3("Método de Pago");
        methodsTitle.addClassNames(LumoUtility.Margin.Top.NONE);

        paymentMethodGroup = new RadioButtonGroup<>();
        paymentMethodGroup.setItems(
                "credit_card",
                "debit_card",
                "paypal",
                "bank_transfer"
        );

        paymentMethodGroup.setItemLabelGenerator(this::getPaymentMethodLabel);
        paymentMethodGroup.setValue("credit_card");

        // Create custom styled payment method cards
        VerticalLayout methodsContainer = new VerticalLayout();
        methodsContainer.setSpacing(true);
        methodsContainer.setPadding(false);

        for (String method : paymentMethodGroup.getListDataView().getItems().toList()) {
            methodsContainer.add(createPaymentMethodCard(method));
        }

        section.add(methodsTitle, methodsContainer);
        return section;
    }

    private HorizontalLayout createPaymentMethodCard(String method) {
        HorizontalLayout card = new HorizontalLayout();
        card.addClassNames(
                LumoUtility.Border.ALL,
                LumoUtility.BorderRadius.MEDIUM,
                LumoUtility.Padding.MEDIUM
        );
        card.getStyle().set("transition", "all 0.2s ease");
        card.getStyle().set("cursor", "pointer");
        card.setWidthFull();
        card.setAlignItems(FlexComponent.Alignment.CENTER);
        card.setSpacing(true);

        Icon methodIcon = getPaymentMethodIcon(method);
        methodIcon.setSize("32px");

        VerticalLayout info = new VerticalLayout();
        info.setPadding(false);
        info.setSpacing(false);

        Span methodName = new Span(getPaymentMethodLabel(method));
        methodName.addClassNames(LumoUtility.FontWeight.BOLD);

        Span methodDesc = new Span(getPaymentMethodDescription(method));
        methodDesc.addClassNames(LumoUtility.TextColor.SECONDARY, LumoUtility.FontSize.SMALL);

        info.add(methodName, methodDesc);

        card.add(methodIcon, info);

        // Add click listener to select this method
        card.addClickListener(e -> {
            paymentMethodGroup.setValue(method);
            updateCardStyles(method);
        });

        return card;
    }

    private void updateCardStyles(String selectedMethod) {
        // This would require storing references to cards, simplified for now
    }

    private VerticalLayout createSecurityInfo() {
        VerticalLayout securitySection = new VerticalLayout();
        securitySection.setSpacing(true);
        securitySection.setPadding(false);

        HorizontalLayout securityInfo = new HorizontalLayout();
        securityInfo.setSpacing(true);
        securityInfo.setAlignItems(FlexComponent.Alignment.CENTER);

        Icon lockIcon = new Icon(VaadinIcon.LOCK);
        lockIcon.setColor("#4CAF50");

        Span securityText = new Span("Tu pago es seguro y está encriptado con SSL");
        securityText.addClassNames(LumoUtility.TextColor.SECONDARY, LumoUtility.FontSize.SMALL);

        securityInfo.add(lockIcon, securityText);
        securitySection.add(securityInfo);

        return securitySection;
    }

    private HorizontalLayout createActionButtons() {
        HorizontalLayout actions = new HorizontalLayout();
        actions.setWidthFull();
        actions.setSpacing(true);
        actions.setJustifyContentMode(FlexComponent.JustifyContentMode.END);

        Button cancelButton = new Button("Cancelar", new Icon(VaadinIcon.CLOSE));
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        cancelButton.addClickListener(e -> close());

        payButton = new Button("Realizar Pago", new Icon(VaadinIcon.CREDIT_CARD));
        payButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
        payButton.setWidth("150px");
        payButton.addClickListener(e -> processPayment());

        actions.add(cancelButton, payButton);
        return actions;
    }

    private void processPayment() {
        String selectedMethod = paymentMethodGroup.getValue();

        if (selectedMethod == null) {
            Notification.show("Selecciona un método de pago")
                    .addThemeVariants(NotificationVariant.LUMO_WARNING);
            return;
        }

        // Disable button to prevent double-click
        payButton.setEnabled(false);

        // Simulate payment processing
        getUI().ifPresent(ui -> ui.access(() -> {
            try {
                // Simulate API call delay
                Thread.sleep(1500);

                // Update order status to CONFIRMED (not COMPLETED)
                orderService.updateOrder(order.getId(), OrderStatus.CONFIRMED);

                Notification successNotification = Notification.show(
                        "✓ Pago realizado con éxito",
                        3000,
                        Notification.Position.TOP_CENTER
                );
                successNotification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);

                // Execute callback
                if (onPaymentSuccess != null) {
                    onPaymentSuccess.run();
                }

                // Close dialog
                close();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                Notification.show("Error al procesar el pago")
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                payButton.setEnabled(true);
            }
        }));
    }

    private Icon getPaymentMethodIcon(String method) {
        return switch (method) {
            case "credit_card" -> new Icon(VaadinIcon.CREDIT_CARD);
            case "debit_card" -> new Icon(VaadinIcon.CREDIT_CARD);
            case "paypal" -> {
                Icon icon = new Icon(VaadinIcon.WALLET);
                icon.setColor("#003087");
                yield icon;
            }
            case "bank_transfer" -> new Icon(VaadinIcon.BUILDING);
            default -> new Icon(VaadinIcon.MONEY);
        };
    }

    private String getPaymentMethodLabel(String method) {
        return switch (method) {
            case "credit_card" -> "Tarjeta de Crédito";
            case "debit_card" -> "Tarjeta de Débito";
            case "paypal" -> "PayPal";
            case "bank_transfer" -> "Transferencia Bancaria";
            default -> method;
        };
    }

    private String getPaymentMethodDescription(String method) {
        return switch (method) {
            case "credit_card" -> "Visa, Mastercard, American Express";
            case "debit_card" -> "Tarjetas de débito internacionales";
            case "paypal" -> "Pago seguro con tu cuenta PayPal";
            case "bank_transfer" -> "Transferencia bancaria directa";
            default -> "";
        };
    }
}

