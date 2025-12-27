package com.foodie.application.ui.views;

import com.foodie.application.dto.MenuDto;
import com.foodie.application.ui.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility;

import com.foodie.application.dto.ProductDto;
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
            Button button = new Button(menu.getName(), e -> {
                getUI().ifPresent(ui -> ui.getPage().executeJs(
                        "document.getElementById($0).scrollIntoView({behavior: 'smooth'});",
                        "menu-" + menu.getId()
                ));
            });
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

                List<ProductDto> products = menuService.getProducts(menu.getId());

                if (products != null) {
                    for (ProductDto product : products) {
                        cardContainer.add(createProductCard(product));
                        cardContainer.addClassNames(LumoUtility.Gap.MEDIUM);
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

    private Div createProductCard(ProductDto product) {
        Div card = new Div();
        card.addClassName("product-card");
        card.getStyle().set("max-width", "400px")
                .set("box-shadow", "0 2px 6px rgba(0,0,0,0.1)");

        Image image = new Image(product.getImageUrl(), product.getName());
        image.addClassName("product-image");
        image.getStyle().setWidth("350px").setHeight("350px");

        H1 name = new H1(product.getName());
        name.addClassName("product-name");
        name.getStyle().set("text-align", "center").set("font-size", "26px");

        Paragraph desc = new Paragraph(product.getDescription());
        desc.addClassName("product-description");

        Paragraph price = new Paragraph(String.format("€%.2f", product.getPrice()));
        price.addClassName("product-price");
        price.getStyle().set("font-weight", "bold");

        Button addButton = new Button("Añadir al carrito", e -> addToCart(product));
        addButton.addClassName("add-button");

        VerticalLayout content = new VerticalLayout(name, desc, price, addButton);
        content.setPadding(false);
        content.setSpacing(false);
        content.setAlignItems(Alignment.CENTER);

        card.add(image, content);
        return card;
    }

    private void addToCart(ProductDto product) {
        cartService.addToCart(
                product.getId(),
                product.getName(),
                product.getPrice(),
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

    private void filterByCategory(String category) {
        System.out.println("Filtrando por: " + category);
    }
}
