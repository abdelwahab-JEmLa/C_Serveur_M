// P3_ViewModel.kt
package com.example.Packages._3.Fragment.ViewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.Packages._3.Fragment.Models.Ui_Mutable_State
import com.example.Packages._3.Fragment.ViewModel.init._1.Aliment_From_Authers_Refs.init.Init_ImportCalcules_Ui_Stat
import com.google.firebase.Firebase
import com.google.firebase.database.database
import kotlinx.coroutines.launch

internal class P3_ViewModel : ViewModel() {
    private val database = Firebase.database
    val refFirebase = database.getReference("_1_Prototype4Dec_3_Host_Package_3_DataBase")

    var _ui_Mutable_State = Ui_Mutable_State()
    val ui_Mutable_State: Ui_Mutable_State get() = _ui_Mutable_State

    // Progress tracking
    var initializationProgress by mutableFloatStateOf(0f)
    var isInitializing by mutableStateOf(false)

    init {
        viewModelScope.launch {
            isInitializing = true
            Init_ImportCalcules_Ui_Stat { progress ->
                initializationProgress = progress
            }
            isInitializing = false
        }
    }
}
