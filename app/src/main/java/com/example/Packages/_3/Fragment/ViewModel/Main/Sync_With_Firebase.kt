package com.example.Packages._3.Fragment.ViewModel.Main

import com.example.Packages._3.Fragment.ViewModel._2.Init.Main.Model.Archives.Ui_Mutable_State
import com.example.Packages._3.Fragment.ViewModel.P3_ViewModel

internal fun P3_ViewModel.Sync_With_Firebase(data: Ui_Mutable_State, remove: Boolean = false) {
        if (remove) {
            ref_ViewModel_Produit_DataBase.removeValue()
        } else {
            ref_ViewModel_Produit_DataBase.setValue(data)
        }
    }
