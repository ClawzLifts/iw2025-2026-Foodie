package com.foodie.application.security;

import com.vaadin.flow.server.VaadinServletRequest;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Custom access denied handler that redirects unauthorized users to /foodmenu
 * instead of showing a generic error page.
 */
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, 
                      HttpServletResponse response,
                      AccessDeniedException accessDeniedException) throws IOException, ServletException {
        
        // Solo redirigir si intenta acceder a /admin sin permisos
        if (request.getRequestURI().contains("/admin")) {
            response.sendRedirect("/foodmenu");
        } else {
            // Para otras peticiones, devolver 403
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Acceso denegado");
        }
    }
}
