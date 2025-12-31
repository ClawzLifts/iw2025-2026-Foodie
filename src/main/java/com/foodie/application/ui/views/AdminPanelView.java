package com.foodie.application.ui.views;

import com.foodie.application.service.AllergenService;
import com.foodie.application.service.CashClosingService;
import com.foodie.application.service.EstablishmentService;
import com.foodie.application.service.IngredientService;
import com.foodie.application.service.MenuItemService;
import com.foodie.application.service.MenuService;
import com.foodie.application.service.OrderService;
import com.foodie.application.service.ProductService;
import com.foodie.application.service.UserService;
import com.foodie.application.ui.MainLayout;
import com.foodie.application.ui.components.CashClosingComponent;
import com.foodie.application.ui.components.EstablishmentManagementComponent;
import com.foodie.application.ui.components.MenuManagementComponent;
import com.foodie.application.ui.components.OrderManagementComponent;
import com.foodie.application.ui.components.ProductManagementComponent;
import com.foodie.application.ui.components.SalesStatisticsComponent;
import com.foodie.application.ui.components.UserManagementComponent;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;

/**
 * Admin Panel View for managing menus, products, and orders.
 * This view provides a tabbed interface to manage different aspects of the application.
 *
 * @author Foodie Admin Team
 * @version 2.0
 * @since 2025
 */
@Route(value = "admin", layout = MainLayout.class)
@PageTitle("Panel de Administración | Foodie")
public class AdminPanelView extends VerticalLayout {

    private final MenuService menuService;
    private final MenuItemService menuItemService;
    private final ProductService productService;
    private final OrderService orderService;
    private final AllergenService allergenService;
    private final IngredientService ingredientService;
    private final UserService userService;
    private final CashClosingService cashClosingService;
    private final EstablishmentService establishmentService;

    private VerticalLayout contentContainer;

    public AdminPanelView(MenuService menuService, MenuItemService menuItemService,
                         ProductService productService, OrderService orderService, AllergenService allergenService,
                         IngredientService ingredientService, UserService userService, CashClosingService cashClosingService,
                         EstablishmentService establishmentService) {
        this.menuService = menuService;
        this.menuItemService = menuItemService;
        this.productService = productService;
        this.orderService = orderService;
        this.allergenService = allergenService;
        this.ingredientService = ingredientService;
        this.userService = userService;
        this.cashClosingService = cashClosingService;
        this.establishmentService = establishmentService;

        addClassName("admin-panel-view");
        setSizeFull();
        setPadding(true);
        setSpacing(true);

        add(createHeader());
        add(createTabNavigation());

        // Create content container
        contentContainer = new VerticalLayout();
        contentContainer.setPadding(false);
        contentContainer.setSpacing(true);
        contentContainer.setWidthFull();
        contentContainer.setFlexGrow(1, contentContainer);
        add(contentContainer);

        // Show initial tab (Menus)
        showMenusTab();
    }

    /**
     * Creates the header section with title
     */
    private HorizontalLayout createHeader() {
        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);

        return header;
    }

    /**
     * Creates the tabs navigation for switching between sections
     */
    private HorizontalLayout createTabNavigation() {
        Tab menusTab = new Tab();
        menusTab.add(new Icon(VaadinIcon.SPOON), new Span("Menús"));

        Tab productsTab = new Tab();
        productsTab.add(new Icon(VaadinIcon.PACKAGE), new Span("Productos"));

        Tab usersTab = new Tab();
        usersTab.add(new Icon(VaadinIcon.USERS), new Span("Usuarios"));

        Tab ordersTab = new Tab();
        ordersTab.add(new Icon(VaadinIcon.CART), new Span("Pedidos"));

        Tab statisticsTab = new Tab();
        statisticsTab.add(new Icon(VaadinIcon.BAR_CHART), new Span("Estadísticas"));

        Tab cashClosingTab = new Tab();
        cashClosingTab.add(new Icon(VaadinIcon.WALLET), new Span("Control de Caja"));

        Tab establishmentTab = new Tab();
        establishmentTab.add(new Icon(VaadinIcon.BUILDING), new Span("Establecimiento"));

        Tabs tabs = new Tabs(menusTab, productsTab, usersTab, ordersTab, statisticsTab, cashClosingTab, establishmentTab);
        tabs.addClassNames(
                LumoUtility.BorderRadius.MEDIUM,
                LumoUtility.Background.BASE,
                LumoUtility.BoxShadow.SMALL
        );

        // Centrar tabs
        HorizontalLayout tabsWrapper = new HorizontalLayout(tabs);
        tabsWrapper.setWidthFull();
        tabsWrapper.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        tabs.addSelectedChangeListener(event -> {
            Tab selectedTab = event.getSelectedTab();
            if (selectedTab == menusTab) {
                showMenusTab();
            } else if (selectedTab == productsTab) {
                showProductsTab();
            } else if (selectedTab == usersTab) {
                showUsersTab();
            } else if (selectedTab == ordersTab) {
                showOrdersTab();
            } else if (selectedTab == statisticsTab) {
                showStatisticsTab();
            } else if (selectedTab == cashClosingTab) {
                showCashClosingTab();
            } else if (selectedTab == establishmentTab) {
                showEstablishmentTab();
            }
        });

        return tabsWrapper;

    }

    /**
     * Displays the menus management tab
     */
    private void showMenusTab() {
        contentContainer.removeAll();
        MenuManagementComponent menuComponent = new MenuManagementComponent(menuService, menuItemService, productService);
        contentContainer.add(menuComponent);
    }

    /**
     * Displays the products management tab
     */
    private void showProductsTab() {
        contentContainer.removeAll();
        ProductManagementComponent productComponent = new ProductManagementComponent(productService, allergenService, ingredientService);
        contentContainer.add(productComponent);
    }

    /**
     * Displays the users management tab
     */
    private void showUsersTab() {
        contentContainer.removeAll();
        UserManagementComponent userComponent = new UserManagementComponent(userService);
        contentContainer.add(userComponent);
    }

    /**
     * Displays the orders management tab
     */
    private void showOrdersTab() {
        contentContainer.removeAll();
        OrderManagementComponent orderComponent = new OrderManagementComponent(orderService);
        contentContainer.add(orderComponent);
    }

    /**
     * Displays the sales statistics tab
     */
    private void showStatisticsTab() {
        contentContainer.removeAll();
        SalesStatisticsComponent statisticsComponent = new SalesStatisticsComponent(orderService);
        contentContainer.add(statisticsComponent);
    }

    /**
     * Displays the cash closing tab
     */
    private void showCashClosingTab() {
        contentContainer.removeAll();
        CashClosingComponent cashClosingComponent = new CashClosingComponent(cashClosingService);
        contentContainer.add(cashClosingComponent);
    }

    /**
     * Displays the establishment management tab
     */
    private void showEstablishmentTab() {
        contentContainer.removeAll();
        EstablishmentManagementComponent establishmentComponent = new EstablishmentManagementComponent(establishmentService);
        contentContainer.add(establishmentComponent);
    }
}

