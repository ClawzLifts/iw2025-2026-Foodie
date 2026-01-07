package com.foodie.application.config;

import com.foodie.application.domain.Establishment;
import com.foodie.application.domain.Role;
import com.foodie.application.domain.User;
import com.foodie.application.repository.EstablishmentRepository;
import com.foodie.application.repository.RoleRepository;
import com.foodie.application.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalTime;

/**
 * Configuration class for initializing default data on application startup.
 * This ensures that the establishment, roles, and admin user exist even if data.sql fails to load.
 */
@Slf4j
@Configuration
public class DataInitializationConfig {

    @Bean
    public ApplicationRunner initializeData(
            EstablishmentRepository establishmentRepository,
            RoleRepository roleRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        return args -> {
            try {
                // Quick check: if both roles and establishment exist, skip initialization
                boolean adminRoleExists = roleRepository.findAll().stream()
                        .anyMatch(r -> r.getName().equals("ADMIN"));
                boolean establishmentExists = establishmentRepository.findById(1).isPresent();

                if (adminRoleExists && establishmentExists) {
                    log.info("Default data already exists, skipping initialization");
                    return;
                }

                log.info("Initializing default data...");

                // Initialize ADMIN role
                if (!adminRoleExists) {
                    Role adminRole = Role.builder()
                            .name("ADMIN")
                            .build();
                    roleRepository.save(adminRole);
                    log.info("ADMIN role created successfully");
                }

                // Initialize USER role
                if (roleRepository.findAll().stream().noneMatch(r -> r.getName().equals("USER"))) {
                    Role userRole = Role.builder()
                            .name("USER")
                            .build();
                    roleRepository.save(userRole);
                    log.info("USER role created successfully");
                }

                // Initialize default establishment
                if (!establishmentExists) {
                    Establishment establishment = Establishment.builder()
                            .id(1)
                            .name("Casa Manteca")
                            .description("Un icono culinario de Cádiz con más de 70 años de historia especializado en montaditos de atún, jamón ibérico y conservas gourmet.")
                            .address("Cádiz, Andalucía, España")
                            .phone("+34 956 280 513")
                            .openingTime(LocalTime.of(10, 0))
                            .closingTime(LocalTime.of(23, 0))
                            .build();
                    establishmentRepository.save(establishment);
                    log.info("Default establishment created successfully");
                }

                // Initialize admin user
                Role adminRole = roleRepository.findAll().stream()
                        .filter(r -> r.getName().equals("ADMIN"))
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("ADMIN role not found"));

                if (userRepository.findByUsername("admin").isEmpty()) {
                    User adminUser = User.builder()
                            .username("admin")
                            .password("$2a$10$WW.kQvMRvmjp8ECTZ8iZCeIqJW0LQOT3bQp.WDXXFqKc4OxmEbvQa") // admin123
                            .email("admin@casamanteca.com")
                            .role(adminRole)
                            .build();
                    userRepository.save(adminUser);
                    log.info("Admin user created successfully");
                } else {
                    // Update existing admin user to have ADMIN role if needed
                    User existingAdmin = userRepository.findByUsername("admin").get();
                    if (!existingAdmin.getRole().getName().equals("ADMIN")) {
                        existingAdmin.setRole(adminRole);
                        userRepository.save(existingAdmin);
                        log.info("Admin user role updated to ADMIN");
                    }
                }

                log.info("Default data initialization completed");

            } catch (Exception e) {
                log.error("Error initializing default data", e);
            }
        };
    }
}

