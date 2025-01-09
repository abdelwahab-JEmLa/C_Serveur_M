package com.example.Packages.A_GrosssitsCommendHandler.A1_Fragment.A_Head.Model

import androidx.compose.runtime.mutableStateListOf

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


