package com.foodie.application.service;

import com.foodie.application.domain.User;
import com.foodie.application.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User registerUser(String username, String rawPassword, String email, String role) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("Usuario ya existe");
        }
        User user = User.builder()
                .username(username)
                .password(passwordEncoder.encode(rawPassword))
                .email(email)
                .role(role != null ? role : "USER")
                .build();
        return userRepository.save(user);
    }


    public User registerUserByManager(String username, String rawPassword, String role, String email) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("Usuario ya existe");
        }
        User user = User.builder()
                .username(username)
                .password(passwordEncoder.encode(rawPassword))
                .role(role)
                .email(email)
                .build();
        return userRepository.save(user);
    }

    public java.util.List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public java.util.List<String> getAllRoles() {
        return List.of("USER", "MANAGER");
    }
}