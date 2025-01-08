package com.example.Packages.A_GrosssitsCommendHandler.A1_Fragment.A_Head.Model

class Maps {
    var mapGroToMapPositionToProduits:
            Map<GrossistInfosModel,
                    Map<TypePosition,Map<ArticleInfosModel,Map<ColourEtGoutInfosModel, Double>>>> =
        mutableMapOf()

    var positionedArticles: Map<ArticleInfosModel, Map<ColourEtGoutInfosModel, Double>> =
        mutableMapOf()

    var nonPositionedArticles: Map<ArticleInfosModel, Map<ColourEtGoutInfosModel, Double>> =
        mutableMapOf()
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


