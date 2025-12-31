package com.foodie.application.ui.components;

import com.foodie.application.domain.PaymentMethod;
import com.foodie.application.dto.CashClosingDto;
import com.foodie.application.service.CashClosingService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Component for managing cash opening and closing
 * Displays daily sales by payment method and handles cash reconciliation
 */
public class CashClosingComponent extends VerticalLayout {

    private final CashClosingService cashClosingService;
    private VerticalLayout salesLayout;
    private HorizontalLayout actionsLayout;
    private CashClosingDto todaysCash;

    public CashClosingComponent(CashClosingService cashClosingService) {
        this.cashClosingService = cashClosingService;

        setPadding(false);
        setSpacing(true);
        setWidthFull();
        setFlexGrow(1, this);

        initializeComponent();
        refreshCashStatus();
    }

    private void initializeComponent() {
        // Header
        H2 title = new H2("Control de Caja");
        title.addClassNames(LumoUtility.Margin.Top.NONE);

        HorizontalLayout titleLayout = new HorizontalLayout();
        titleLayout.setWidthFull();
        titleLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        titleLayout.add(title);
        add(titleLayout);

        // Actions section (moved to the top, centered)
        actionsLayout = new HorizontalLayout();
        actionsLayout.setSpacing(true);
        actionsLayout.setWidthFull();
        actionsLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        add(actionsLayout);

        // Sales by payment method section (no title)
        salesLayout = new VerticalLayout();
        salesLayout.setPadding(true);
        salesLayout.setSpacing(true);
        salesLayout.addClassName(LumoUtility.Border.ALL);
        salesLayout.addClassName(LumoUtility.BorderRadius.MEDIUM);
        salesLayout.addClassName(LumoUtility.Background.BASE);
        salesLayout.setHeight("250px");
        add(salesLayout);

        // Historical closings section
        H3 historyTitle = new H3("Histórico de Cierres de Caja");
        historyTitle.addClassNames(LumoUtility.Margin.Top.LARGE);
        HorizontalLayout historyTitleLayout = new HorizontalLayout();
        historyTitleLayout.setWidthFull();
        historyTitleLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        historyTitleLayout.add(historyTitle);
        add(historyTitleLayout);

        VerticalLayout historyLayout = new VerticalLayout();
        historyLayout.setPadding(true);
        historyLayout.setSpacing(true);
        historyLayout.addClassName(LumoUtility.Border.ALL);
        historyLayout.addClassName(LumoUtility.BorderRadius.MEDIUM);
        historyLayout.addClassName(LumoUtility.Background.BASE);
        historyLayout.setHeightFull();
        add(historyLayout);

        loadHistoricalClosings(historyLayout);
    }

    private void loadHistoricalClosings(VerticalLayout historyLayout) {
        List<CashClosingDto> closedClosings = cashClosingService.getAllClosedCashClosings();

        if (closedClosings.isEmpty()) {
            historyLayout.add(new Span("No hay cierres de caja registrados"));
            return;
        }

        // Create a grid to display historical closings
        Grid<CashClosingHistoryRow> historyGrid = new Grid<>(CashClosingHistoryRow.class, false);
        historyGrid.setWidthFull();
        historyGrid.setHeight("300px");
        historyGrid.addThemeVariants(GridVariant.LUMO_COMPACT);
        historyGrid.setSelectionMode(Grid.SelectionMode.NONE);

        historyGrid.addColumn(row -> row.getDate().toString())
                .setHeader("Fecha")
                .setFlexGrow(1)
                .setTextAlign(com.vaadin.flow.component.grid.ColumnTextAlign.CENTER);

        // Add columns for each payment method
        for (PaymentMethod method : PaymentMethod.values()) {
            String methodName = method.toString();
            historyGrid.addComponentColumn(row -> {
                Double error = row.getErrorForMethod(methodName);
                Span errorSpan = new Span(String.format("%.2f €", error));
                if (error > 0.01) {
                    errorSpan.getStyle().setColor("green");
                    errorSpan.getStyle().setFontWeight("bold");
                } else if (error < -0.01) {
                    errorSpan.getStyle().setColor("red");
                    errorSpan.getStyle().setFontWeight("bold");
                }
                // Center align the span
                errorSpan.getStyle().set("display", "flex");
                errorSpan.getStyle().set("justify-content", "center");
                errorSpan.getStyle().set("width", "100%");
                return errorSpan;
            }).setHeader(methodName)
                    .setFlexGrow(1)
                    .setTextAlign(com.vaadin.flow.component.grid.ColumnTextAlign.CENTER);
        }

        // ...existing code...
        Map<java.time.LocalDate, CashClosingHistoryRow> rowsByDate = new HashMap<>();
        for (CashClosingDto closing : closedClosings) {
            CashClosingHistoryRow row = rowsByDate.computeIfAbsent(closing.getDate(),
                    CashClosingHistoryRow::new);

            for (String method : closing.getDifference().keySet()) {
                Double error = closing.getDifference().getOrDefault(method, 0.0);
                row.setErrorForMethod(method, error);
            }
        }

        List<CashClosingHistoryRow> items = new ArrayList<>(rowsByDate.values());
        items.sort((a, b) -> b.getDate().compareTo(a.getDate())); // Sort by date descending

        historyGrid.setItems(items);
        historyLayout.add(historyGrid);
    }

    private void refreshCashStatus() {
        // Get today's cash status
        todaysCash = cashClosingService.getTodaysCashClosing();

        // Update sales display
        updateSalesDisplay();

        // Update actions
        updateActions();
    }

    private void updateSalesDisplay() {
        salesLayout.removeAll();

        Map<String, Double> todaysSales = cashClosingService.getTodaysSalesByPaymentMethod();

        if (todaysSales.isEmpty()) {
            salesLayout.add(new Span("Sin ventas registradas hoy"));
            return;
        }

        // Create a grid to display sales
        Grid<PaymentMethodSale> salesGrid = new Grid<>(PaymentMethodSale.class, false);
        salesGrid.setWidthFull();
        salesGrid.setHeight("auto");
        salesGrid.addThemeVariants(GridVariant.LUMO_COMPACT);
        salesGrid.setAllRowsVisible(true);
        salesGrid.getStyle().set("--lumo-grid-column-border-style", "none");

        salesGrid.addColumn(PaymentMethodSale::getPaymentMethod)
                .setHeader("Método de Pago")
                .setFlexGrow(1)
                .setTextAlign(com.vaadin.flow.component.grid.ColumnTextAlign.CENTER);

        salesGrid.addColumn(sale -> String.format("%.2f €", sale.getAmount()))
                .setHeader("Total Ventas")
                .setFlexGrow(1)
                .setTextAlign(com.vaadin.flow.component.grid.ColumnTextAlign.CENTER);

        // If cash is open, show additional columns
        if (todaysCash != null && !todaysCash.getIsClosed()) {
            salesGrid.addColumn(sale -> {
                Double opening = todaysCash.getOpeningBalance().getOrDefault(sale.getPaymentMethod(), 0.0);
                return String.format("%.2f €", opening);
            }).setHeader("Saldo Inicial")
                    .setFlexGrow(1)
                    .setTextAlign(com.vaadin.flow.component.grid.ColumnTextAlign.CENTER);
        }

        // If cash is closed, show real amount and difference
        if (todaysCash != null && todaysCash.getIsClosed()) {
            salesGrid.addColumn(sale -> {
                Double real = todaysCash.getRealAmount().getOrDefault(sale.getPaymentMethod(), 0.0);
                return String.format("%.2f €", real);
            }).setHeader("Cantidad Real")
                    .setFlexGrow(1)
                    .setTextAlign(com.vaadin.flow.component.grid.ColumnTextAlign.CENTER);

            salesGrid.addComponentColumn(sale -> {
                Double difference = todaysCash.getDifference().getOrDefault(sale.getPaymentMethod(), 0.0);
                Span diffSpan = new Span(String.format("%.2f €", difference));
                if (difference > 0) {
                    diffSpan.getStyle().setColor("green");
                } else if (difference < 0) {
                    diffSpan.getStyle().setColor("red");
                }
                return diffSpan;
            }).setHeader("Diferencia")
                    .setFlexGrow(1)
                    .setTextAlign(com.vaadin.flow.component.grid.ColumnTextAlign.CENTER);
        }

        // ...existing code...
        var salesList = todaysSales.entrySet().stream()
                .map(e -> new PaymentMethodSale(e.getKey(), e.getValue()))
                .toList();

        salesGrid.setItems(salesList);
        salesLayout.add(salesGrid);

        // Add status information
        if (todaysCash != null) {
            HorizontalLayout statusLayout = new HorizontalLayout();
            statusLayout.setSpacing(true);
            statusLayout.setAlignItems(FlexComponent.Alignment.CENTER);
            statusLayout.setWidthFull();
            statusLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

            String statusText = todaysCash.getIsClosed() ? "✓ CAJA CERRADA" : "● CAJA ABIERTA";
            Span statusSpan = new Span(statusText);
            statusSpan.addClassName(LumoUtility.FontWeight.BOLD);
            if (todaysCash.getIsClosed()) {
                statusSpan.getStyle().setColor("green");
            } else {
                statusSpan.getStyle().setColor("orange");
            }

            statusLayout.add(statusSpan);
            salesLayout.add(statusLayout);
        }
    }

    private void updateActions() {
        actionsLayout.removeAll();

        if (todaysCash == null || todaysCash.getIsClosed()) {
            // Show open cash button
            Button openCashBtn = new Button("Apertura de Caja", new Icon(VaadinIcon.UNLOCK));
            openCashBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
            openCashBtn.addClickListener(e -> openCashDialog());
            actionsLayout.add(openCashBtn);
        } else {
            // Show close cash button
            Button closeCashBtn = new Button("Cierre de Caja", new Icon(VaadinIcon.LOCK));
            closeCashBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
            closeCashBtn.addClickListener(e -> closeCashDialog());
            actionsLayout.add(closeCashBtn);
        }
    }

    private void openCashDialog() {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Apertura de Caja");
        dialog.setWidth("600px");

        VerticalLayout content = new VerticalLayout();
        content.setSpacing(true);
        content.setPadding(true);

        H3 subtitle = new H3("Ingresa el saldo inicial de cada método de pago");
        subtitle.addClassNames(LumoUtility.FontSize.MEDIUM);
        content.add(subtitle);

        Map<String, NumberField> amountFields = new HashMap<>();

        // Create input fields for each payment method
        for (PaymentMethod method : PaymentMethod.values()) {
            HorizontalLayout methodLayout = new HorizontalLayout();
            methodLayout.setAlignItems(FlexComponent.Alignment.CENTER);
            methodLayout.setSpacing(true);

            Span label = new Span(method.toString());
            label.setWidth("150px");
            label.addClassName(LumoUtility.FontWeight.BOLD);

            NumberField amountField = new NumberField();
            amountField.setLabel("Saldo inicial");
            amountField.setValue(0.0);
            amountField.setMin(0);
            amountField.setWidthFull();

            amountFields.put(method.toString(), amountField);

            methodLayout.add(label, amountField);
            methodLayout.setFlexGrow(1, amountField);
            content.add(methodLayout);
        }

        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setSpacing(true);

        Button saveBtn = new Button("Abrir Caja", e -> {
            try {
                Map<String, Double> openingBalance = new HashMap<>();
                for (var entry : amountFields.entrySet()) {
                    openingBalance.put(entry.getKey(), entry.getValue().getValue());
                }

                cashClosingService.openCash(openingBalance);
                Notification.show("Caja abierta exitosamente", 3000, Notification.Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                dialog.close();
                refreshCashStatus();
            } catch (Exception ex) {
                Notification.show("Error: " + ex.getMessage(), 3000, Notification.Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });
        saveBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button cancelBtn = new Button("Cancelar", e -> dialog.close());

        buttonLayout.add(saveBtn, cancelBtn);
        content.add(buttonLayout);

        dialog.add(content);
        dialog.open();
    }

    private void closeCashDialog() {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Cierre de Caja");
        dialog.setWidth("700px");

        VerticalLayout content = new VerticalLayout();
        content.setSpacing(true);
        content.setPadding(true);

        H3 subtitle = new H3("Ingresa los montos reales registrados en cada método de pago");
        subtitle.addClassNames(LumoUtility.FontSize.MEDIUM);
        content.add(subtitle);

        // Show expected amounts
        H3 expectedTitle = new H3("Ventas Esperadas:");
        expectedTitle.addClassNames(LumoUtility.FontSize.SMALL);
        content.add(expectedTitle);

        Map<String, Double> expectedAmounts = cashClosingService.getTodaysSalesByPaymentMethod();
        for (var entry : expectedAmounts.entrySet()) {
            Span span = new Span(entry.getKey() + ": " + String.format("%.2f €", entry.getValue()));
            span.addClassNames(LumoUtility.TextColor.SECONDARY, LumoUtility.FontSize.SMALL);
            content.add(span);
        }

        content.add(new com.vaadin.flow.component.html.Hr());

        // Input fields for real amounts
        H3 realTitle = new H3("Montos Reales:");
        realTitle.addClassNames(LumoUtility.FontSize.MEDIUM, LumoUtility.Margin.Top.MEDIUM);
        content.add(realTitle);

        Map<String, NumberField> realAmountFields = new HashMap<>();

        for (PaymentMethod method : PaymentMethod.values()) {
            HorizontalLayout methodLayout = new HorizontalLayout();
            methodLayout.setAlignItems(FlexComponent.Alignment.CENTER);
            methodLayout.setSpacing(true);

            Span label = new Span(method.toString());
            label.setWidth("150px");
            label.addClassName(LumoUtility.FontWeight.BOLD);

            NumberField amountField = new NumberField();
            amountField.setLabel("Cantidad real");
            amountField.setValue(expectedAmounts.getOrDefault(method.toString(), 0.0));
            amountField.setMin(0);
            amountField.setWidthFull();

            realAmountFields.put(method.toString(), amountField);

            methodLayout.add(label, amountField);
            methodLayout.setFlexGrow(1, amountField);
            content.add(methodLayout);
        }

        // Notes field
        H3 notesTitle = new H3("Notas (opcional):");
        notesTitle.addClassNames(LumoUtility.FontSize.MEDIUM, LumoUtility.Margin.Top.MEDIUM);
        content.add(notesTitle);

        TextArea notesField = new TextArea();
        notesField.setPlaceholder("Agrega observaciones sobre la caja...");
        notesField.setWidthFull();
        notesField.setHeight("100px");
        content.add(notesField);

        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setSpacing(true);
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);

        Button saveBtn = new Button("Cerrar Caja", e -> {
            try {
                Map<String, Double> realAmounts = new HashMap<>();
                for (var entry : realAmountFields.entrySet()) {
                    realAmounts.put(entry.getKey(), entry.getValue().getValue());
                }

                String notes = notesField.getValue();

                CashClosingDto result = cashClosingService.closeCash(realAmounts, notes);

                // Show summary
                showClosingSummary(result);
                dialog.close();
                refreshCashStatus();
            } catch (Exception ex) {
                Notification.show("Error: " + ex.getMessage(), 3000, Notification.Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });
        saveBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);

        Button cancelBtn = new Button("Cancelar", e -> dialog.close());

        buttonLayout.add(saveBtn, cancelBtn);
        content.add(buttonLayout);

        dialog.add(content);
        dialog.open();
    }

    private void showClosingSummary(CashClosingDto closingDto) {
        Dialog summaryDialog = new Dialog();
        summaryDialog.setHeaderTitle("Resumen de Cierre de Caja");
        summaryDialog.setWidth("600px");

        VerticalLayout content = new VerticalLayout();
        content.setSpacing(true);
        content.setPadding(true);

        // Create summary grid
        Grid<PaymentMethodSummary> summaryGrid = new Grid<>(PaymentMethodSummary.class, false);
        summaryGrid.setWidthFull();
        summaryGrid.addThemeVariants(GridVariant.LUMO_COMPACT);

        summaryGrid.addColumn(PaymentMethodSummary::getPaymentMethod)
                .setHeader("Método")
                .setAutoWidth(true);

        summaryGrid.addColumn(summary -> String.format("%.2f €", summary.getExpected()))
                .setHeader("Esperado")
                .setAutoWidth(true);

        summaryGrid.addColumn(summary -> String.format("%.2f €", summary.getReal()))
                .setHeader("Real")
                .setAutoWidth(true);

        summaryGrid.addComponentColumn(summary -> {
            Span diffSpan = new Span(String.format("%.2f €", summary.getDifference()));
            if (summary.getDifference() > 0) {
                diffSpan.getStyle().setColor("green");
                diffSpan.getStyle().setFontWeight("bold");
            } else if (summary.getDifference() < 0) {
                diffSpan.getStyle().setColor("red");
                diffSpan.getStyle().setFontWeight("bold");
            }
            return diffSpan;
        }).setHeader("Diferencia")
                .setAutoWidth(true);

        var summaryList = closingDto.getDifference().entrySet().stream()
                .map(e -> new PaymentMethodSummary(
                        e.getKey(),
                        closingDto.getExpectedAmount().getOrDefault(e.getKey(), 0.0),
                        closingDto.getRealAmount().getOrDefault(e.getKey(), 0.0),
                        e.getValue()
                ))
                .toList();

        summaryGrid.setItems(summaryList);
        content.add(summaryGrid);

        if (closingDto.getNotes() != null && !closingDto.getNotes().isEmpty()) {
            H3 notesTitle = new H3("Notas:");
            content.add(notesTitle);
            Span notesSpan = new Span(closingDto.getNotes());
            content.add(notesSpan);
        }

        Button closeBtn = new Button("Cerrar", e -> summaryDialog.close());
        content.add(closeBtn);

        summaryDialog.add(content);
        summaryDialog.open();
    }

    // Helper classes for displaying data
    private static class PaymentMethodSale {
        private final String paymentMethod;
        private final Double amount;

        PaymentMethodSale(String paymentMethod, Double amount) {
            this.paymentMethod = paymentMethod;
            this.amount = amount;
        }

        public String getPaymentMethod() {
            return paymentMethod;
        }

        public Double getAmount() {
            return amount;
        }
    }

    private static class PaymentMethodSummary {
        private final String paymentMethod;
        private final Double expected;
        private final Double real;
        private final Double difference;

        PaymentMethodSummary(String paymentMethod, Double expected, Double real, Double difference) {
            this.paymentMethod = paymentMethod;
            this.expected = expected;
            this.real = real;
            this.difference = difference;
        }

        public String getPaymentMethod() {
            return paymentMethod;
        }

        public Double getExpected() {
            return expected;
        }

        public Double getReal() {
            return real;
        }

        public Double getDifference() {
            return difference;
        }
    }

    private static class CashClosingHistoryRow {
        private final java.time.LocalDate date;
        private final Map<String, Double> errorsByMethod = new HashMap<>();

        CashClosingHistoryRow(java.time.LocalDate date) {
            this.date = date;
            // Initialize all methods with 0.0
            for (PaymentMethod method : PaymentMethod.values()) {
                errorsByMethod.put(method.toString(), 0.0);
            }
        }

        public java.time.LocalDate getDate() {
            return date;
        }

        public Double getErrorForMethod(String method) {
            return errorsByMethod.getOrDefault(method, 0.0);
        }

        public void setErrorForMethod(String method, Double error) {
            errorsByMethod.put(method, error);
        }
    }
}

