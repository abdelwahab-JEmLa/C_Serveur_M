package com.example.Packages._3.Fragment.ViewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.Packages._3.Fragment.Models.Test.UiStateSnapshotStateList
import com.example.Packages._3.Fragment.Models.Ui_Mutable_State
import com.example.Packages._3.Fragment.ViewModel.init._1.Aliment_From_Authers_Refs.initial._1Initialize
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
    var _uiStateSnapshotStateList by mutableStateOf(
        UiStateSnapshotStateList(
            initialLastUpdateTime = System.currentTimeMillis().toString(),
            initialGroupFireBaseReference = emptyList()
        )
    )
    val uiStateSnapshotStateList: UiStateSnapshotStateList get() = _uiStateSnapshotStateList

    // Progress tracking
    var initializationProgress by mutableFloatStateOf(0f)
        private set

    var isInitializing by mutableStateOf(false)
        private set

    init {
        viewModelScope.launch {
            try {
                isInitializing = true
                _1Initialize { progress ->
                    initializationProgress = progress
                }
            } finally {
                isInitializing = false
            }
        }
    }

    // Function to update the UiStateSnapshotStateList
    fun updateUiState(newState: UiStateSnapshotStateList) {
        _uiStateSnapshotStateList = newState
    }

    // Function to reset the UiStateSnapshotStateList
    fun resetUiState() {
        _uiStateSnapshotStateList = UiStateSnapshotStateList(
            initialLastUpdateTime = System.currentTimeMillis().toString(),
            initialGroupFireBaseReference = emptyList()
        )
    }
}
