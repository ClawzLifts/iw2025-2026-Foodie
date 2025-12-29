package com.foodie.application.ui.views;

import com.foodie.application.dto.MenuDto;
import com.foodie.application.dto.MenuItemDisplayDto;
import com.foodie.application.ui.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility;

import com.foodie.application.service.MenuService;
import com.foodie.application.service.CartService;

import java.util.List;

@PageTitle("Menu")
@AnonymousAllowed
@Route(value = "foodmenu", layout = MainLayout.class)
public class MenuView extends HorizontalLayout {

    private final MenuService menuService;
    private final CartService cartService;

    public MenuView(MenuService menuService, CartService cartService) {
        this.menuService = menuService;
        this.cartService = cartService;

        List<MenuDto> menus = menuService.getMenus();

        setSizeFull();
        setPadding(false);
        setSpacing(false);

        // Sidebar
        VerticalLayout sidebar = createSidebar(menus);
        sidebar.setWidth("250px");

        // Contenido principal
        VerticalLayout mainContent = createMainContent(menus);

        add(sidebar, mainContent);
        setFlexGrow(1, mainContent);
    }

    private VerticalLayout createSidebar(List<MenuDto> menus) {
        VerticalLayout sidebar = new VerticalLayout();
        sidebar.setPadding(true);

        H3 menuTitle = new H3("Categorías");
        sidebar.add(menuTitle);

        menus.forEach(menu -> {
            Button button = new Button(menu.getName());
            button.addClickListener(e -> getUI().ifPresent(ui -> ui.getPage().executeJs(
                        "document.getElementById($0).scrollIntoView({behavior: 'smooth'});",
                        "menu-" + menu.getId()
                )));
            sidebar.add(button);
        });

        return sidebar;
    }

    private VerticalLayout createMainContent(List<MenuDto> menus) {
        VerticalLayout mainContent = new VerticalLayout();
        mainContent.setSizeFull();
        mainContent.getStyle()
                .set("overflow", "auto")
                .set("height", "100%");
        mainContent.setPadding(true);
        mainContent.setSpacing(false);
        mainContent.setAlignItems(Alignment.CENTER);

        if(menus != null && !menus.isEmpty()){
            menus.forEach(menu -> {

                H2 menuTitle = new H2(menu.getName());
                menuTitle.getStyle().set("text-align", "center").set("font-size", "50px");
                menuTitle.getElement().setAttribute("id", "menu-" +  menu.getId());

                FlexLayout cardContainer = new FlexLayout();
                cardContainer.addClassName("menu-grid");
                cardContainer.setFlexWrap(FlexLayout.FlexWrap.WRAP);
                cardContainer.setJustifyContentMode(FlexLayout.JustifyContentMode.CENTER);
                cardContainer.setWidthFull();
                cardContainer.addClassNames(LumoUtility.Gap.MEDIUM);

                // Usar el nuevo método que devuelve MenuItemDisplayDto con descuentos
                List<MenuItemDisplayDto> menuItems = menuService.getMenuItemsForDisplay(menu.getId());

                if (menuItems != null) {
                    for (MenuItemDisplayDto item : menuItems) {
                        cardContainer.add(createProductCard(item));
                    }
                }

                VerticalLayout sectionLayout = new VerticalLayout(menuTitle, cardContainer);
                sectionLayout.setWidthFull();
                sectionLayout.setAlignItems(Alignment.CENTER);
                sectionLayout.getStyle().set("margin-bottom", "100px");

                mainContent.add(sectionLayout);
            });
        }

        return mainContent;
    }

    /**
     * Creates a product card component with pricing and featured badge
     *
     * @param item the MenuItemDisplayDto containing product information
     * @return a Div component representing the product card
     */
    private Div createProductCard(MenuItemDisplayDto item) {
        Div card = new Div();
        card.addClassName("product-card");
        card.getStyle().set("max-width", "400px")
                .set("box-shadow", "0 2px 6px rgba(0,0,0,0.1)")
                .set("position", "relative");

        // Badge para destacados
        if (item.getFeatured() != null && item.getFeatured()) {
            Div badge = new Div("⭐ DESTACADO");
            badge.getStyle()
                    .set("position", "absolute")
                    .set("top", "10px")
                    .set("right", "10px")
                    .set("background-color", "#FFD700")
                    .set("color", "#000")
                    .set("padding", "5px 10px")
                    .set("border-radius", "5px")
                    .set("font-weight", "bold")
                    .set("font-size", "12px")
                    .set("z-index", "10");
            card.add(badge);
        }

        Image image = new Image(item.getImageUrl(), item.getName());
        image.addClassName("product-image");
        image.getStyle().setWidth("350px").setHeight("350px");

        H1 name = new H1(item.getName());
        name.addClassName("product-name");
        name.getStyle().set("text-align", "center").set("font-size", "26px");

        Paragraph desc = new Paragraph(item.getDescription());
        desc.addClassName("product-description");

        // Sección de precios
        HorizontalLayout priceLayout = new HorizontalLayout();
        priceLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        priceLayout.setSpacing(true);

        if (item.getDiscountPercentage() != null && item.getDiscountPercentage() > 0) {
            // Mostrar precio original tachado
            Paragraph originalPrice = new Paragraph(String.format("€%.2f", item.getOriginalPrice()));
            originalPrice.getStyle()
                    .set("text-decoration", "line-through")
                    .set("color", "#999")
                    .set("font-size", "14px")
                    .set("margin", "0");

            // Mostrar precio con descuento en verde
            Paragraph discountedPrice = new Paragraph(String.format("€%.2f", item.getDiscountedPrice()));
            discountedPrice.getStyle()
                    .set("font-weight", "bold")
                    .set("color", "#28a745")
                    .set("font-size", "18px")
                    .set("margin", "0");

            // Badge de descuento
            Span discountBadge = new Span(item.getDiscountPercentage() + "% OFF");
            discountBadge.getStyle()
                    .set("background-color", "#dc3545")
                    .set("color", "white")
                    .set("padding", "3px 8px")
                    .set("border-radius", "3px")
                    .set("font-size", "12px")
                    .set("font-weight", "bold")
                    .set("margin-left", "10px");

            priceLayout.add(originalPrice, discountedPrice, discountBadge);
        } else {
            // Sin descuento, mostrar solo el precio
            Paragraph price = new Paragraph(String.format("€%.2f", item.getOriginalPrice()));
            price.addClassName("product-price");
            price.getStyle().set("font-weight", "bold");
            priceLayout.add(price);
        }

        Button addButton = new Button("Añadir al carrito");
        addButton.addClickListener(e -> addToCart(item));
        addButton.addClassName("add-button");

        VerticalLayout content = new VerticalLayout(name, desc, priceLayout, addButton);
        content.setPadding(false);
        content.setSpacing(false);
        content.setAlignItems(Alignment.CENTER);

        card.add(image, content);
        return card;
    }

    /**
     * Adds a product to the cart with its discounted price if applicable
     *
     * @param item the MenuItemDisplayDto to add to cart
     */
    private void addToCart(MenuItemDisplayDto item) {
        // Usar el precio con descuento si existe
        Double priceToAdd = (item.getDiscountPercentage() != null && item.getDiscountPercentage() > 0)
                ? item.getDiscountedPrice()
                : item.getOriginalPrice();

        cartService.addToCart(
                item.getProductId(),
                item.getName(),
                priceToAdd,
                1
        );

        // Refresh the cart display in the header
        getUI().ifPresent(ui -> {
            MainLayout layout = (MainLayout) ui.getChildren()
                    .filter(c -> c instanceof MainLayout)
                    .findFirst()
                    .orElse(null);
            if (layout != null) {
                layout.refreshCart();
            }
        });
    }
}
