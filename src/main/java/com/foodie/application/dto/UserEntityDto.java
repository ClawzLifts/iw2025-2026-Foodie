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
    Integer id;
    String username;
    String password;
    String email;
    String profilePictureUrl;
    Long phoneNumber;
    String address;
    String fullName;
    String role;
    Set<Set<String>> allergies;

    public UserEntityDto(User user){
        this.id = user.getId();
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.email = user.getEmail();
        this.profilePictureUrl = user.getProfilePictureUrl();
        this.phoneNumber = user.getPhoneNumber();
        this.address = user.getAddress();
        this.fullName = user.getFullName();
        this.role = user.getRole().toString();
        this.allergies = Set.of(user.getAllergies());
    }
}