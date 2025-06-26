package com.wmc.eventplaner.data.dto

import com.google.gson.annotations.SerializedName

// Or if using Kotlinx Serialization:
// import kotlinx.serialization.SerialName
// import kotlinx.serialization.Serializable

// @Serializable // For Kotlinx Serialization
data class EventsResponse(
    @SerializedName("data")
    var data: List<Event>? = null,
)

// @Serializable // For Kotlinx Serialization
data class Event(
    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("title")
    val title: String? = null,

    @SerializedName("description")
    val description: String? = null,

    @SerializedName("startDateTime")
    val startDateTime: String? = null, // You might want to parse this into a Date/LocalDateTime object later

    @SerializedName("endDateTime")
    val endDateTime: String? = null,   // Same as above
    @SerializedName("eventDate")
    val date: String? = null,   // Same as above

    @SerializedName("location")
    val location: String? = null,

    @SerializedName("status")
    val status: String? = null,        // Could be an enum if you have predefined statuses

    @SerializedName("isPublic")
    val isPublic: Boolean? = null,

    @SerializedName("imageUrl")
    val imageUrl: String? = null,
    @SerializedName("imageData")
    val image: String? = null,

)