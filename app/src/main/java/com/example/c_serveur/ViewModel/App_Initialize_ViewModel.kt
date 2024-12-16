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
import com.example.c_serveur.ViewModel.Model.App_Initialize_Model

open class App_Initialize_ViewModel : ViewModel() {

    var _app_Initialize_Model by mutableStateOf(
        App_Initialize_Model()
    )

    val app_Initialize_Model: App_Initialize_Model get() = this._app_Initialize_Model

    // Progress tracking
    var initializationProgress by mutableFloatStateOf(0f)

    var isInitializing by mutableStateOf(false)
    var initializationComplete by mutableStateOf(false)

    init {
        viewModelScope.launch {
            try {
                isInitializing = true
                _app_Initialize_Model.load_Produits_FireBase()
            } finally {
                isInitializing = false
            }
        }
    }
}
