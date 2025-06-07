package com.wmc.eventplaner.data

import android.util.Log
import com.wmc.eventplaner.common.BaseRepository
import com.wmc.eventplaner.common.Resource
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
import com.wmc.eventplaner.util.toJsonObject
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class RemoteRepository @Inject constructor(
    private val api: ApiService
) : BaseRepository() {

    suspend fun login(request: LoginRequest): Resource<LoginResponse> {
        return safeApiCall { api.login(request) }
    }

    suspend fun register(request: RegistrationRequest): Resource<RegistrationResponse> {
        return safeApiCall {
            api.register(request) }

    }

    suspend fun registerWithSocial(request: RegistrationRequest): Resource<RegistrationResponse> {
        return safeApiCall { api.registerWithSocial(request) }
    }

    suspend fun loginWithSocial(request: SocialLoginRequest): Resource<LoginResponse> {
        return safeApiCall { api.loginWithSocial(request) }
    }

    suspend fun forgotPassword(request: ForgotPasswordRequest): Resource<LoginResponse> {
        return safeApiCall {

            api.forgotPassword(request)
        }
    }
    suspend fun getAllEvent(): Resource<EventsResponse> {
        return safeApiCall { api.getUserEvent() }
    }
    suspend fun getAdminEvent(request: LoginRequest): Resource<LoginResponse> {
        return safeApiCall { api.getAdminEvent(request) }
    }
    suspend fun createEvent(
        request: CreateEventRequest,
    ): Resource<EventCreationResponse> {
        return safeApiCall {
            Log.d("TAG", "create event: ${request.toJsonObject()}")
            api.createEvent(
                title = request.title?.toRequestBody(),
                description = request.description?.toRequestBody(),
                startTime = request.startTime?.toRequestBody(),
                endTime = request.endTime?.toRequestBody(),
                location = request.location?.toRequestBody(),
                email = request.email?.toRequestBody(),
                date = request.date?.toRequestBody(),
                imageFile = prepareImageFile(request.imageFile),
            )
        }
    }
//    fun prepareImageFile(uri: File?): MultipartBody.Part? {
//        val requestFile = uri?.asRequestBody("image/*".toMediaType())
//        return MultipartBody.Part.createFormData("imageFile", uri?.name, requestFile!!)
//    }
fun prepareImageFile(file: File?): MultipartBody.Part? {
    // Return null if file is null, doesn't exist, or is a remote file (http/https)
    if (file == null || !file.exists() || file.toString().startsWith("http", ignoreCase = true)) {
        return null
    }

    val requestFile = file.asRequestBody("image/*".toMediaType())
    return MultipartBody.Part.createFormData("imageFile", file.name, requestFile)
}

    suspend fun deleteEvent(request: EventGetByIdRequest,): Resource<EventCreationResponse> {
        return safeApiCall { api.deleteEvent(id=request) }
    }
    suspend fun updateEvent(request: CreateEventRequest): Resource<EventCreationResponse> {
        return safeApiCall {
            Log.d("TAG", "updateEvent: ${request.toJsonObject()}")
            api.updateEvent( title = request.title?.toRequestBody(),
            description = request.description?.toRequestBody(),
            startTime = request.startTime?.toRequestBody(),
            endTime = request.endTime?.toRequestBody(),
            location = request.location?.toRequestBody(),
            email = request.email?.toRequestBody(),
            date = request.date?.toRequestBody(),
            imageFile =prepareImageFile(request.imageFile),
            id =  request.id?.toString()?.toRequestBody()
            )
        }
    }
    suspend fun getEventById(id: EventGetByIdRequest): Resource<GetEventByIdResponse> {
        return safeApiCall { api.getEvent(id) }
    }




}
