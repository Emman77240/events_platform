package com.wmc.eventplaner.data.dto

import com.google.gson.annotations.SerializedName

// @Serializable // Add this if using Kotlinx Serialization
data class LoginRequest(
    @SerializedName("email")
    val email: String? = null,

    @SerializedName("password")
    val password: String? = null,
)