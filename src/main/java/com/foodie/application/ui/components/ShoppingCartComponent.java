package com.foodie.application.ui.components;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.theme.lumo.LumoUtility;
import com.foodie.application.domain.ProductList;
import com.foodie.application.service.CartService;

import java.util.List;

/**
 * Shopping cart component for the header.
 * <p>
 * Displays a shopping cart icon with an item count badge.
 * When clicked, opens a popover showing all cart items, total price,
 * and options to view cart or checkout.
 * </p>
 */
public class ShoppingCartComponent extends Div {

    private final CartService cartService;
    private final Span cartBadge;
    private final Dialog cartDialog;
    private final VerticalLayout cartContent;

    /**
     * Creates a new shopping cart component.
     *
     * @param cartService the cart service for managing cart data
     */
    public ShoppingCartComponent(CartService cartService) {
        this.cartService = cartService;

        // Create the cart icon button
        Icon cartIcon = new Icon(VaadinIcon.CART);
        cartIcon.getStyle().set("cursor", "pointer");

        // Create badge showing item count
        cartBadge = new Span();
        cartBadge.addClassName("cart-badge");
        cartBadge.addClassNames(
                LumoUtility.Background.ERROR,
                LumoUtility.TextColor.PRIMARY_CONTRAST
        );
        cartBadge.getStyle()
                .set("position", "absolute")
                .set("top", "-8px")
                .set("right", "-8px")
                .set("min-width", "24px")
                .set("text-align", "center")
                .set("font-weight", "bold")
                .set("font-size", "12px")
                .set("border-radius", "50%")
                .set("padding", "2px 6px");

        // Cart icon container
        Div cartIconContainer = new Div(cartIcon, cartBadge);
        cartIconContainer.getStyle()
                .set("position", "relative")
                .set("display", "inline-block")
                .set("cursor", "pointer");

        // Create cart dialog content
        cartContent = new VerticalLayout();
        cartContent.setPadding(true);
        cartContent.setSpacing(true);
        cartContent.setWidthFull();
        cartContent.setMaxWidth("400px");

        // Create dialog
        cartDialog = new Dialog();
        cartDialog.setHeaderTitle("Carrito de Compras");
        cartDialog.setWidth("400px");
        cartDialog.add(cartContent);
        cartDialog.getFooter().add(new Button("Cerrar", e -> cartDialog.close()));

        // Add click listener to icon
        cartIconContainer.addClickListener(e -> {
            updateCartDisplay();
            cartDialog.open();
        });

        add(cartIconContainer);

        // Update badge initially
        updateBadge();
    }

    /**
     * Updates the cart item count badge.
     */
    private void updateBadge() {
        Integer itemCount = cartService.getCartItemCount();
        cartBadge.setText(itemCount.toString());
        cartBadge.setVisible(itemCount > 0);
    }

    /**
     * Updates the entire cart display with current items.
     */
    private void updateCartDisplay() {
        cartContent.removeAll();
        updateBadge();

        List<ProductList> items = cartService.getCartItems();

        if (items.isEmpty()) {
            Span emptyMessage = new Span("Tu carrito está vacío");
            emptyMessage.addClassNames(LumoUtility.TextColor.SECONDARY);
            cartContent.add(emptyMessage);
        } else {
            // Title
            H3 title = new H3("Carrito de Compras");
            title.getStyle().setMargin("0");
            cartContent.add(title);

            // Items list
            VerticalLayout itemsList = new VerticalLayout();
            itemsList.setPadding(false);
            itemsList.setSpacing(true);

            for (ProductList item : items) {
                Div itemDiv = createCartItemDiv(item);
                itemsList.add(itemDiv);
            }

            cartContent.add(itemsList);

            // Separator
            Div separator = new Div();
            separator.getStyle()
                    .set("height", "1px")
                    .set("background-color", "var(--lumo-contrast-20pct)")
                    .setMarginTop("10px")
                    .setMarginBottom("10px");
            cartContent.add(separator);

            // Total price
            Double total = cartService.getCartTotal();
            Div totalDiv = new Div();
            totalDiv.getStyle()
                    .set("display", "flex")
                    .set("justify-content", "space-between")
                    .set("font-weight", "bold")
                    .set("font-size", "16px");

            Span totalLabel = new Span("Total:");
            Span totalPrice = new Span(String.format("€%.2f", total));
            totalPrice.addClassNames(LumoUtility.TextColor.SUCCESS);

            totalDiv.add(totalLabel, totalPrice);
            cartContent.add(totalDiv);

            // Buttons
            Div buttonContainer = new Div();
            buttonContainer.getStyle()
                    .set("display", "flex")
                    .set("gap", "8px")
                    .setMarginTop("10px");

            Button viewCartButton = new Button("Ver Carrito", e -> {
                cartDialog.close();
                // Navigate to cart view
                // getUI().ifPresent(ui -> ui.navigate("cart"));
            });
            viewCartButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            viewCartButton.setWidth("48%");

            Button checkoutButton = new Button("Comprar", e -> {
                cartDialog.close();
                // Navigate to checkout
                // getUI().ifPresent(ui -> ui.navigate("checkout"));
            });
            checkoutButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
            checkoutButton.setWidth("48%");

            buttonContainer.add(viewCartButton, checkoutButton);
            cartContent.add(buttonContainer);
        }
    }

    /**
     * Creates a visual representation of a cart item.
     *
     * @param item the product list item
     * @return a Div containing the item information
     */
    private Div createCartItemDiv(ProductList item) {
        Div itemDiv = new Div();
        itemDiv.addClassName("cart-item");
        itemDiv.getStyle()
                .set("padding", "8px")
                .set("background-color", "var(--lumo-contrast-10pct)")
                .set("border-radius", "4px")
                .set("display", "flex")
                .set("justify-content", "space-between")
                .set("align-items", "center");

        // Item info
        Div infoDiv = new Div();
        infoDiv.getStyle()
                .set("display", "flex")
                .set("flex-direction", "column")
                .set("flex", "1");

        Span nameSpan = new Span(item.getProductName());
        nameSpan.getStyle().set("font-weight", "500");

        Span quantityPrice = new Span(
                String.format("%d x €%.2f", item.getQuantity(), item.getPrice())
        );
        quantityPrice.addClassNames(LumoUtility.TextColor.SECONDARY);
        quantityPrice.getStyle().set("font-size", "13px");

        infoDiv.add(nameSpan, quantityPrice);

        // Remove button
        Button removeButton = new Button(new Icon(VaadinIcon.TRASH));
        removeButton.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_ERROR);
        removeButton.setAriaLabel("Eliminar del carrito");
        removeButton.addClickListener(e -> {
            cartService.removeFromCart(item.getProductId());
            updateCartDisplay();
        });

        itemDiv.add(infoDiv, removeButton);
        return itemDiv;
    }

    /**
     * Refreshes the cart display when cart is modified externally.
     */
    public void refresh() {
        updateBadge();
        if (cartDialog.isOpened()) {
            updateCartDisplay();
        }
    }
}

