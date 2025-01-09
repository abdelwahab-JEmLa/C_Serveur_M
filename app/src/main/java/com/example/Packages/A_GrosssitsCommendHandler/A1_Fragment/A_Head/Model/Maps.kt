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

        fun updateMapFromPositionedLists(grossistId: Long, viewModel_Head: ViewModel_Head) {
            // Launch in a coroutine scope to handle suspend functions
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val _maps = viewModel_Head._maps
                    val mapGroToMapPositionToProduits = _maps.mapGroToMapPositionToProduits
                    val grossistEntry = mapGroToMapPositionToProduits.find { it.key.id == grossistId }
                        ?: throw IllegalStateException("Grossist with ID $grossistId not found")

                    // Create the updated position map
                    val updatedPositionMap = mutableMapOf<TypePosition, MutableList<Map.Entry<ArticleInfosModel, MutableList<Map.Entry<ColourEtGoutInfosModel, Double>>>>>()

                    // Add positioned and non-positioned articles to their respective lists
                    updatedPositionMap[TypePosition.POSITIONE] = _maps.positionedArticles.toMutableList()
                    updatedPositionMap[TypePosition.NON_POSITIONE] = _maps.nonPositionedArticles.toMutableList()

                    // Create the Firebase data structure with correct types
                    val firebaseData = mapGroToMapPositionToProduits.map { entry ->
                        if (entry.key.id == grossistId) {
                            mapOf(
                                "grossistInfo" to mapOf(
                                    "id" to entry.key.id,
                                    "nom" to entry.key.nom
                                ),
                                "products" to mapOf(
                                    TypePosition.POSITIONE.name to (updatedPositionMap[TypePosition.POSITIONE] ?: mutableListOf())
                                        .map { article -> formatArticleForFirebase(article) },
                                    TypePosition.NON_POSITIONE.name to (updatedPositionMap[TypePosition.NON_POSITIONE] ?: mutableListOf())
                                        .map { article -> formatArticleForFirebase(article) }
                                )
                            ) as Map<String, Any>
                        } else {
                            mapOf(
                                "grossistInfo" to mapOf(
                                    "id" to entry.key.id,
                                    "nom" to entry.key.nom
                                ),
                                "products" to entry.value.mapValues { (_, articles) ->
                                    articles.map { formatArticleForFirebase(it) }
                                }
                            ) as Map<String, Any>
                        }
                    }

                    // Update Firebase
                    batchUpdateCompan(firebaseData)

                    // Update local state
                    val grossistIndex = mapGroToMapPositionToProduits.indexOfFirst { it.key.id == grossistId }
                    if (grossistIndex != -1) {
                        mapGroToMapPositionToProduits[grossistIndex] = AbstractMap.SimpleEntry(
                            grossistEntry.key,
                            updatedPositionMap
                        )
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


