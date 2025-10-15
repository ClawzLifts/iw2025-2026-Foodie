package com.foodie.application.base.ui.views;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;

@Route("")
@PageTitle("Inicio | Foodie")
public class MainView extends AppLayout {

    public MainView() {
        createHeader();
        createDrawer();
        setContent(createLandingPage());
    }

    private void createHeader() {
        H1 logo = new H1("🍕 Foodie");
        logo.addClassNames(
                LumoUtility.FontSize.LARGE,
                LumoUtility.Margin.MEDIUM
        );

        var header = new HorizontalLayout(new DrawerToggle(), logo);
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.setWidthFull();
        header.addClassNames(
                LumoUtility.Padding.Vertical.NONE,
                LumoUtility.Padding.Horizontal.MEDIUM
        );

        addToNavbar(header);
    }

    private void createDrawer() {
        // Puedes agregar items de navegación aquí luego
        addToDrawer(new VerticalLayout(
                new Button("Inicio", new Icon(VaadinIcon.HOME)),
                new Button("Recetas", new Icon(VaadinIcon.BOOK)),
                new Button("Favoritos", new Icon(VaadinIcon.HEART))
        ));
    }

    private VerticalLayout createLandingPage() {
        // Hero Section
        VerticalLayout heroSection = new VerticalLayout();
        heroSection.addClassNames(
                LumoUtility.Padding.XLARGE,
                LumoUtility.Background.PRIMARY_10,
                LumoUtility.TextAlignment.CENTER
        );
        heroSection.setWidthFull();
        heroSection.setAlignItems(FlexComponent.Alignment.CENTER);
        heroSection.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        heroSection.setHeight("400px");

        H1 heroTitle = new H1("Descubre el Sabor Auténtico");
        heroTitle.addClassNames(
                LumoUtility.FontSize.XXXLARGE,
                LumoUtility.FontWeight.BOLD,
                LumoUtility.Margin.Bottom.NONE
        );

        H2 heroSubtitle = new H2("Tu guía culinaria para experiencias inolvidables");
        heroSubtitle.addClassNames(
                LumoUtility.FontSize.LARGE,
                LumoUtility.FontWeight.LIGHT,
                LumoUtility.TextColor.SECONDARY
        );

        Button exploreButton = new Button("Explorar Recetas", new Icon(VaadinIcon.SEARCH));
        exploreButton.addClassNames(
                LumoUtility.Margin.Top.LARGE,
                LumoUtility.Padding.LARGE
        );

        heroSection.add(heroTitle, heroSubtitle, exploreButton);

        // Features Section
        VerticalLayout featuresSection = new VerticalLayout();
        featuresSection.addClassNames(
                LumoUtility.Padding.XLARGE,
                LumoUtility.TextAlignment.CENTER
        );
        featuresSection.setWidthFull();
        featuresSection.setAlignItems(FlexComponent.Alignment.CENTER);

        H2 featuresTitle = new H2("¿Por qué elegir Foodie?");
        featuresTitle.addClassNames(
                LumoUtility.FontSize.XXLARGE,
                LumoUtility.FontWeight.BOLD,
                LumoUtility.Margin.Bottom.XLARGE
        );

        HorizontalLayout featuresGrid = new HorizontalLayout();
        featuresGrid.addClassNames(
                LumoUtility.JustifyContent.CENTER,
                LumoUtility.FlexWrap.WRAP,
                LumoUtility.Gap.LARGE
        );
        featuresGrid.setWidthFull();

        // Feature 1
        VerticalLayout feature1 = createFeatureCard(
                VaadinIcon.BOOK,
                "Recetas Exclusivas",
                "Descubre recetas únicas de chefs profesionales y cocineros caseros"
        );

        // Feature 2
        VerticalLayout feature2 = createFeatureCard(
                VaadinIcon.HEART,
                "Tus Favoritos",
                "Guarda tus recetas preferidas y crea tu propia colección personal"
        );

        // Feature 3
        VerticalLayout feature3 = createFeatureCard(
                VaadinIcon.CLOCK,
                "Preparación Rápida",
                "Encuentra recetas por tiempo de preparación y nivel de dificultad"
        );

        featuresGrid.add(feature1, feature2, feature3);
        featuresSection.add(featuresTitle, featuresGrid);

        // CTA Section
        VerticalLayout ctaSection = new VerticalLayout();
        ctaSection.addClassNames(
                LumoUtility.Padding.XLARGE,
                LumoUtility.Background.CONTRAST_5,
                LumoUtility.TextAlignment.CENTER
        );
        ctaSection.setWidthFull();
        ctaSection.setAlignItems(FlexComponent.Alignment.CENTER);

        H2 ctaTitle = new H2("¿Listo para comenzar tu aventura culinaria?");
        ctaTitle.addClassNames(
                LumoUtility.FontSize.XXLARGE,
                LumoUtility.FontWeight.BOLD
        );

        H2 ctaSubtitle = new H2("Únete a nuestra comunidad de foodies");
        ctaSubtitle.addClassNames(
                LumoUtility.FontSize.LARGE,
                LumoUtility.FontWeight.LIGHT,
                LumoUtility.Margin.Bottom.LARGE
        );

        Button joinButton = new Button("Crear Cuenta Gratis", new Icon(VaadinIcon.USER));
        joinButton.addClassNames(
                LumoUtility.Padding.LARGE,
                LumoUtility.FontSize.LARGE
        );

        ctaSection.add(ctaTitle, ctaSubtitle, joinButton);

        // Main container
        VerticalLayout landingPage = new VerticalLayout();
        landingPage.addClassNames(LumoUtility.Padding.NONE);
        landingPage.setSpacing(false);
        landingPage.setWidthFull();

        landingPage.add(heroSection, featuresSection, ctaSection);

        return landingPage;
    }

    private VerticalLayout createFeatureCard(VaadinIcon icon, String title, String description) {
        VerticalLayout card = new VerticalLayout();
        card.addClassNames(
                LumoUtility.Padding.LARGE,
                LumoUtility.Background.BASE,
                LumoUtility.BorderRadius.LARGE,
                LumoUtility.BoxShadow.SMALL,
                LumoUtility.MaxWidth.SCREEN_MEDIUM
        );
        card.setAlignItems(FlexComponent.Alignment.CENTER);
        card.setSpacing(false);

        Icon featureIcon = new Icon(icon);
        featureIcon.addClassNames(
                LumoUtility.FontSize.XXXLARGE,
                LumoUtility.TextColor.PRIMARY,
                LumoUtility.Margin.Bottom.MEDIUM
        );

        H2 featureTitle = new H2(title);
        featureTitle.addClassNames(
                LumoUtility.FontSize.LARGE,
                LumoUtility.FontWeight.BOLD,
                LumoUtility.Margin.Bottom.SMALL,
                LumoUtility.TextAlignment.CENTER
        );

        Div featureDescription = new Div();
        featureDescription.setText(description);
        featureDescription.addClassNames(
                LumoUtility.TextColor.SECONDARY,
                LumoUtility.TextAlignment.CENTER
        );

        card.add(featureIcon, featureTitle, featureDescription);
        return card;
    }
}