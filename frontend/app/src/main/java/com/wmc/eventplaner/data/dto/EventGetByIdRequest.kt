package com.wmc.eventplaner.data.dto

import com.google.gson.annotations.SerializedName

// @Serializable // Add this if using Kotlinx Serialization
data class EventGetByIdRequest(
    @SerializedName("email")
    val email: String? = null,

    @SerializedName("id")
    val id: Int? = null,
)