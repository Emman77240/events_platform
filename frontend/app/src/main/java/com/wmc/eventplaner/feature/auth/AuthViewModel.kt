package com.wmc.eventplaner.feature.auth

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.wmc.eventplaner.common.BaseViewModel
import com.wmc.eventplaner.common.ErrorState
import com.wmc.eventplaner.data.DatastorePreferences
import com.wmc.eventplaner.data.dto.LoginResponse
import com.wmc.eventplaner.data.RemoteRepository
import com.wmc.eventplaner.data.dto.ForgotPasswordRequest
import com.wmc.eventplaner.data.dto.LoginRequest
import com.wmc.eventplaner.data.dto.RegistrationRequest
import com.wmc.eventplaner.data.dto.RegistrationResponse
import com.wmc.eventplaner.data.dto.SocialLoginRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: RemoteRepository,
    private val datastorePreferences: DatastorePreferences
) : BaseViewModel() {

    // Specific state for login screen
    val loginState = mutableStateOf<LoginResponse?>(null)
    val loginSocialState = mutableStateOf<LoginResponse?>(null)
    val registerState = mutableStateOf<RegistrationResponse?>(null)
  //  val registerSocialState = mutableStateOf<RegistrationResponse?>(null)
    val forgotPasswordState = mutableStateOf<LoginResponse?>(null)
    private val _authRequestCache = MutableStateFlow<LoginRequest?>(null)
    val authRequestCache: StateFlow<LoginRequest?> = _authRequestCache.asStateFlow()
    var isSocialLogin = false
    init {
        viewModelScope.launch {
            _authRequestCache.value = getAuthInfo()
        }
    }

    fun  saveAuthInfo(auth : LoginRequest){
        datastorePreferences.putObject("auth",auth)
    }
    fun getAuthInfo(): LoginRequest? {
        return datastorePreferences.getObject("auth",LoginRequest::class.java)
    }
    fun clearAuthInfo(){
        viewModelScope.launch {
            datastorePreferences.clearByKey("auth")
        }

    }

    fun login(request: LoginRequest) {
        executeApiCall(
            apiCall = { repository.login(request) },
            onSuccess = { response ->
                if (isSocialLogin==false){
                    saveAuthInfo(request)
                }
                loginState.value = response
                clearError()

            },
            onError = { errorMessage ->
                // dummy to be remove
//                if (isSocialLogin==false){
//                    saveAuthInfo(request)
//                }
              //  loginState.value = generateDummyLoginResponse()


                errorState.value = ErrorState(
                    message = errorMessage ?: "Login failed",
                    retryAction = { login(request) },
                )
            }
        )
    }

    fun loginWithSocial(request: SocialLoginRequest) {
        executeApiCall(
            apiCall = { repository.loginWithSocial(request) },
            onSuccess = { response ->
                loginState.value = response
                clearError()
                // Additional success handling if needed
            },
            onError = { errorMessage ->
                // Specific error handling for login
                errorState.value = ErrorState(
                    message = errorMessage ?: "Login failed",
                    retryAction = { loginWithSocial(request) },
                )
            }
        )
    }


    fun registerUserWithLocal(request: RegistrationRequest) {
        executeApiCall(
            apiCall = { repository.register(request) },
            onSuccess = { response ->
                registerState.value = response
                clearError()
                // Additional success handling if needed
            },
            onError = { errorMessage ->
                // Specific error handling for login
                errorState.value = ErrorState(
                    message = errorMessage ?: "Login failed",
                    retryAction = { registerUserWithLocal(request) },
                )
            }
        )
    }

    fun registerUserWithSocial(request: RegistrationRequest) {
        executeApiCall(
            apiCall = { repository.registerWithSocial(request) },
            onSuccess = { response ->
                registerState.value = response
                clearError()
                // Additional success handling if needed
            },
            onError = { errorMessage ->
                // Specific error handling for login
                errorState.value = ErrorState(
                    message = errorMessage ?: "Login failed",
                    retryAction = { registerUserWithSocial(request) },
                )
            }
        )
    }


    fun forgotPassword(request: ForgotPasswordRequest) {
        executeApiCall(
            apiCall = { repository.forgotPassword(request) },
            onSuccess = { response ->
                forgotPasswordState.value = response
                clearError()
                // Additional success handling if needed
            },
            onError = { errorMessage ->
                // Specific error handling for login
                errorState.value = ErrorState(
                    message = errorMessage ?: "Login failed",
                    retryAction = { forgotPassword(request) },
                )
            }
        )
    }

}


