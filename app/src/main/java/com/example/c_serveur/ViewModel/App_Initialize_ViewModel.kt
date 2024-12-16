package com.example.c_serveur.ViewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.example.Packages._3.Fragment.Models.UiState
import com.example.Packages._3.Fragment.ViewModel._2.Init.Main.Components.Initialise_ViewModel
import com.google.firebase.Firebase
import com.google.firebase.database.database
import kotlinx.coroutines.launch
import androidx.lifecycle.ViewModel

open class App_Initialize_ViewModel : ViewModel() {

    // UiStateSnapshotStateList management with proper initialization
    var _uiState by mutableStateOf(
        UiState(
            initialLastUpdateTime = System.currentTimeMillis().toString(),
            initialReferencesFireBaseGroup = emptyList(),
            initial_Produits_DataBase = emptyList()
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
                Initialise_ViewModel()
            } finally {
                isInitializing = false
            }
        }
    }
}
