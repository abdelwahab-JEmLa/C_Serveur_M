package com.example.Packages.A_GrosssitsCommendHandler.A1_Fragment.A_Head

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.google.firebase.Firebase
import com.google.firebase.database.database
import kotlinx.coroutines.tasks.await

class Model_CodingWithMaps {
    var mutableStatesVars by mutableStateOf(MutableStatesVars())

    class MutableStatesVars {
        var mapGrossistIdToProduitId: SnapshotStateList<Mapping.Grossist> =
            mutableStateListOf()

        var positionedProduits: SnapshotStateList<Mapping.Grossist.Produits> =
            mutableStateListOf()

        var unPositionedProduits: SnapshotStateList<Mapping.Grossist.Produits> =
            mutableStateListOf()

    }

    class Mapping {
        data class Grossist(
            var grossistId: Long = 0,
            var produits: SnapshotStateList<Produits> = mutableStateListOf()
        ) {
            data class Produits(
                var produitId: Long = 0,
                var commendCouleurs: SnapshotStateList<CommendCouleurs> = mutableStateListOf()
            ) {
                data class CommendCouleurs(
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
