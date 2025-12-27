package com.foodie.application.ui;

import com.vaadin.flow.component.avatar.Avatar;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

import com.vaadin.flow.theme.lumo.LumoUtility;
import com.foodie.application.service.CartService;
import com.foodie.application.ui.components.ShoppingCartComponent;
import org.springframework.beans.factory.annotation.Autowired;

public class MainLayout extends AppLayout {

    private final CartService cartService;
    private ShoppingCartComponent shoppingCart;

    @Autowired
    public MainLayout(CartService cartService) {
        this.cartService = cartService;
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

        Avatar avatar = new Avatar();

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
        header.addToEnd(shoppingCart, avatar);

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
