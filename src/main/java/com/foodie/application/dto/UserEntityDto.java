package com.foodie.application.dto;

import com.foodie.application.domain.User;
import lombok.Value;

import java.io.Serializable;
import java.util.Set;

/**
 * DTO for {@link User}
 */
@Value
public class UserEntityDto implements Serializable {
    Long id;
    String username;
    String password;
    String email;
    String profilePictureUrl;
    Long phoneNumber;
    String address;
    String fullName;
    String role;
    Set<Set<String>> allergies;
}