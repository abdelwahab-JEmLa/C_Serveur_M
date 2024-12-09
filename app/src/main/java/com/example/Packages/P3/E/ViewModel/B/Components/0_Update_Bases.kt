package com.example.Packages.P3.E.ViewModel.B.Components

import androidx.lifecycle.viewModelScope
import com.example.Models.Grossissts_DataBAse
import com.example.Packages.P3.E.ViewModel.ViewModelFragment
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

fun ViewModelFragment.Parent_Ui_Statue_DataBase_Update(nameVal:String, Data: Any?) {
    viewModelScope.launch {
       
        _Ui_Statue_DataBase.update { currentState ->
            when (nameVal) {
                "grossisst_Au_Filtre_Mnt" -> currentState.copy(grossisst_Au_Filtre_Mnt = Data as? Grossissts_DataBAse)
                "mode_click_is_trensfert_to_fab_gross" -> currentState.copy(mode_click_is_trensfert_to_fab_gross = Data as Boolean)
                else -> currentState
            }
        }
    }
}
