package com.example.Packages.A_GrosssitsCommendHandler.A1_Fragment.A_Head.Model.init

import com.example.Apps_Head._1.Model.ProduitsAncienDataBaseMain
import com.example.Apps_Head._4.Init.GetAncienDataBasesMain
import com.example.Packages.A_GrosssitsCommendHandler.A1_Fragment.A_Head.Model.GrossistInfosModel
import com.example.Packages.A_GrosssitsCommendHandler.A1_Fragment.A_Head.Model.TypePosition
import com.google.firebase.Firebase
import com.google.firebase.database.database
import kotlinx.coroutines.tasks.await

suspend fun startImplementationViewModel(
    nombreEntries: Int  = 100,
    onInitProgress: (Int) -> Unit
) {
    try {
        // Si nombreEntries est 0, on sort
        if (nombreEntries <= 0) return

        // 1. Récupérer les données anciennes
        val ancienData = GetAncienDataBasesMain()

        // 2. Définir les grossistes
        val grossists = listOf(
            GrossistInfosModel(1, "Grossist Alpha"),
            GrossistInfosModel(2, "Grossist Beta"),
            GrossistInfosModel(3, "Grossist Gamma")
        )

        // 3. Filtrer les produits
        val halfCount = nombreEntries / 2
        val filteredProducts = ancienData.produitsDatabase.let { products ->
            val older = products.filter { it.idArticle < 2000 }.take(halfCount)
            val newer = products.filter { it.idArticle > 2000 }.take(halfCount)
            (older + newer).take(nombreEntries)
        }

        // 4. Créer la structure de données pour Firebase
        val firebaseData = grossists.map { grossist ->
            val productsMap = mutableMapOf<String, List<Map<String, Any>>>()
            val (positioned, nonPositioned) = filteredProducts.partition { (it.idArticle % 2).toInt() == 0 }

            // Mapper les produits
            val mapProducts = { product: ProduitsAncienDataBaseMain ->
                mapOf(
                    "articleInfo" to mapOf(
                        "id" to product.idArticle,
                        "nom" to product.nomArticleFinale,
                        "besoinToBeUpdated" to false
                    ),
                    "colors" to listOf(
                        product.idcolor1 to product.couleur1,
                        product.idcolor2 to product.couleur2,
                        product.idcolor3 to product.couleur3,
                        product.idcolor4 to product.couleur4
                    ).filter { it.first != 0L && !it.second.isNullOrBlank() }
                        .map {
                            mapOf(
                                "colorInfo" to mapOf(
                                    "id" to it.first,
                                    "nom" to it.second,
                                    "imogi" to "📦"
                                ),
                                "quantity" to (10..50).random()
                            )
                        }
                )
            }

            productsMap[TypePosition.POSITIONE.name] = positioned.map(mapProducts)
            productsMap[TypePosition.NON_POSITIONE.name] = nonPositioned.map(mapProducts)

            mapOf(
                "grossistInfo" to mapOf(
                    "id" to grossist.id,
                    "nom" to grossist.nom
                ),
                "products" to productsMap
            )
        }

        // 5. Mettre à jour Firebase
        val mapsRef = Firebase.database
            .getReference("0_UiState_3_Host_Package_3_Prototype11Dec")
            .child("Maps")
            .child("mapGroToMapPositionToProduits")

        mapsRef.updateChildren(
            firebaseData.mapIndexed { index, data ->
                "/$index" to data
            }.toMap()
        ).await()

        // 6. Mise à jour du progrès
        onInitProgress(100)

    } catch (e: Exception) {
        throw e
    }
}
