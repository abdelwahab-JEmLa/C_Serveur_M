package com.example.Packages.A_GrosssitsCommendHandler.A1_Fragment.A_Head

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.Apps_Head._1.Model.AppsHeadModel
import com.google.firebase.Firebase
import com.google.firebase.database.database

class Model_CodingWithMaps {
    var maps by mutableStateOf(Maps())

    class Maps {
        var grossistList: List<Pair<AppsHeadModel.ProduitModel.GrossistBonCommandes
        .GrossistInformations,
                List<AppsHeadModel.ProduitModel>>> by mutableStateOf(emptyList())

        var visibleGrossistAssociatedProduits: Pair<AppsHeadModel.ProduitModel.GrossistBonCommandes.GrossistInformations, List<AppsHeadModel.ProduitModel>> by mutableStateOf(
            // Initialize with an empty grossist and empty list as default
            Pair(
                AppsHeadModel.ProduitModel.GrossistBonCommandes.GrossistInformations(),
                emptyList()
            )
        )
    }

    companion object {
        val mapsFireBaseRef = Firebase.database
            .getReference("0_UiState_3_Host_Package_3_Prototype11Dec")
            .child("A_CodingWithListsPatterns")
    }
}
