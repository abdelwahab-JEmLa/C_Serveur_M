// ParamatersAppsModel.kt
package com.example.Z_AppsFather.Parent._1.Model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.Y_AppsFather.Kotlin.ModelAppsFather.ProduitModel.GrossistBonCommandes.GrossistInformations
import com.google.firebase.Firebase
import com.google.firebase.database.database

class ParamatersAppsModel {
    // Using proper delegate pattern with non-null initial value
    var telephoneClientParamaters by mutableStateOf(TelephoneClientParamaters())

    class TelephoneClientParamaters {
        var selectedGrossist by mutableStateOf<GrossistInformations?>(null)
    }

    companion object {
        private const val SELF_CHEMIN_BASE = "0_UiState_3_Host_Package_3_Prototype11Dec/ParamatersAppsModel"
        val refSelfFireBase = Firebase.database.getReference(SELF_CHEMIN_BASE)
    }
}
