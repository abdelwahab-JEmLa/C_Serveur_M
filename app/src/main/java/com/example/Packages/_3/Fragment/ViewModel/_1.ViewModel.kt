package com.example.Packages._3.Fragment.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.Packages._3.Fragment.Models.Ui_Mutable_State
import com.example.Packages._3.Fragment.ViewModel.Test.Test_Initiale_Calcules_Autre_Valeurs
import com.example.Packages._3.Fragment.ViewModel.init.Init_ImportCalcules_Ui_Stat
import com.google.firebase.Firebase
import com.google.firebase.database.database
import kotlinx.coroutines.launch

internal class P3_ViewModel : ViewModel() {
    private val database = Firebase.database
    val refFirebase = database.getReference("_1_Prototype4Dec_3_Host_Package_3_DataBase")

    var _ui_Mutable_State = Ui_Mutable_State()
    val ui_Mutable_State: Ui_Mutable_State get() = _ui_Mutable_State

    init {

        _ui_Mutable_State.logGroupingDetails("P3_ViewModel")

        // Launch a coroutine using viewModelScope
        viewModelScope.launch {
                Init_ImportCalcules_Ui_Stat()
                //Test_Initiale_Calcules_Autre_Valeurs()
        }

        _ui_Mutable_State.logGroupingDetails("P3_ViewModel")
    }
}
