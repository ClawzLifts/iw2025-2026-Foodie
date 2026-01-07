package com.foodie.application.security;

import com.foodie.application.security.ui.views.LoginView;
import com.foodie.application.security.ui.views.RegisterView;
import com.foodie.application.ui.views.MainView;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;
import com.vaadin.flow.server.auth.AccessAnnotationChecker;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Listener que maneja las redirecciones cuando un usuario intenta acceder
 * a una vista para la que no tiene permisos.
 */
@Component
public class AccessControlInitListener implements VaadinServiceInitListener {

    private final AccessAnnotationChecker accessChecker = new AccessAnnotationChecker();

    @Override
    public void serviceInit(ServiceInitEvent initEvent) {
        initEvent.getSource().addUIInitListener(uiInitEvent -> {
            uiInitEvent.getUI().addBeforeEnterListener(this::beforeEnter);
        });
    }

    private void beforeEnter(BeforeEnterEvent event) {
        Class<?> navigationTarget = event.getNavigationTarget();
        
        // Permitir acceso a la vista de login, registro y main (landing page) sin autenticación
        if (LoginView.class.equals(navigationTarget)
                || RegisterView.class.equals(navigationTarget)
                || MainView.class.equals(navigationTarget)) {
            return;
        }

        // Verificar si el usuario está autenticado
        if (!isUserLoggedIn()) {
            event.rerouteTo(LoginView.class);
            return;
        }

        // Verificar permisos de acceso basados en anotaciones
        if (!accessChecker.hasAccess(navigationTarget, getCurrentUser(), this::hasRole)) {
            // Si no tiene acceso, redirigir a foodmenu con cambio de URL
            event.forwardTo("foodmenu");
        }
    }

    private boolean isUserLoggedIn() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken);
    }

    private Authentication getCurrentUser() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    private boolean hasRole(String role) {
        Authentication authentication = getCurrentUser();
        if (authentication == null) {
            return false;
        }
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals(role));
    }
}
