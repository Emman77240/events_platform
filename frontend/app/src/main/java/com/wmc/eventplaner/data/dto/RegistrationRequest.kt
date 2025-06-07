package com.wmc.eventplaner.data.dto

import com.google.gson.annotations.SerializedName

// @Serializable // Add this if using Kotlinx Serialization
data class RegistrationRequest(
    @SerializedName("email")
    val email: String? = null,

    @SerializedName("fullName")
    val fullName: String? = null,

    @SerializedName("token")
    val token: String? = null,

    @SerializedName("password")
    val password: String? = null,
    val confirmPassword: String? = null,
    val image: String? = null,

    )