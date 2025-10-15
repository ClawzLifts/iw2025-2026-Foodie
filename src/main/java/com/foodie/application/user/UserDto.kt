package com.foodie.application.user

import jakarta.validation.constraints.NotNull
import java.io.Serializable

/**
 * DTO for {@link com.foodie.application.User.User}
 */
data class UserDto(
    @field:NotNull val id: Long? = null,
    val username: String? = null,
    val password: String? = null,
    val email: String? = null,
    val profilePictureUrl: String? = null,
    val role: String? = null,
    val phoneNumber: Long? = null,
    val address: String? = null,
    val fullName: String? = null
) : Serializable