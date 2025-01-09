package com.example.Packages.A_GrosssitsCommendHandler.A1_Fragment.A_Head.Model.init

import com.example.Apps_Head._1.Model.ProduitsAncienDataBaseMain
import com.example.Apps_Head._4.Init.GetAncienDataBasesMain
import com.example.Packages.A_GrosssitsCommendHandler.A1_Fragment.A_Head.Model.GrossistInfosModel
import com.example.Packages.A_GrosssitsCommendHandler.A1_Fragment.A_Head.Model.Maps.Companion.batchUpdateCompan
import com.example.Packages.A_GrosssitsCommendHandler.A1_Fragment.A_Head.Model.TypePosition

suspend fun startImplementationViewModel(
    nombreEntries: Int = 100,
    onInitProgress: (Int) -> Unit
) {
    try {
        if (nombreEntries <= 0) return

        // 1. Récupérer les données anciennes
        val ancienData = GetAncienDataBasesMain()

        // 2. Définir les grossistes
        val grossists = listOf(
            GrossistInfosModel(1, "Grossist Alpha"),
            GrossistInfosModel(2, "Grossist Beta"),
            GrossistInfosModel(3, "Grossist Gamma")
        )

        // 3. Filtrer et distribuer les produits
        val allProducts = ancienData.produitsDatabase
            .filter { it.idArticle != 0L } // Filter out invalid products
            .shuffled() // Randomize the order for fair distribution

        // Calculate products per grossist
        val productsPerGrossist = nombreEntries / grossists.size

        // Distribute products among grossists
        val grossistProducts = List(grossists.size) { index ->
            val startIndex = index * productsPerGrossist
            val endIndex = if (index == grossists.size - 1) {
                minOf(allProducts.size, nombreEntries)
            } else {
                startIndex + productsPerGrossist
            }
            allProducts.subList(startIndex, minOf(endIndex, allProducts.size))
        }

        // 4. Créer la structure de données pour Firebase
        val firebaseData = grossists.mapIndexed { grossistIndex, grossist ->
            val products = grossistProducts[grossistIndex]
            val productsMap = mutableMapOf<String, List<Map<String, Any>>>()

            // Partition products based on ID parity for positioning
            val (positioned, nonPositioned) = products.partition { (it.idArticle % 2).toInt() == 0 }

            // Mapper les produits avec fonction commune
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
                                    "imogi" to it.second?.let { it1 -> getEmojiForColor(it1) }
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


        batchUpdateCompan(firebaseData)

        // 6. Mise à jour du progrès
        onInitProgress(100)

    } catch (e: Exception) {
        throw e
    }
}

// Helper function to assign appropriate emojis based on color names
private fun getEmojiForColor(colorName: String): String {
    return when {
        colorName.contains("chocolat", ignoreCase = true) -> "🍫"
        colorName.contains("fraise", ignoreCase = true) -> "🍓"
        colorName.contains("banane", ignoreCase = true) -> "🍌"
        colorName.contains("lait", ignoreCase = true) -> "🥛"
        colorName.contains("ceris", ignoreCase = true) -> "🍒"
        colorName.contains("caramel", ignoreCase = true) -> "🥞"
        colorName.contains("fruité", ignoreCase = true) -> "🍡"
        colorName.contains("noix", ignoreCase = true) -> "🥥"
        colorName.contains("nougat", ignoreCase = true) -> "🎇"
        colorName.contains("oreo", ignoreCase = true) -> "🍪"
        colorName.contains("reglize", ignoreCase = true) -> "🍙"
        colorName.contains("standard", ignoreCase = true) -> "🎁"
        colorName.contains("multi", ignoreCase = true) -> "🎨"
        else -> "📦"
    }
}
