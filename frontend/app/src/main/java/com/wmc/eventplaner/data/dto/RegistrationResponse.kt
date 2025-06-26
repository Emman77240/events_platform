package com.wmc.eventplaner.data.dto

import com.google.gson.annotations.SerializedName

// Or if using Kotlinx Serialization:
// import kotlinx.serialization.SerialName
// import kotlinx.serialization.Serializable

// @Serializable // Add this if using Kotlinx Serialization
data class RegistrationResponse(
    @SerializedName("success")
    val success: Boolean? = null,

    @SerializedName("message")
    val message: String? = null,

    @SerializedName("token")
    val token: String? = null,

    @SerializedName("user")
    val user: RegisteredUser? = null, // Reusing the User data class
)

// You would reuse the same User data class as defined before:
// @Serializable // Add this if using Kotlinx Serialization
data class RegisteredUser(
    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("email")
    val email: String? = null,

    @SerializedName("fullName")
    val fullName: String? = null,

    @SerializedName("role")
    val role: String? = null,

    )