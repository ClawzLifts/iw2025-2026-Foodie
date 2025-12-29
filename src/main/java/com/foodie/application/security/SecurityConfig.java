package com.foodie.application.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Autowired
    private RoleBasedSuccessHandler roleBasedSuccessHandler;
    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .userDetailsService(userDetailsService)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/login", "/register", "/image/**",
                                "/frontend/**", "/VAADIN/**", "/vaadin/**", "/webjars/**",
                                "/favicon.ico", "/robots.txt", "/manifest.webmanifest", "/sw.js",
                                "/offline.html", "/icons/**", "/images/**", "/styles/**"
                        ).permitAll()
                        .requestMatchers("/dashboard/**").hasRole("MANAGER")
                        .requestMatchers("/carta/**", "/carrito/**", "/pago/**", "/foodmenu").hasRole("USER")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")                // GET login form
                        .loginProcessingUrl("/perform_login") // POST login creds
                        .successHandler(roleBasedSuccessHandler)
                        .failureUrl("/login?error=true")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                )
                .csrf(csrf -> csrf.disable());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
