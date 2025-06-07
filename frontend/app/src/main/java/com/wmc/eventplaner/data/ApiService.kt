package com.wmc.eventplaner.data

import com.wmc.eventplaner.data.dto.CreateEventRequest
import com.wmc.eventplaner.data.dto.EventCreationResponse
import com.wmc.eventplaner.data.dto.EventDetails
import com.wmc.eventplaner.data.dto.EventGetByIdRequest
import com.wmc.eventplaner.data.dto.EventsResponse
import com.wmc.eventplaner.data.dto.ForgotPasswordRequest
import com.wmc.eventplaner.data.dto.GetEventByIdResponse
import com.wmc.eventplaner.data.dto.LoginResponse
import com.wmc.eventplaner.data.dto.LoginRequest
import com.wmc.eventplaner.data.dto.RegistrationRequest
import com.wmc.eventplaner.data.dto.RegistrationResponse
import com.wmc.eventplaner.data.dto.SocialLoginRequest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

interface ApiService {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @POST("auth/register")
    suspend fun register(@Body request: RegistrationRequest): RegistrationResponse

    @POST("auth/social-login")
    suspend fun registerWithSocial(@Body request: RegistrationRequest): RegistrationResponse

    @POST("auth/social-login")
    suspend fun loginWithSocial(@Body request: SocialLoginRequest): LoginResponse

    @POST("forgot-password/recover-forgotten-password")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequest): LoginResponse

    @GET("events/get-All")
    suspend fun getUserEvent(): EventsResponse

    @POST("event/getAdminEvent")
    suspend fun getAdminEvent(@Body request: LoginRequest): LoginResponse

//    @POST("event/createEvent")
//    suspend fun createEvent(@Body request: CreateEventRequest): EventCreationResponse
    @Multipart
    @POST("events/create")
    suspend fun createEvent(
    @Part("title") title: RequestBody?,
    @Part("description") description: RequestBody?,
    @Part("startTime") startTime: RequestBody?,
    @Part("endTime") endTime: RequestBody?,
    @Part("location") location: RequestBody?,
    @Part("email") email: RequestBody?,
    @Part imageFile: MultipartBody.Part?,
    @Part("eventDate") date: RequestBody?,
): EventCreationResponse

    @POST("events/delete")
    suspend fun deleteEvent(
        @Body id: EventGetByIdRequest,
    ): EventCreationResponse

    @Multipart
    @POST("events/update")
    suspend fun updateEvent(
        @Part("title") title: RequestBody?,
        @Part("description") description: RequestBody?,
        @Part("startTime") startTime: RequestBody?,
        @Part("endTime") endTime: RequestBody?,
        @Part("location") location: RequestBody?,
        @Part("email") email: RequestBody?,
        @Part imageFile: MultipartBody.Part?,
        @Part("eventDate") date: RequestBody?,
        @Part("id") id: RequestBody?,
    ): EventCreationResponse

    @POST("events/get-by-id")
    suspend fun getEvent(
        @Body id: EventGetByIdRequest,
    ): GetEventByIdResponse

}
