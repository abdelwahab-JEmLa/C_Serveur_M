package com.example.Packages.A_GrosssitsCommendHandler.A1_Fragment.A_Head

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.Apps_Head._1.Model.AppsHeadModel
import com.google.firebase.Firebase
import com.google.firebase.database.database

class Model_CodingWithMaps {
    var maps by mutableStateOf(Maps())

    class Maps {
        var grossistList by remember {
            mutableStateOf<Pair<Pair<AppsHeadModel.ProduitModel.GrossistBonCommandes.GrossistInformations, List<AppsHeadModel.ProduitModel>>>>(
                emptyList()
            ) //->
            //TODO(FIXME):Fix erreur Property delegate must have a 'getValue(Model_CodingWithMaps.Maps, KProperty*>)' method. None of the following functions are suitable.
            //State<T>.getValue(Any?, KProperty<*>)   where T cannot be inferred for    inline operator fun <T> State<T>.getValue(thisObj: Any?, property: KProperty<*>): T defined in androidx.compose.runtime
        }
    }

    companion object {
        val mapsFireBaseRef = Firebase.database
            .getReference("0_UiState_3_Host_Package_3_Prototype11Dec")
            .child("A_CodingWithListsPatterns")
    }
}
