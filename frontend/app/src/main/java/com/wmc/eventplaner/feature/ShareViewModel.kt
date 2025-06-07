package com.wmc.eventplaner.feature

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wmc.eventplaner.data.RemoteRepository
import com.wmc.eventplaner.data.dto.Event
import com.wmc.eventplaner.data.dto.LoginResponse
import com.wmc.eventplaner.feature.home.HomeViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShareViewModel @Inject constructor(): ViewModel() {
    private val _authData: MutableStateFlow<LoginResponse?> =
        MutableStateFlow(null)
    val authData: StateFlow<LoginResponse?> = _authData.asStateFlow()

    private val _eventEdit = MutableStateFlow<Event?>(null)
    val eventEdit: StateFlow<Event?> = _eventEdit.asStateFlow()
    var isAllEventCall = true
    fun updateAuthData(data: LoginResponse) {
        viewModelScope.launch {
            _authData.value = data
        }

    }
    fun setEditEvent(event: Event) {
        viewModelScope.launch {
            _eventEdit.value = event
        }
    }

}