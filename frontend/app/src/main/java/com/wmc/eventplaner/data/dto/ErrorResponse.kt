package com.wmc.eventplaner.data.dto

import com.google.gson.annotations.SerializedName

// @Serializable // For Kotlinx Serialization
data class ErrorResponse(
    @SerializedName("data")
    val data: Any? = null, // Remains null in this case

    @SerializedName("responseCode")
    val responseCode: String? = null, // This will be "400"

    @SerializedName("responseMessage")
    val responseMessage: String? = null, // This will be "Invalid credentials"

    @SerializedName("token")
    val token: String? = null, // Remains null

    @SerializedName("user")
    val user: User? = null, // Assuming UserData or similar, remains null
)