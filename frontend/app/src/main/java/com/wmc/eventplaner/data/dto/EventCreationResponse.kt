package com.wmc.eventplaner.data.dto

import com.google.gson.annotations.SerializedName

// Or if using Kotlinx Serialization:
// import kotlinx.serialization.SerialName
// import kotlinx.serialization.Serializable

// @Serializable // For Kotlinx Serialization
data class EventCreationResponse(
    @SerializedName("data")
    val eventData: EventDetails? = null, // Renamed from "data" to avoid conflict if used as a generic name

    @SerializedName("responseCode")
    val responseCode: String? = null,

    @SerializedName("responseMessage")
    val responseMessage: String? = null,
)

// @Serializable // For Kotlinx Serialization
data class EventDetails(
    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("title")
    val title: String? = null,

    @SerializedName("description")
    val description: String? = null,

    @SerializedName("startTime")
    val startTime: String? = null, // Consider parsing to Date/LocalDateTime

    @SerializedName("endTime")
    val endTime: String? = null,   // Consider parsing to Date/LocalDateTime

    @SerializedName("location")
    val location: String? = null,

    @SerializedName("imageData")
    val imageData: Any? = null, // Type Any? as it's null, could be String (base64) or specific object if not null

    @SerializedName("participants")
    val participants: List<Any>? = null, // Assuming it's a list of some participant objects or could be empty

    @SerializedName("organizer")
    val organizer: Any? = null, // Assuming it could be an Organizer object or null

    @SerializedName("attendees")
    val attendees: List<Any>? = null, // Assuming it's a list of some attendee objects or could be empty

    @SerializedName("categories")
    val categories: List<Any>? = null, // Assuming it's a list of some category objects or could be empty

    @SerializedName("status")
    val status: String? = null,    // Could be an enum

    @SerializedName("maxAttendees")
    val maxAttendees: Int? = null, // Assuming it's an Int, though JSON shows null

    @SerializedName("version")
    val version: Int? = null,

    @SerializedName("imageUrl")
    val imageUrl: String? = null,  // JSON shows null, but likely a String if present

    @SerializedName("file")
    val file: String? = null,

    @SerializedName("public")
    val isPublic: Boolean? = null, // Renamed from "public" to "isPublic" to follow Kotlin conventions for booleans
)