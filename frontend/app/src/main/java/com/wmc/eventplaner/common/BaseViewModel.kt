package com.wmc.eventplaner.common

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

open class BaseViewModel : ViewModel() {
    // Universal loading state
    val isLoading = mutableStateOf(false)

    // Universal error handling
    val errorState = mutableStateOf<ErrorState?>(null)

    // Generic function for API calls
    protected fun <T> executeApiCall(
        apiCall: suspend () -> Resource<T>,
        onSuccess: (T) -> Unit = {},
        onError: (String?) -> Unit = { errorState.value = ErrorState(it) }
    ) {
        viewModelScope.launch {
            isLoading.value = true
            errorState.value = null

            when (val result = apiCall()) {
                is Resource.Success -> {
                    result.data?.let(onSuccess)
                }
                is Resource.Error -> {

                    onError(result.message)
                }

                is Resource.Loading<*> -> {}
            }

            isLoading.value = false
        }
    }
    fun clearError() {
        errorState.value = null
    }
}

data class ErrorState(
    val message: String?,
    val retryAction: (() -> Unit)? = null,
)

