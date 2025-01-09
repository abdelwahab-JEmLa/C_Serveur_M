// ParamatersAppsModel.kt
package com.example.Apps_Head.B_AppsSetingsMaster.Model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.google.firebase.Firebase
import com.google.firebase.database.database

class ParamatersAppsModel {
    // Using proper delegate pattern with non-null initial value
    var telephoneClientParamaters by mutableStateOf(TelephoneClientParamaters())

    class TelephoneClientParamaters {
        var selectedGrossistIndex by mutableStateOf<Int?>(null)

    }

    companion object {
        private const val SELF_CHEMIN_BASE = "0_UiState_3_Host_Package_3_Prototype11Dec/ParamatersAppsModel"
        val refSelfFireBase = Firebase.database.getReference(SELF_CHEMIN_BASE)
    }
}
