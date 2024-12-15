package com.example.Packages._4.Fragment.ViewModel._1.Main

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.Packages._3.Fragment.ViewModel._2.Init.Main.Components.Initialise_ViewModel
import com.example.Packages._4.Fragment.ViewModel.Model._4_Fragment_Ui_State
import com.example.c_serveur.ViewModel.Model.App_Initialize_Model
import kotlinx.coroutines.launch

class F4_ViewModel internal constructor() : ViewModel() {

    var _uiState by mutableStateOf(
        _4_Fragment_Ui_State(
            initialLastUpdateTime = System.currentTimeMillis().toString(),
        )
    )
    var  _app_Initialize_Model by mutableStateOf(
        App_Initialize_Model(
        )
    )

    val uiState: _4_Fragment_Ui_State get() = this._uiState
    val app_Initialize_Model: App_Initialize_Model get() = this._app_Initialize_Model

    var isInitializing by mutableStateOf(false)
    var initializationProgress by mutableFloatStateOf(0f)
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


