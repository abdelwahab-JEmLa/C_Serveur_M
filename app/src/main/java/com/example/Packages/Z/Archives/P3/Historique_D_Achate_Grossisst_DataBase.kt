package com.example.Packages.Z.Archives.P3

import androidx.lifecycle.viewModelScope
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.Packages.Z.Archives.P3.E.ViewModel.ViewModelFragment
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@Entity
data class Historique_D_Achate_Grossisst_DataBase(
    @PrimaryKey var vid: Long = 0,
    var produit_id: Long = 0,
    var produit_nom: String = "",
    var grossisst_id: Long = 0,
    var time_Achat: String = "", //"yyyy-mm-dd/hh:mm"   
) {
    constructor() : this(0)
    companion object {
        fun ViewModelFragment.initializeData() {
            viewModelScope.launch {
                dataBase.historique_D_Achate_Grossisst_DataBase_Dao().getAllFlow().collect { Historique_D_Achate_Grossisst_DataBase ->
                    _Ui_Statue_DataBase.update { currentState ->
                        currentState.copy(
                            historique_D_Achate_Grossisst_DataBase = Historique_D_Achate_Grossisst_DataBase,
                        )
                    }
                }
            }
        }
    }
}
