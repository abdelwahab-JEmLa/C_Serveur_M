package com.example.c_serveur.ViewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.Packages._3.Fragment.ViewModel._2.Init.Main.Components.Initialise_ViewModel_Main
import com.example.c_serveur.ViewModel.Model.App_Initialize_Model
import kotlinx.coroutines.launch

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
                Initialise_ViewModel_Main()
            } finally {
                isInitializing = false
            }
        }
    }
}
