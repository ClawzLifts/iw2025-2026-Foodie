package com.foodie.application.ui.views;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.theme.lumo.LumoUtility;

@Route("")
@PageTitle("Bar Casa Manteca | Foodie")
public class MainView extends AppLayout {

    public MainView() {
        createHeader();
        createDrawer();
        setContent(createLandingPage());
    }

    private void createHeader() {
        H1 logo = new H1("🍖 Foodie");
        logo.addClassNames(
                LumoUtility.FontSize.LARGE,
                LumoUtility.Margin.MEDIUM
        );

        Button orderButton = new Button("Hacer Pedido", new Icon(VaadinIcon.CART));
        orderButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        orderButton.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate("foodmenu")));

        var header = new HorizontalLayout(new DrawerToggle(), logo);
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.expand(logo);
        header.setWidthFull();
        header.addClassNames(
                LumoUtility.Padding.Vertical.NONE,
                LumoUtility.Padding.Horizontal.MEDIUM
        );
        header.addToEnd(orderButton);

        addToNavbar(header);
    }

    private void createDrawer() {
        Button pedidosButton = new Button("Pedidos Online", new Icon(VaadinIcon.CART));
        pedidosButton.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate("foodmenu")));

        addToDrawer(new VerticalLayout(
                new Button("Inicio", new Icon(VaadinIcon.HOME)),
                new Button("Menú", new Icon(VaadinIcon.BOOK)),
                new Button("Sobre Nosotros", new Icon(VaadinIcon.INFO)),
                new Button("Contacto", new Icon(VaadinIcon.PHONE)),
                pedidosButton
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

        H1 heroTitle = new H1("Bienvenido a Casa Manteca");
        heroTitle.addClassNames(
                LumoUtility.FontSize.XXXLARGE,
                LumoUtility.FontWeight.BOLD,
                LumoUtility.Margin.Bottom.NONE
        );

        H2 heroSubtitle = new H2("Tradición gaditana desde 1953");
        heroSubtitle.addClassNames(
                LumoUtility.FontSize.LARGE,
                LumoUtility.FontWeight.LIGHT,
                LumoUtility.TextColor.SECONDARY
        );

        Button orderCTAButton = new Button("Realiza tu Pedido Online", new Icon(VaadinIcon.CART));
        orderCTAButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_LARGE);
        orderCTAButton.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate("foodmenu")));
        orderCTAButton.addClassNames(LumoUtility.Margin.Top.LARGE);

        heroSection.add(heroTitle, heroSubtitle, orderCTAButton);

        // About Section
        VerticalLayout aboutSection = new VerticalLayout();
        aboutSection.addClassNames(
                LumoUtility.Padding.XLARGE,
                LumoUtility.TextAlignment.CENTER
        );
        aboutSection.setWidthFull();
        aboutSection.setAlignItems(FlexComponent.Alignment.CENTER);

        H2 aboutTitle = new H2("Nuestro Legado Gastronómico");
        aboutTitle.addClassNames(
                LumoUtility.FontSize.XXLARGE,
                LumoUtility.FontWeight.BOLD,
                LumoUtility.Margin.Bottom.LARGE
        );

        Paragraph aboutText = new Paragraph(
                "Casa Manteca es un icono culinario de Cádiz con más de 70 años de historia. " +
                "Especializado en montaditos de atún, jamón ibérico y conservas gourmet, " +
                "Casa Manteca mantiene la esencia de la gastronomía gaditana tradicional combinada con " +
                "una experiencia moderna. Ahora, disfruta de nuestras delicias desde la comodidad de tu hogar."
        );
        aboutText.addClassNames(
                LumoUtility.FontSize.LARGE,
                LumoUtility.TextColor.SECONDARY,
                LumoUtility.MaxWidth.SCREEN_MEDIUM
        );

        aboutSection.add(aboutTitle, aboutText);

        // Specialties Section
        VerticalLayout specialtiesSection = new VerticalLayout();
        specialtiesSection.addClassNames(
                LumoUtility.Padding.XLARGE,
                LumoUtility.Background.CONTRAST_5,
                LumoUtility.TextAlignment.CENTER
        );
        specialtiesSection.setWidthFull();
        specialtiesSection.setAlignItems(FlexComponent.Alignment.CENTER);

        H2 specialtiesTitle = new H2("Nuestras Especialidades");
        specialtiesTitle.addClassNames(
                LumoUtility.FontSize.XXLARGE,
                LumoUtility.FontWeight.BOLD,
                LumoUtility.Margin.Bottom.XLARGE
        );

        HorizontalLayout specialtiesGrid = new HorizontalLayout();
        specialtiesGrid.addClassNames(
                LumoUtility.JustifyContent.CENTER,
                LumoUtility.FlexWrap.WRAP,
                LumoUtility.Gap.LARGE
        );
        specialtiesGrid.setWidthFull();

        // Specialty 1 - Montaditos de Atún
        VerticalLayout specialty1 = createSpecialtyCard(
                VaadinIcon.SPOON,
                "Montaditos de Atún",
                "Nuestro emblema. Atún de la mejor calidad sobre pan tostado con nuestro toque especial"
        );

        // Specialty 2 - Jamón Ibérico
        VerticalLayout specialty2 = createSpecialtyCard(
                VaadinIcon.CUTLERY,
                "Jamón Ibérico",
                "Jamón de pata negra de máxima calidad en raciones y montaditos"
        );

        // Specialty 3 - Conservas Gourmet
        VerticalLayout specialty3 = createSpecialtyCard(
                VaadinIcon.PACKAGE,
                "Conservas Gourmet",
                "Selección de conservas artesanales y productos de lujo para llevar"
        );

        specialtiesGrid.add(specialty1, specialty2, specialty3);
        specialtiesSection.add(specialtiesTitle, specialtiesGrid);

        // Why Order Online Section
        VerticalLayout whySection = new VerticalLayout();
        whySection.addClassNames(
                LumoUtility.Padding.XLARGE,
                LumoUtility.TextAlignment.CENTER
        );
        whySection.setWidthFull();
        whySection.setAlignItems(FlexComponent.Alignment.CENTER);

        H2 whyTitle = new H2("¿Por qué Pedir Online?");
        whyTitle.addClassNames(
                LumoUtility.FontSize.XXLARGE,
                LumoUtility.FontWeight.BOLD,
                LumoUtility.Margin.Bottom.XLARGE
        );

        HorizontalLayout benefitsGrid = new HorizontalLayout();
        benefitsGrid.addClassNames(
                LumoUtility.JustifyContent.CENTER,
                LumoUtility.FlexWrap.WRAP,
                LumoUtility.Gap.LARGE
        );
        benefitsGrid.setWidthFull();

        benefitsGrid.add(
                createBenefitCard(VaadinIcon.CLOCK, "Comodidad", "Pide desde casa en cualquier momento"),
                createBenefitCard(VaadinIcon.TRUCK, "Entrega Rápida", "Recibe tu pedido en poco tiempo"),
                createBenefitCard(VaadinIcon.CREDIT_CARD, "Pago Seguro", "Múltiples opciones de pago disponibles"),
                createBenefitCard(VaadinIcon.STAR, "Calidad Garantizada", "Los mismos productos premium de siempre")
        );

        whySection.add(whyTitle, benefitsGrid);

        // CTA Section
        VerticalLayout ctaSection = new VerticalLayout();
        ctaSection.addClassNames(
                LumoUtility.Padding.XLARGE,
                LumoUtility.Background.PRIMARY,
                LumoUtility.TextAlignment.CENTER
        );
        ctaSection.setWidthFull();
        ctaSection.setAlignItems(FlexComponent.Alignment.CENTER);

        H2 ctaTitle = new H2("¿Listo para Probar Nuestros Sabores?");
        ctaTitle.addClassNames(
                LumoUtility.FontSize.XXLARGE,
                LumoUtility.FontWeight.BOLD,
                LumoUtility.TextColor.PRIMARY_CONTRAST
        );

        Paragraph ctaSubtitle = new Paragraph("Accede a nuestro menú completo y realiza tu pedido ahora");
        ctaSubtitle.addClassNames(
                LumoUtility.FontSize.LARGE,
                LumoUtility.TextColor.PRIMARY_CONTRAST,
                LumoUtility.Margin.Bottom.LARGE
        );

        Button mainCTAButton = new Button("Ver Menú y Pedir Online", new Icon(VaadinIcon.CART));
        mainCTAButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_LARGE);
        mainCTAButton.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate("foodmenu")));

        ctaSection.add(ctaTitle, ctaSubtitle, mainCTAButton);

        // Footer Section
        VerticalLayout footerSection = new VerticalLayout();
        footerSection.addClassNames(
                LumoUtility.Padding.LARGE,
                LumoUtility.Background.CONTRAST_10,
                LumoUtility.TextAlignment.CENTER
        );
        footerSection.setWidthFull();

        H3 footerTitle = new H3("Información de Contacto");
        Paragraph address = new Paragraph("📍 Calle Antonio López, 1, Cádiz 11001");
        Paragraph phone = new Paragraph("☎️ +34 956 262 255");
        Paragraph email = new Paragraph("✉️ info@casamanteca.es");

        footerSection.add(footerTitle, address, phone, email);

        // Main container
        VerticalLayout landingPage = new VerticalLayout();
        landingPage.addClassNames(LumoUtility.Padding.NONE);
        landingPage.setSpacing(false);
        landingPage.setWidthFull();

        landingPage.add(heroSection, aboutSection, specialtiesSection, whySection, ctaSection, footerSection);

        return landingPage;
    }

    private VerticalLayout createSpecialtyCard(VaadinIcon icon, String title, String description) {
        VerticalLayout card = new VerticalLayout();
        card.addClassNames(
                LumoUtility.Padding.LARGE,
                LumoUtility.Background.BASE,
                LumoUtility.BorderRadius.LARGE,
                LumoUtility.BoxShadow.SMALL
        );
        card.setAlignItems(FlexComponent.Alignment.CENTER);
        card.setSpacing(false);
        card.setMaxWidth("280px");

        Icon specialtyIcon = new Icon(icon);
        specialtyIcon.addClassNames(
                LumoUtility.FontSize.XXXLARGE,
                LumoUtility.TextColor.PRIMARY,
                LumoUtility.Margin.Bottom.MEDIUM
        );

        H3 specialtyTitle = new H3(title);
        specialtyTitle.addClassNames(
                LumoUtility.FontSize.LARGE,
                LumoUtility.FontWeight.BOLD,
                LumoUtility.Margin.Bottom.SMALL,
                LumoUtility.TextAlignment.CENTER
        );

        Div specialtyDescription = new Div();
        specialtyDescription.setText(description);
        specialtyDescription.addClassNames(
                LumoUtility.TextColor.SECONDARY,
                LumoUtility.TextAlignment.CENTER,
                LumoUtility.FontSize.SMALL
        );

        card.add(specialtyIcon, specialtyTitle, specialtyDescription);
        return card;
    }

    private VerticalLayout createBenefitCard(VaadinIcon icon, String title, String description) {
        VerticalLayout card = new VerticalLayout();
        card.addClassNames(
                LumoUtility.Padding.LARGE,
                LumoUtility.Background.BASE,
                LumoUtility.BorderRadius.LARGE,
                LumoUtility.BoxShadow.SMALL
        );
        card.setAlignItems(FlexComponent.Alignment.CENTER);
        card.setSpacing(false);
        card.setMaxWidth("200px");

        Icon benefitIcon = new Icon(icon);
        benefitIcon.addClassNames(
                LumoUtility.FontSize.XXXLARGE,
                LumoUtility.TextColor.SUCCESS,
                LumoUtility.Margin.Bottom.MEDIUM
        );

        H3 benefitTitle = new H3(title);
        benefitTitle.addClassNames(
                LumoUtility.FontSize.MEDIUM,
                LumoUtility.FontWeight.BOLD,
                LumoUtility.Margin.Bottom.SMALL
        );

        Div benefitDescription = new Div();
        benefitDescription.setText(description);
        benefitDescription.addClassNames(
                LumoUtility.TextColor.SECONDARY,
                LumoUtility.TextAlignment.CENTER,
                LumoUtility.FontSize.SMALL
        );

        card.add(benefitIcon, benefitTitle, benefitDescription);
        return card;
    }
}