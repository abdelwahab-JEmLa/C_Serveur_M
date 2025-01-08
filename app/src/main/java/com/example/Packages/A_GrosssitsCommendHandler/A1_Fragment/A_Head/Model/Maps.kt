package com.example.Packages.A_GrosssitsCommendHandler.A1_Fragment.A_Head.Model

class Maps {
    var mapGroToMapPositionToProduits:
            MutableList<Map.Entry<GrossistInfosModel,
                    Map<TypePosition,
                            MutableList<Map.Entry<ArticleInfosModel,
                                    MutableList<Map.Entry<ColourEtGoutInfosModel, Double>>>>>>> =
        mutableListOf()

    var positionedArticles: MutableList<Map.Entry<ArticleInfosModel,
            MutableList<Map.Entry<ColourEtGoutInfosModel, Double>>>> =
        mutableListOf()

    var nonPositionedArticles: MutableList<Map.Entry<ArticleInfosModel,
            MutableList<Map.Entry<ColourEtGoutInfosModel, Double>>>> =
        mutableListOf()
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


