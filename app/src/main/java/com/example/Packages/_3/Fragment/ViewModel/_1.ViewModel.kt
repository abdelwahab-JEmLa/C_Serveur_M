package com.example.Packages._3.Fragment.ViewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.Packages._3.Fragment.Models.Ui_Mutable_State
import com.example.Packages._3.Fragment.ViewModel._2.Init.Main.Components._1Initialize
import com.example.Packages._3.Fragment.ViewModel._2.Init.Main.Model._1.MAIN.UiState
import com.google.firebase.Firebase
import com.google.firebase.database.database
import kotlinx.coroutines.launch

internal class P3_ViewModel : ViewModel() {
    private val database = Firebase.database
    val ref_ViewModel_Produit_DataBase =
        database.getReference("_1_Prototype4Dec_3_Host_Package_3_DataBase")

    // UI State management
    var _ui_Mutable_State = Ui_Mutable_State()
    val ui_Mutable_State: Ui_Mutable_State get() = _ui_Mutable_State

    // UiStateSnapshotStateList management with proper initialization
    var _uiState by mutableStateOf(
        UiState(
            initialLastUpdateTime = System.currentTimeMillis().toString(),
            initialReferencesFireBaseGroup = emptyList(),
            initial_Produits_DataBase = emptyList()
        )
    )
    val uiStateSnapshotStateList: UiState get() = _uiState

    // Progress tracking
    var initializationProgress by mutableFloatStateOf(0f)

    var isInitializing by mutableStateOf(false)
        private set

    init {
        viewModelScope.launch {
            try {
                isInitializing = true
                _1Initialize()
            } finally {
                isInitializing = false
            }
        }
    }


}
