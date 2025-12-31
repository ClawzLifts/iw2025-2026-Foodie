package com.foodie.application.dto;

import com.foodie.application.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * DTO for displaying user information in the UI.
 * Transfers user data from service to presentation layer without exposing domain model.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto implements Serializable {
    private Integer id;
    private String username;
    private String email;
    private String fullName;
    private String roleName;
    private Long phoneNumber;
    private String address;
    private String profilePictureUrl;

    /**
     * Converts a User entity to UserDisplayDto
     */
    public static UserDto fromUser(User user) {
        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .roleName(user.getRole() != null ? user.getRole().getName() : null)
                .phoneNumber(user.getPhoneNumber())
                .address(user.getAddress())
                .profilePictureUrl(user.getProfilePictureUrl())
                .build();
    }
}

