package com.wmc.eventplaner.data.dto

import com.google.gson.annotations.SerializedName

// @Serializable // Add this if using Kotlinx Serialization
data class SocialLoginRequest(
    @SerializedName("email")
    val email: String? = null,

    @SerializedName("token")
    val token: String? = null,
    @SerializedName("fullName")
    val fullName: String? = null,

    @SerializedName("image")
    val image: String? = null,

)
