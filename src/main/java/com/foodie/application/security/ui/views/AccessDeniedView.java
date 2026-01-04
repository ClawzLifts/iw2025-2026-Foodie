package com.foodie.application.security.ui.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.ErrorParameter;
import com.vaadin.flow.router.HasErrorParameter;
import com.vaadin.flow.router.NotFoundException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;

/**
 * Custom error view that handles access denied exceptions.
 * Redirects users to /foodmenu when they try to access restricted areas.
 */
public class AccessDeniedView extends VerticalLayout implements HasErrorParameter<AccessDeniedException> {

    @Override
    public int setErrorParameter(BeforeEnterEvent event, ErrorParameter<AccessDeniedException> parameter) {
        // Redirigir a /foodmenu en lugar de mostrar un error
        UI.getCurrent().navigate("foodmenu");
        return HttpServletResponse.SC_FORBIDDEN;
    }
}
