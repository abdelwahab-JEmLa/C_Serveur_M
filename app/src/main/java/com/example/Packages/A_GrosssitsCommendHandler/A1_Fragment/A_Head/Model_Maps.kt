package com.example.Packages.A_GrosssitsCommendHandler.A1_Fragment.A_Head

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.Apps_Head._1.Model.AppsHeadModel
import com.google.firebase.Firebase
import com.google.firebase.database.database
import kotlinx.coroutines.tasks.await

class Model_CodingWithMaps {
    var maps by mutableStateOf(MutableStatesVars())

    class MutableStatesVars {
        var grossistList: List<Pair<AppsHeadModel.ProduitModel.GrossistBonCommandes.GrossistInformations,
                List<AppsHeadModel.ProduitModel>>> by mutableStateOf(emptyList())

        var mapGrossistIdToProduitId: SnapshotStateList<Maper.MapGrossistIdToProduitId> =
            mutableStateListOf()

        var visibleGrossistAssociatedProduits: Pair<AppsHeadModel.ProduitModel.GrossistBonCommandes.GrossistInformations,
                List<AppsHeadModel.ProduitModel>> by mutableStateOf(
            Pair(
                AppsHeadModel.ProduitModel.GrossistBonCommandes.GrossistInformations(),
                emptyList()
            )
        )
    }

    class Maper {
        data class MapGrossistIdToProduitId(
            var grossistId: Long = 0,
            var produits: SnapshotStateList<Produit> = mutableStateListOf()
        ) {
            data class Produit(
                var produitId: Long = 0,
                var commendCouleurs: SnapshotStateList<CommendCouleur> = mutableStateListOf()
            ) {
                data class CommendCouleur(
                    var couleurId: Long = 0,
                    var quantityCommend: Int = 0
                )
            }
        }
    }

    companion object {
        val mapsFireBaseRef = Firebase.database
            .getReference("0_UiState_3_Host_Package_3_Prototype11Dec")
            .child("A_CodingWithListsPatterns")

        suspend fun batchFireBaseUpdateGrossist(grossists: List<Map<String, Any>>) {
            // Mettre à jour Firebase
            val updates = grossists.mapIndexed { index, grossist ->
                "/$index" to grossist
            }.toMap()

            mapsFireBaseRef.updateChildren(updates).await()
        }
    }
}
