package com.example.Packages.Z.Archives.Models

import androidx.lifecycle.viewModelScope
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.Packages.Z.Archives.P3.E.ViewModel.ViewModelFragment
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@Entity
data class Grossissts_DataBAse(
    @PrimaryKey (autoGenerate = true)
    var idSupplierSu: Long = 0,
    var nomSupplierSu: String = "",
    var nomVocaleArabeDuSupplier: String = "",
    var nameInFrenche: String = "",
    var bonDuSupplierSu: String = "",
    val couleurSu: String = "#FFFFFF",
    var currentCreditBalance: Double = 0.0,
    var longTermCredit : Boolean = false,
    var ignoreItProdects: Boolean = false,
    var classmentSupplier: Double = 0.0,
    ) {
    constructor() : this(0)
    companion object {
        fun ViewModelFragment.init_Collect_Grossissts_DataBAse() {
            viewModelScope.launch {
                dataBase.grossissts_DataBAse_Dao().getAllFlow().collect {
                    _Ui_Statue_DataBase.update { currentState ->
                        currentState.copy(
                            grossissts_DataBAse = it,
                        )
                    }
                }
            }
        }
    }

}
