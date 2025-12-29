package com.foodie.application.service;

import com.foodie.application.domain.Role;
import com.foodie.application.domain.User;
import com.foodie.application.repository.RoleRepository;
import com.foodie.application.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
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
                .role(role != null ? roleRepository.findByName(role).orElseThrow(() -> new RuntimeException("Role not found"))
                                   : roleRepository.findByName("USER").orElseThrow(() -> new RuntimeException("Role USER not found")))
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
                .role(roleRepository.findByName(role).orElseThrow(() -> new RuntimeException("Role not found")))
                .email(email)
                .build();
        return userRepository.save(user);
    }

    public java.util.List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public java.util.List<String> getAllRoles() {
        return roleRepository.findAll().stream().map(Role::getName).toList();
    }

    public void deleteUser(Integer userId) {
        userRepository.deleteById(userId);
    }

    public User updateUser(Integer userId, String username, String email) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            throw new RuntimeException("Usuario no encontrado");
        }
        User user = optionalUser.get();
        user.setUsername(username);
        user.setEmail(email);
        return userRepository.save(user);
    }

    public long countUsers() {
        return userRepository.count();
    }

}