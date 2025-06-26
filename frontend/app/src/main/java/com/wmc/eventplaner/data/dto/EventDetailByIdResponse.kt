package com.wmc.eventplaner.data.dto

import com.google.gson.annotations.SerializedName

// Or if using Kotlinx Serialization:
// import kotlinx.serialization.SerialName
// import kotlinx.serialization.Serializable

// @Serializable // For Kotlinx Serialization
data class GetEventByIdResponse(
    @SerializedName("data")
    val data: EventDetailsData? = null, // Using a more specific name for the nested data

    @SerializedName("responseCode")
    val responseCode: String? = null,

    @SerializedName("responseMessage")
    val responseMessage: String? = null,
)

// @Serializable // For Kotlinx Serialization
data class EventDetailsData(
    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("title")
    val title: String? = null,

    @SerializedName("description")
    val description: String? = null,

    @SerializedName("startDateTime")
    val startDateTime: String? = null, // Consider parsing to Date/LocalDateTime

    @SerializedName("endDateTime")
    val endDateTime: String? = null,   // Consider parsing to Date/LocalDateTime

    @SerializedName("location")
    val location: String? = null,

    @SerializedName("status")
    val status: String? = null,        // Could be an enum

    @SerializedName("isPublic")
    val isPublic: Boolean? = null,

    @SerializedName("imageUrl")
    val imageUrl: String? = null,

    @SerializedName("eventDate")
    val eventDate: String? = null,

    @SerializedName("maxAttendees")
    val maxAttendees: Int? = null,
    // JSON shows null, String? is appropriate if it can contain a date string
)