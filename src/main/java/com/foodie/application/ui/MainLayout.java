package com.foodie.application.ui;

import com.vaadin.flow.component.avatar.Avatar;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

import com.vaadin.flow.theme.lumo.LumoUtility;

public class MainLayout extends AppLayout {

    public MainLayout() {
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
        header.addToEnd(avatar);

        addToNavbar(header);
    }
}
