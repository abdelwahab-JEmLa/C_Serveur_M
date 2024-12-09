package com.example.Packages._3.Fragment.ViewModel.Main

import com.example.Packages._3.Fragment.Models.Ui_Mutable_State
import com.example.Packages._3.Fragment.ViewModel.P3_ViewModel

internal fun P3_ViewModel.Sync_With_Firebase(data: Ui_Mutable_State, remove: Boolean = false) {
        if (remove) {
            refFirebase.removeValue()
        } else {
            refFirebase.setValue(data)
        }
    }
