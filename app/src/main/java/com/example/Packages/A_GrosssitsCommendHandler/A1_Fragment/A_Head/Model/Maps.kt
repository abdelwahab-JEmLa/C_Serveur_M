package com.example.Packages.A_GrosssitsCommendHandler.A1_Fragment.A_Head.Model

import androidx.compose.runtime.mutableStateListOf
import com.google.firebase.Firebase
import com.google.firebase.database.database
import kotlinx.coroutines.tasks.await

class Maps {
    // Liste principale
    var mapGroToMapPositionToProduits = mutableStateListOf<Map.Entry<GrossistInfosModel,
            Map<TypePosition,
                    MutableList<Map.Entry<ArticleInfosModel,
                            MutableList<Map.Entry<ColourEtGoutInfosModel, Double>>>>>>>()

    // Listes dérivées
    var positionedArticles = mutableStateListOf<Map.Entry<ArticleInfosModel,
            MutableList<Map.Entry<ColourEtGoutInfosModel, Double>>>>()

    var nonPositionedArticles = mutableStateListOf<Map.Entry<ArticleInfosModel,
            MutableList<Map.Entry<ColourEtGoutInfosModel, Double>>>>()
    companion object{
        val mapsRef = Firebase.database
            .getReference("0_UiState_3_Host_Package_3_Prototype11Dec")
            .child("Maps")
            .child("mapGroToMapPositionToProduits")
        suspend fun batchUpdate(firebaseData: List<Map<String, Map<String, Any>>>) {
            mapsRef.updateChildren(
                firebaseData.mapIndexed { index, data ->
                    "/$index" to data
                }.toMap()
            ).await()
        }
    }
}

enum class TypePosition { POSITIONE, NON_POSITIONE }

class ArticleInfosModel(
    var id: Long = 0,
    var nom: String = "",
    var besoinToBeUpdated: Boolean = false,
    var sonImageBesoinActualisation: Boolean = false,
    var imageGlidReloadTigger: Int = 0,
)

class GrossistInfosModel(
    var id: Long = 0,
    var nom: String = "",
)

class ColourEtGoutInfosModel(
    var id: Long = 0,
    var nom: String = "",
    var imogi: String = "",
)


