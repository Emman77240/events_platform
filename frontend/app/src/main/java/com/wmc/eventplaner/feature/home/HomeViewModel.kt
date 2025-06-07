package com.wmc.eventplaner.feature.home

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.wmc.eventplaner.common.BaseViewModel
import com.wmc.eventplaner.common.ErrorState
import com.wmc.eventplaner.data.DatastorePreferences
import com.wmc.eventplaner.data.RemoteRepository
import com.wmc.eventplaner.data.dto.CreateEventRequest
import com.wmc.eventplaner.data.dto.Event
import com.wmc.eventplaner.data.dto.EventCreationResponse
import com.wmc.eventplaner.data.dto.EventDetails
import com.wmc.eventplaner.data.dto.EventGetByIdRequest
import com.wmc.eventplaner.data.dto.EventsResponse
import com.wmc.eventplaner.data.dto.GetEventByIdResponse
import com.wmc.eventplaner.data.dto.LoginRequest
import com.wmc.eventplaner.data.dto.LoginResponse
import com.wmc.eventplaner.feature.event.dummyEventDetails
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
open class HomeViewModel @Inject constructor(
    private val repository: RemoteRepository,
    private val datastorePreferences: DatastorePreferences
) : BaseViewModel() {

    // Specific state for login screen
    val allEventState = mutableStateOf<EventsResponse?>(null)
    val adminEventState = mutableStateOf<LoginResponse?>(null)
    val createEventState = mutableStateOf<EventCreationResponse?>(null)
    var deleteEventState = mutableStateOf<EventCreationResponse?>(null)
    val updateEventState = mutableStateOf<EventCreationResponse?>(null)
    val eventDetailState = mutableStateOf<GetEventByIdResponse?>(null)
    fun clearAuthInfo(){
        viewModelScope.launch {
            datastorePreferences.clearByKey("auth")
        }

    }
    fun getAllEvent() {
        executeApiCall(
            apiCall = { repository.getAllEvent() },
            onSuccess = { response ->
                allEventState.value = response
                clearError()
                // Additional success handling if needed
            },
            onError = { errorMessage ->
                // Specific error handling for login
             //   allEventState.value=dummyEventResponse
                errorState.value = ErrorState(
                    message = errorMessage ?: "Login failed",
                    retryAction = { getAllEvent() },
                )
            }
        )
    }

    fun updateEvent(request: CreateEventRequest) {
        executeApiCall(
            apiCall = { repository.updateEvent(request) },
            onSuccess = { response ->
                updateEventState.value = response
                clearError()
                // Additional success handling if needed
            },
            onError = { errorMessage ->
                // Specific error handling for login
                errorState.value = ErrorState(
                    message = errorMessage ?: "something went wrong",
                    retryAction = { updateEvent(request) },
                )
            }
        )
    }

     fun getEventDetail(request: EventGetByIdRequest) {
        executeApiCall(
            apiCall = { repository.getEventById(request) },
            onSuccess = { response ->
                eventDetailState.value = response
                Log.d("TAG", "getEventDetail: $response")
                clearError()
                // Additional success handling if needed
            },
            onError = { errorMessage ->
              //  eventDetailState.value =dummyEventDetails // dummmy to be remove
                // Specific error handling for login
                errorState.value = ErrorState(
                    message = errorMessage ?: "Something went wrong",
                    retryAction = { getEventDetail(request) },
                )
            }
        )
    }

  fun createEvent(request: CreateEventRequest) {
        executeApiCall(
            apiCall = { repository.createEvent(request) },
            onSuccess = { response ->
                createEventState.value = response
                clearError()
                // Additional success handling if needed
            },
            onError = { errorMessage ->
                // Specific error handling for login
                errorState.value = ErrorState(
                    message = errorMessage ?: "Login failed",
                    retryAction = { createEvent(request) },
                )
            }
        )
    }
 fun deleteEvent(request: EventGetByIdRequest) {
        executeApiCall(
            apiCall = { repository.deleteEvent(request) },
            onSuccess = { response ->
                deleteEventState.value = response
                clearError()
                // Additional success handling if needed
            },
            onError = { errorMessage ->
                // Specific error handling for login
                errorState.value = ErrorState(
                    message = errorMessage ?: "Login failed",
                    retryAction = { deleteEvent(request) },
                )
            }
        )
    }

}