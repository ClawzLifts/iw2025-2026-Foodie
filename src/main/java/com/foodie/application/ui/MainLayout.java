package com.foodie.application.ui;

import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.server.VaadinServletRequest;
import com.vaadin.flow.theme.lumo.LumoUtility;
import com.foodie.application.service.CartService;
import com.foodie.application.service.UserService;
import com.foodie.application.ui.components.ShoppingCartComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;

public class MainLayout extends AppLayout {

    private final CartService cartService;
    private final UserService userService;
    private ShoppingCartComponent shoppingCart;

    @Autowired
    public MainLayout(CartService cartService, UserService userService) {
        this.cartService = cartService;
        this.userService = userService;
        // Header
        createHeader();
    }

    private void createHeader() {
        DrawerToggle toggle = new DrawerToggle();

        H1 title = new H1("Foodie 🍔");
        title.addClassNames(
                LumoUtility.FontSize.LARGE,
                LumoUtility.Margin.MEDIUM
        );

        // Create shopping cart component
        shoppingCart = new ShoppingCartComponent(cartService);

        HorizontalLayout header = new HorizontalLayout(toggle, title);
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.expand(title);
        header.setWidthFull();
        header.addClassNames(
                LumoUtility.Padding.Horizontal.MEDIUM,
                LumoUtility.Padding.Vertical.SMALL,
                LumoUtility.Background.BASE,
                LumoUtility.BoxShadow.SMALL
        );

        // Add shopping cart
        header.addToEnd(shoppingCart);

        // Check if user is authenticated
        var currentUser = userService.getCurrentUser();
        if (currentUser != null) {
            // User is logged in - show avatar with dropdown menu
            Avatar avatar = new Avatar();
            avatar.setName(currentUser.getUsername());
            avatar.getStyle().set("cursor", "pointer");

            ContextMenu contextMenu = new ContextMenu(avatar);
            contextMenu.setOpenOnClick(true);

            // Add profile option
            contextMenu.addItem("👤 " + currentUser.getUsername(), e -> {
                // Navigate to profile - you can implement this later
                getUI().ifPresent(ui -> ui.navigate("profile"));
            });

            contextMenu.addItem("📦 Mis Pedidos", e -> {
                getUI().ifPresent(ui -> ui.navigate("myorders"));
            });

            // Add separator
            contextMenu.add(new Span(""));

            // Add logout option
            contextMenu.addItem("🚪 Cerrar Sesión", e -> {
                var request = VaadinServletRequest.getCurrent().getHttpServletRequest();
                new SecurityContextLogoutHandler().logout(request, null, null);
                getUI().ifPresent(ui -> {
                    ui.getSession().close();
                    ui.navigate("login");
                });
            });

            header.addToEnd(avatar);
        } else {
            // User is not logged in - show login button
            Button loginButton = new Button("Iniciar Sesión", new Icon(VaadinIcon.USER));
            loginButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            loginButton.addClickListener(e -> {
                getUI().ifPresent(ui -> ui.navigate("login"));
            });

            header.addToEnd(loginButton);
        }

        addToNavbar(header);
    }

    /**
     * Refreshes the shopping cart display.
     * Call this method after adding items to the cart to update the UI.
     */
    public void refreshCart() {
        if (shoppingCart != null) {
            shoppingCart.refresh();
        }
    }
}
