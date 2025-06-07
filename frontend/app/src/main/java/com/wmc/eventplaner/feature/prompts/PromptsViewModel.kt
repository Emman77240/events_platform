package com.wmc.eventplaner.feature.prompts

import androidx.lifecycle.ViewModel
import com.wmc.eventplaner.common.PromptTypeShow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject


@HiltViewModel
class PromptsViewModel @Inject constructor() : ViewModel() {
    private val _currentPrompt = MutableStateFlow<PromptTypeShow?>(null)
    val currentPrompt: StateFlow<PromptTypeShow?> = _currentPrompt

    fun updatePrompt(promptTypeShow:PromptTypeShow?) {
        promptTypeShow?.let {
            if (currentPrompt.value==null){
                _currentPrompt.value = promptTypeShow
            }
        }?:run {
            _currentPrompt.value = null
        }
    }

   fun comingSoon(message:String="Coming Soon"){
        updatePrompt(
            if (message.isNotBlank()){
                PromptTypeShow.ComingSoon(message=message){
                    updatePrompt(null)
                }
            }else{
                PromptTypeShow.ComingSoon{
                    updatePrompt(null)
                }
            }

        )
    }

    fun dismissPrompt() {
        _currentPrompt.value = null
    }
    fun clearPrompt() {
        _currentPrompt.value = null
    }
}