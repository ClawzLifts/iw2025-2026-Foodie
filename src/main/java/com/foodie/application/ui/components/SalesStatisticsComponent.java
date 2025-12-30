package com.foodie.application.ui.components;

import com.foodie.application.dto.SalesStatisticsDto;
import com.foodie.application.service.OrderService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.time.LocalDate;

/**
 * Component for displaying sales statistics by date and product.
 * This component provides a tabbed interface to view sales data aggregated by date or product.
 *
 * @author Foodie Admin Team
 * @version 1.0
 * @since 2025
 */
public class SalesStatisticsComponent extends VerticalLayout {

    private final OrderService orderService;
    private DatePicker startDatePicker;
    private DatePicker endDatePicker;
    private Grid<SalesStatisticsDto> statisticsGrid;
    private Tabs statisticsTabs;
    private VerticalLayout contentContainer;
    private Paragraph noDataMessage;

    public SalesStatisticsComponent(OrderService orderService) {
        this.orderService = orderService;

        addClassName("sales-statistics-component");
        setPadding(true);
        setSpacing(true);
        setWidthFull();

        add(createHeader());
        add(createFilterSection());

        // Create content container for statistics
        contentContainer = new VerticalLayout();
        contentContainer.setPadding(false);
        contentContainer.setSpacing(true);
        contentContainer.setWidthFull();
        add(contentContainer);

        // Show statistics by date by default
        loadStatisticsByDate();
    }

    /**
     * Creates the header section
     */
    private VerticalLayout createHeader() {
        H2 title = new H2("Estadísticas de Ventas");
        title.addClassNames(
                LumoUtility.TextColor.PRIMARY,
                LumoUtility.Margin.Vertical.MEDIUM
        );

        HorizontalLayout headerLayout = new HorizontalLayout();
        headerLayout.setWidthFull();
        headerLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        headerLayout.add(title);

        VerticalLayout headerContainer = new VerticalLayout();
        headerContainer.setPadding(false);
        headerContainer.setSpacing(false);
        headerContainer.add(headerLayout);
        return headerContainer;
    }

    /**
     * Creates the filter section with date pickers and tabs
     */
    private VerticalLayout createFilterSection() {
        // Date filter section
        HorizontalLayout dateFilterLayout = new HorizontalLayout();
        dateFilterLayout.setPadding(true);
        dateFilterLayout.setSpacing(true);
        dateFilterLayout.setAlignItems(FlexComponent.Alignment.END);
        dateFilterLayout.addClassNames(
                LumoUtility.Background.BASE,
                LumoUtility.BorderRadius.MEDIUM,
                LumoUtility.BoxShadow.SMALL,
                LumoUtility.Padding.MEDIUM
        );

        // Initialize date pickers with default range (last 30 days)
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(30);

        startDatePicker = new DatePicker("Fecha Inicio");
        startDatePicker.setValue(startDate);
        startDatePicker.setWidth("200px");

        endDatePicker = new DatePicker("Fecha Fin");
        endDatePicker.setValue(endDate);
        endDatePicker.setWidth("200px");

        Button searchButton = new Button("Buscar", VaadinIcon.SEARCH.create());
        searchButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        searchButton.addClickListener(event -> refreshStatistics());

        dateFilterLayout.add(startDatePicker, endDatePicker, searchButton);

        HorizontalLayout centeredFilterLayout = new HorizontalLayout();
        centeredFilterLayout.setWidthFull();
        centeredFilterLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        centeredFilterLayout.add(dateFilterLayout);

        // Create tabs for switching between date and product view
        Tab dateTab = new Tab("Por Fecha");
        Tab productTab = new Tab("Por Producto");

        statisticsTabs = new Tabs(dateTab, productTab);
        statisticsTabs.setWidthFull();
        statisticsTabs.addSelectedChangeListener(event -> {
            if (event.getSelectedTab() == dateTab) {
                loadStatisticsByDate();
            } else {
                loadStatisticsByProduct();
            }
        });

        VerticalLayout filterSection = new VerticalLayout();
        filterSection.setPadding(false);
        filterSection.setSpacing(true);
        filterSection.add(centeredFilterLayout, statisticsTabs);
        filterSection.setWidthFull();

        return filterSection;
    }

    /**
     * Loads and displays sales statistics by date
     */
    private void loadStatisticsByDate() {
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();

        var statistics = orderService.getSalesStatisticsByDate(startDate, endDate);

        contentContainer.removeAll();
        if (statistics.isEmpty()) {
            noDataMessage = new Paragraph("No hay datos de ventas disponibles para el rango de fechas seleccionado.");
            noDataMessage.addClassNames(LumoUtility.TextColor.SECONDARY);
            contentContainer.add(noDataMessage);
        } else {
            statisticsGrid = new Grid<>(SalesStatisticsDto.class);
            statisticsGrid.setItems(statistics);
            statisticsGrid.setColumns();
            statisticsGrid.addColumn(stat -> stat.getDate()).setHeader("Fecha").setSortable(true);
            statisticsGrid.addColumn(SalesStatisticsDto::getQuantitySold)
                    .setHeader("Cantidad Vendida").setSortable(true);
            statisticsGrid.addColumn(stat -> String.format("$%.2f", stat.getTotalRevenue()))
                    .setHeader("Ingresos Totales").setSortable(true);
            statisticsGrid.addColumn(SalesStatisticsDto::getNumberOfOrders)
                    .setHeader("Número de Pedidos").setSortable(true);
            statisticsGrid.addColumn(stat -> String.format("$%.2f", stat.getAverageOrderValue()))
                    .setHeader("Valor Promedio de Pedido").setSortable(true);

            statisticsGrid.setWidthFull();
            statisticsGrid.setHeight("400px");
            contentContainer.add(statisticsGrid);
        }
    }

    /**
     * Loads and displays sales statistics by product
     */
    private void loadStatisticsByProduct() {
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();

        var statistics = orderService.getSalesStatisticsByProduct(startDate, endDate);

        contentContainer.removeAll();
        if (statistics.isEmpty()) {
            noDataMessage = new Paragraph("No hay datos de ventas disponibles para el rango de fechas seleccionado.");
            noDataMessage.addClassNames(LumoUtility.TextColor.SECONDARY);
            contentContainer.add(noDataMessage);
        } else {
            statisticsGrid = new Grid<>(SalesStatisticsDto.class);
            statisticsGrid.setItems(statistics);
            statisticsGrid.setColumns();
            statisticsGrid.addColumn(SalesStatisticsDto::getProductName)
                    .setHeader("Nombre del Producto").setSortable(true);
            statisticsGrid.addColumn(SalesStatisticsDto::getQuantitySold)
                    .setHeader("Cantidad Vendida").setSortable(true);
            statisticsGrid.addColumn(stat -> String.format("$%.2f", stat.getTotalRevenue()))
                    .setHeader("Ingresos Totales").setSortable(true);
            statisticsGrid.addColumn(SalesStatisticsDto::getNumberOfOrders)
                    .setHeader("Número de Pedidos").setSortable(true);
            statisticsGrid.addColumn(stat -> String.format("$%.2f", stat.getAverageOrderValue()))
                    .setHeader("Valor Promedio por Pedido").setSortable(true);

            statisticsGrid.setWidthFull();
            statisticsGrid.setHeight("400px");
            contentContainer.add(statisticsGrid);
        }
    }

    /**
     * Refreshes the current statistics view
     */
    private void refreshStatistics() {
        Tab selectedTab = statisticsTabs.getSelectedTab();
        if (selectedTab != null && selectedTab.getLabel().equals("Por Fecha")) {
            loadStatisticsByDate();
        } else {
            loadStatisticsByProduct();
        }
    }
}

