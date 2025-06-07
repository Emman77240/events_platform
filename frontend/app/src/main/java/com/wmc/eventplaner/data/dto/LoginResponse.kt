package com.wmc.eventplaner.data.dto

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("success")
    val success: Boolean? = null,

    @SerializedName("message")
    val message: String? = null,

    @SerializedName("token")
    val token: String? = null,

    @SerializedName("user")
    val user: User? = null,
)

data class User(
    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("email")
    val email: String? = null,

    @SerializedName("fullName")
    val fullName: String? = null,
     @SerializedName("role")
    val role: String? = null,

)