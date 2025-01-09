package com.example.Packages.A_GrosssitsCommendHandler.A1_Fragment.A_Head.Model


import androidx.compose.runtime.mutableStateListOf
import com.example.Packages.A_GrosssitsCommendHandler.A1_Fragment.A_Head.Model.ViewModel.ViewModel_Head
import com.google.firebase.Firebase
import com.google.firebase.database.database
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.AbstractMap

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

    companion object {
        private val mapsRef = Firebase.database
            .getReference("0_UiState_3_Host_Package_3_Prototype11Dec")
            .child("Maps")
            .child("mapGroToMapPositionToProduits")

        suspend fun batchUpdateCompan(firebaseData: List<Map<String, Any>>) {
            mapsRef.updateChildren(
                firebaseData.mapIndexed { index, data ->
                    "/$index" to data
                }.toMap()
            ).await()
        }

        fun updateMapFromPositionedLists(grossistId: Long, viewModel_Head: ViewModel_Head,itsDeplacement:Boolean?=false) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val _maps = viewModel_Head._maps
                    val mapGroToMapPositionToProduits = _maps.mapGroToMapPositionToProduits
                    if (itsDeplacement == true) {
                        _maps.positionedArticles.clear()
                        _maps.nonPositionedArticles.clear()

                        _maps.positionedArticles.addAll(
                            mapGroToMapPositionToProduits.find { it.key.id == grossistId }
                                ?.value?.get(TypePosition.POSITIONE) ?: mutableListOf()
                        )
                        _maps.nonPositionedArticles.addAll(
                            mapGroToMapPositionToProduits.find { it.key.id == grossistId }
                                ?.value?.get(TypePosition.NON_POSITIONE) ?: mutableListOf()
                        )
                    }
                    val grossistEntry =
                        mapGroToMapPositionToProduits.find { it.key.id == grossistId }
                            ?: throw IllegalStateException("Grossist with ID $grossistId not found")

                    // Create the Firebase data structure
                    val firebaseData = mapGroToMapPositionToProduits.map { entry ->
                        if (entry.key.id == grossistId) {
                            mapOf(
                                "grossistInfo" to mapOf(
                                    "id" to entry.key.id,
                                    "nom" to entry.key.nom
                                ),
                                "products" to mapOf(
                                    "POSITIONE" to _maps.positionedArticles.map { article ->
                                        formatArticleForFirebase(article)
                                    },
                                    "NON_POSITIONE" to _maps.nonPositionedArticles.map { article ->
                                        formatArticleForFirebase(article)
                                    }
                                )
                            ) as Map<String, Any>
                        } else {
                            mapOf(
                                "grossistInfo" to mapOf(
                                    "id" to entry.key.id,
                                    "nom" to entry.key.nom
                                ),
                                "products" to entry.value.map { (position, articles) ->
                                    position.name to articles.map { formatArticleForFirebase(it) }
                                }.toMap()
                            ) as Map<String, Any>
                        }
                    }

                    // Update Firebase
                    batchUpdateCompan(firebaseData)

                    // Update local state
                    val updatedPositionMap =
                        mutableMapOf<TypePosition, MutableList<Map.Entry<ArticleInfosModel, MutableList<Map.Entry<ColourEtGoutInfosModel, Double>>>>>()
                    updatedPositionMap[TypePosition.POSITIONE] =
                        _maps.positionedArticles.toMutableList()
                    updatedPositionMap[TypePosition.NON_POSITIONE] =
                        _maps.nonPositionedArticles.toMutableList()

                    val grossistIndex =
                        mapGroToMapPositionToProduits.indexOfFirst { it.key.id == grossistId }
                    if (grossistIndex != -1) {
                        mapGroToMapPositionToProduits[grossistIndex] = AbstractMap.SimpleEntry(
                            grossistEntry.key,
                            updatedPositionMap
                        )
                    }
                    if (itsDeplacement == true) {
                        _maps.positionedArticles.clear()
                        _maps.nonPositionedArticles.clear()
                    }
                } catch (e: Exception) {
                    throw e
                }
            }
        }

        private fun formatArticleForFirebase(article: Map.Entry<ArticleInfosModel, MutableList<Map.Entry<ColourEtGoutInfosModel, Double>>>): Map<String, Any> {
            return mapOf(
                "articleInfo" to mapOf(
                    "id" to article.key.id,
                    "nom" to article.key.nom,
                    "besoinToBeUpdated" to article.key.besoinToBeUpdated,
                    "sonImageBesoinActualisation" to article.key.sonImageBesoinActualisation,
                    "imageGlidReloadTigger" to article.key.imageGlidReloadTigger
                ),
                "colors" to article.value.map { colorEntry ->
                    mapOf(
                        "colorInfo" to mapOf(
                            "id" to colorEntry.key.id,
                            "nom" to colorEntry.key.nom,
                            "imogi" to colorEntry.key.imogi
                        ),
                        "quantity" to colorEntry.value
                    )
                }
            )
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


