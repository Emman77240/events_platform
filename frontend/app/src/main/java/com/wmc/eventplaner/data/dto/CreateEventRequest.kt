package com.wmc.eventplaner.data.dto

import com.google.gson.annotations.SerializedName
import java.io.File

// Or if using Kotlinx Serialization:
// import kotlinx.serialization.SerialName
// import kotlinx.serialization.Serializable

// @Serializable // Add this if using Kotlinx Serialization
data class CreateEventRequest(
    @SerializedName("title")
    val title: String? = null,

    @SerializedName("description")
    val description: String? = null,

    @SerializedName("id")
    var id: Int? = null,


    @SerializedName("email")
    var email: String?= null,
    @SerializedName("startTime")
    val startTime: String? = null, // Consider if this should be a specific date/time format or Long (timestamp)

    @SerializedName("endTime")
    val endTime: String? = null,   // Consider if this should be a specific date/time format or Long (timestamp)
    @SerializedName("eventDate")
    val date: String? = null,   // Consider if this should be a specific date/time format or Long (timestamp)

    @SerializedName("location")
    val location: String? = null,

    @SerializedName("imageFile")
    val imageFile: File? = null, // This might represent a file path, a base64 encoded string, or a URL.

    // The actual handling will depend on how your API expects the image.
)