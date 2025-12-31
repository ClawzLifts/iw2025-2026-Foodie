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
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.VaadinServletRequest;
import com.vaadin.flow.theme.lumo.LumoUtility;
import com.foodie.application.service.CartService;
import com.foodie.application.service.UserService;
import com.foodie.application.ui.components.ShoppingCartComponent;
import com.foodie.application.ui.views.MainView;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;

public class MainLayout extends AppLayout {

    private final CartService cartService;
    private final UserService userService;
    private ShoppingCartComponent shoppingCart;

    public MainLayout(CartService cartService, UserService userService) {
        this.cartService = cartService;
        this.userService = userService;

        // Add horizontal margins to the layout
        getStyle()
                .set("padding-left", "25rem")
                .set("padding-right", "25rem");

        // Header
        createHeader();
    }

    private void createHeader() {


        // Create a RouterLink for the title that navigates to the landing page
        RouterLink titleLink = new RouterLink("Foodie 🍔", MainView.class);
        H1 title = new H1(titleLink);
        title.addClassNames(
                LumoUtility.FontSize.LARGE,
                LumoUtility.Margin.MEDIUM,
                LumoUtility.FontWeight.BOLD,
                LumoUtility.Margin.Left.XLARGE
        );
        // Remove underline from link
        titleLink.getStyle().set("text-decoration", "none").set("color", "inherit");

        // Create shopping cart component before using it
        shoppingCart = new ShoppingCartComponent(cartService);

        HorizontalLayout header = new HorizontalLayout(title);
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

            // Add Admin Panel option if user is admin
            if (currentUser.getRole() != null && "admin".equalsIgnoreCase(currentUser.getRole().getName())) {
                contextMenu.addItem("⚙️ Panel de Administración", e -> {
                    getUI().ifPresent(ui -> ui.navigate("admin"));
                });
            }

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
