package com.foodie.application.service;

import com.foodie.application.domain.User;
import com.foodie.application.domain.Role;
import com.foodie.application.repository.UserRepository;
import com.foodie.application.repository.RoleRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Service class for managing user operations in the Foodie application.
 * <p>
 * This service provides business logic for user management including:
 * creating, retrieving, updating, and deleting users, as well as
 * managing user roles, allergies, and other user-related operations.
 * </p>
 *
 * @author Foodie Team
 */
@Slf4j
@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    /**
     * Constructs a UserService with required repositories.
     *
     * @param userRepository the user repository for database access
     * @param roleRepository the role repository for database access
     */
    public UserService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    /**
     * Creates a new user with the specified details.
     *
     * @param username the unique username
     * @param password the user's password (should be hashed before calling this method)
     * @param email the unique email address
     * @param fullName the user's full name
     * @param roleId the ID of the role to assign
     * @return the ID of the newly created user
     * @throws EntityNotFoundException if the role is not found
     * @throws IllegalArgumentException if username or email already exists
     */
    @Transactional
    public Integer createUser(String username, String password, String email, String fullName, Integer roleId) {
        // Check if username already exists
        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Username already exists: " + username);
        }

        // Check if email already exists
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email already exists: " + email);
        }

        // Get role
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new EntityNotFoundException("Role not found with id: " + roleId));

        // Create new user
        User user = User.builder()
                .username(username)
                .password(password)
                .email(email)
                .fullName(fullName)
                .role(role)
                .build();

        User savedUser = userRepository.save(user);
        log.info("User created successfully with id: {}", savedUser.getId());
        return savedUser.getId();
    }

    /**
     * Retrieves a user by their ID.
     *
     * @param userId the ID of the user
     * @return the User object
     * @throws EntityNotFoundException if the user is not found
     */
    public User getUserById(Integer userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
    }

    /**
     * Retrieves a user by their username.
     *
     * @param username the username
     * @return the User object wrapped in Optional
     */
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * Retrieves a user by their email.
     *
     * @param email the email address
     * @return the User object wrapped in Optional
     */
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Retrieves all users from the database.
     *
     * @return a list of all users
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Updates a user's personal information.
     *
     * @param userId the ID of the user to update
     * @param fullName the new full name (optional, null to skip)
     * @param email the new email (optional, null to skip)
     * @param phoneNumber the new phone number (optional, null to skip)
     * @param address the new address (optional, null to skip)
     * @throws EntityNotFoundException if the user is not found
     * @throws IllegalArgumentException if the new email already exists for another user
     */
    @Transactional
    public void updateUserProfile(Integer userId, String fullName, String email, Long phoneNumber, String address) {
        User user = getUserById(userId);

        // Check if email is being changed and already exists
        if (email != null && !email.equals(user.getEmail())) {
            if (userRepository.findByEmail(email).isPresent()) {
                throw new IllegalArgumentException("Email already exists: " + email);
            }
            user.setEmail(email);
        }

        if (fullName != null) {
            user.setFullName(fullName);
        }
        if (phoneNumber != null) {
            user.setPhoneNumber(phoneNumber);
        }
        if (address != null) {
            user.setAddress(address);
        }

        userRepository.save(user);
        log.info("User {} profile updated successfully", userId);
    }

    /**
     * Updates a user's password.
     *
     * @param userId the ID of the user
     * @param newPassword the new password (should be hashed before calling this method)
     * @throws EntityNotFoundException if the user is not found
     */
    @Transactional
    public void updateUserPassword(Integer userId, String newPassword) {
        User user = getUserById(userId);
        user.setPassword(newPassword);
        userRepository.save(user);
        log.info("Password updated for user {}", userId);
    }

    /**
     * Updates a user's profile picture URL.
     *
     * @param userId the ID of the user
     * @param profilePictureUrl the new profile picture URL
     * @throws EntityNotFoundException if the user is not found
     */
    @Transactional
    public void updateProfilePicture(Integer userId, String profilePictureUrl) {
        User user = getUserById(userId);
        user.setProfilePictureUrl(profilePictureUrl);
        userRepository.save(user);
        log.info("Profile picture updated for user {}", userId);
    }

    /**
     * Assigns a role to a user.
     *
     * @param userId the ID of the user
     * @param roleId the ID of the role to assign
     * @throws EntityNotFoundException if the user or role is not found
     */
    @Transactional
    public void assignRole(Integer userId, Integer roleId) {
        User user = getUserById(userId);
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new EntityNotFoundException("Role not found with id: " + roleId));

        user.setRole(role);
        userRepository.save(user);
        log.info("Role {} assigned to user {}", roleId, userId);
    }

    /**
     * Adds an allergy to a user's allergy set.
     *
     * @param userId the ID of the user
     * @param allergy the allergy to add
     * @throws EntityNotFoundException if the user is not found
     */
    @Transactional
    public void addAllergy(Integer userId, String allergy) {
        User user = getUserById(userId);
        if (user.getAllergies() == null) {
            java.util.Set<String> allergies = new java.util.HashSet<>();
            allergies.add(allergy);
            user.setAllergies(allergies);
        } else {
            user.getAllergies().add(allergy);
        }
        userRepository.save(user);
        log.info("Allergy '{}' added to user {}", allergy, userId);
    }

    /**
     * Removes an allergy from a user's allergy set.
     *
     * @param userId the ID of the user
     * @param allergy the allergy to remove
     * @return true if the allergy was found and removed, false otherwise
     * @throws EntityNotFoundException if the user is not found
     */
    @Transactional
    public boolean removeAllergy(Integer userId, String allergy) {
        User user = getUserById(userId);
        if (user.getAllergies() != null) {
            boolean removed = user.getAllergies().remove(allergy);
            if (removed) {
                userRepository.save(user);
                log.info("Allergy '{}' removed from user {}", allergy, userId);
            }
            return removed;
        }
        return false;
    }

    /**
     * Updates a user's allergies (replaces the entire set).
     *
     * @param userId the ID of the user
     * @param allergies the new set of allergies
     * @throws EntityNotFoundException if the user is not found
     */
    @Transactional
    public void updateAllergies(Integer userId, Set<String> allergies) {
        User user = getUserById(userId);
        user.setAllergies(allergies);
        userRepository.save(user);
        log.info("Allergies updated for user {}", userId);
    }

    /**
     * Checks if a user has a specific allergy.
     *
     * @param userId the ID of the user
     * @param allergy the allergy to check
     * @return true if the user has the allergy, false otherwise
     * @throws EntityNotFoundException if the user is not found
     */
    public boolean hasAllergy(Integer userId, String allergy) {
        User user = getUserById(userId);
        return user.getAllergies() != null && user.getAllergies().contains(allergy);
    }

    /**
     * Deletes a user from the database.
     *
     * @param userId the ID of the user to delete
     * @throws EntityNotFoundException if the user is not found
     */
    @Transactional
    public void deleteUser(Integer userId) {
        User user = getUserById(userId);
        userRepository.delete(user);
        log.info("User {} deleted successfully", userId);
    }

    /**
     * Retrieves all users with a specific role.
     *
     * @param roleId the ID of the role
     * @return a list of users with the specified role
     */
    public List<User> getUsersByRole(Integer roleId) {
        return userRepository.findAll().stream()
                .filter(user -> user.getRole().getId().equals(roleId))
                .toList();
    }

    /**
     * Checks if a username exists in the database.
     *
     * @param username the username to check
     * @return true if the username exists, false otherwise
     */
    public boolean usernameExists(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    /**
     * Checks if an email exists in the database.
     *
     * @param email the email to check
     * @return true if the email exists, false otherwise
     */
    public boolean emailExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    /**
     * Gets the total number of users in the database.
     *
     * @return the total count of users
     */
    public long getUserCount() {
        return userRepository.count();
    }

    /**
     * Retrieves the currently authenticated user.
     *
     * @return the User object of the currently authenticated user, or null if not authenticated
     */
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        String username = authentication.getName();
        return userRepository.findByUsername(username).orElse(null);
    }
}

