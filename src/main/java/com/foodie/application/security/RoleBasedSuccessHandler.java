package com.foodie.application.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
public class RoleBasedSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        var roles = authentication.getAuthorities();
        if (roles.stream().anyMatch(r -> r.getAuthority().equals("ROLE_MANAGER"))) {
            response.sendRedirect("/dashboard");
        } else if (roles.stream().anyMatch(r -> r.getAuthority().equals("ROLE_USER"))) {
            response.sendRedirect("/foodmenu");
        } else {
            response.sendRedirect("/login?error=role");
        }
    }
}

