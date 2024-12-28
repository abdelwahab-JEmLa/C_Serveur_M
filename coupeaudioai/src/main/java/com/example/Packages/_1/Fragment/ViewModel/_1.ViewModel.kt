package com.example.Packages._1.Fragment.ViewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.Packages._1.Fragment.Models.UiState
import kotlinx.coroutines.launch

class ViewModel_Fragment : ViewModel() {

    private var _uiState by mutableStateOf(
        UiState(
            initialLastUpdateTime = System.currentTimeMillis().toString(),
        )
    )
    val uiState: UiState get() = this._uiState

    // Progress tracking
    var initializationProgress by mutableFloatStateOf(0f)

    var isInitializing by mutableStateOf(false)
    var initializationComplete by mutableStateOf(false)

    init {
        viewModelScope.launch {
                try {
                    isInitializing = true
                } finally {
                    isInitializing = false
                }
        }
    }
}


