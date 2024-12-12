package com.example.Packages._3.Fragment.ViewModel._2.Init.Main.Components

import android.util.Log
import com.example.Packages._3.Fragment.ViewModel.P3_ViewModel
import com.example.Packages._3.Fragment.ViewModel._2.Init.Main.Model._1.MAIN.UiState

const val TAG_Snap = "InitialeUiState"

internal suspend fun P3_ViewModel._1Initialize() {
    try {
        Log.d(TAG_Snap, "Starting _1Initialize")
        initializationProgress = 0.1f  // Better float representation

        // First Init
        if (true) { // Replace with actual condition
            List(200) { i ->
                val produit = UiState.Produit_DataBase(
                    id = i.toLong(),
                    it_ref_Id_don_FireBase = 1L,
                    it_ref_don_FireBase = "produit_DataBase",
                    init_besoin_To_Be_Updated = true
                )
                _uiState.produit_DataBase.add(produit)
            }
        }

        initializationProgress = 0.3f

        // Get ancient data
        val ancienData = get_Ancien_Datas()

        // Update products
        _uiState.produit_DataBase.forEach { new_produit_A_Update ->
            // Find matching ancient product
            ancienData.produitsDatabase.find { it.idArticle == new_produit_A_Update.id }?.let { ancien_DataBase ->
                new_produit_A_Update.nom = ancien_DataBase.nomArticleFinale

                // Process colors more efficiently
                listOf(
                    Triple(ancien_DataBase.idcolor1, 1L, new_produit_A_Update.colours_Et_Gouts),
                    Triple(ancien_DataBase.idcolor2, 2L, new_produit_A_Update.colours_Et_Gouts),
                    Triple(ancien_DataBase.idcolor3, 3L, new_produit_A_Update.colours_Et_Gouts),
                    Triple(ancien_DataBase.idcolor4, 4L, new_produit_A_Update.colours_Et_Gouts)
                ).forEach { (colorId, position, colorsList) ->
                    ancienData.couleurs_List.find { it.idColore == colorId }?.let { color ->
                        colorsList.add(
                            UiState.Produit_DataBase.Colours_Et_Gouts(
                                position_Du_Couleur_Au_Produit = position,
                                nom = color.nameColore,
                                imogi = color.iconColore
                            )
                        )
                    }
                }
            }

            // First group all sales by client ID and article ID
            val salesByClientAndArticle = ancienData.soldArticles
                .groupBy { it.clientSoldToItId }
                .mapValues { (_, sales) ->
                    sales.groupBy { it.idArticle }
                }

            salesByClientAndArticle.forEach { (clientId, articleSales) ->
                articleSales[new_produit_A_Update.id]?.forEachIndexed { index, ancien_soldArticles ->
                    val newAchate = UiState.Produit_DataBase.Demmende_Achate_De_Cette_Produit(
                        vid = (index + 1).toLong(),
                        id_Acheteur = clientId,
                        initial_Colours_Et_Gouts_Acheter_Depuit_Client = emptyList()
                    )

                    // Process all colors with explicit types
                    val colorMappings: List<Pair<Long, Int>> = listOf(
                        Pair(1L, ancien_soldArticles.color1SoldQuantity),
                        Pair(2L, ancien_soldArticles.color2SoldQuantity),
                        Pair(3L, ancien_soldArticles.color3SoldQuantity),
                        Pair(4L, ancien_soldArticles.color4SoldQuantity)
                    )

                    colorMappings.forEach { (position, quantity) ->
                        new_produit_A_Update.colours_Et_Gouts.find {
                            it.position_Du_Couleur_Au_Produit == position
                        }?.let { color ->
                            if (quantity > 0) {  // Only add colors with actual purchases
                                newAchate.colours_Et_Gouts_Acheter_Depuit_Client.add(
                                    UiState.Produit_DataBase.Demmende_Achate_De_Cette_Produit.Colours_Et_Gouts_Acheter_Depuit_Client(
                                        vidPosition = position,
                                        nom = color.nom,
                                        quantity_Achete = quantity,
                                        imogi = color.imogi
                                    )
                                )
                            }
                        }
                    }

                    // Only add the purchase if there were actually colors bought
                    if (newAchate.colours_Et_Gouts_Acheter_Depuit_Client.isNotEmpty()) {
                        new_produit_A_Update.demmende_Achate_De_Cette_Produit.add(newAchate)
                    }
                }
            }
            // First calculate total quantities for each color across all buyers
            val totalQuantitiesByColor = mutableMapOf<Long, Int>()

            new_produit_A_Update.demmende_Achate_De_Cette_Produit.forEach { achate ->
                achate.colours_Et_Gouts_Acheter_Depuit_Client.forEach { color ->
                    totalQuantitiesByColor[color.vidPosition] =
                        (totalQuantitiesByColor[color.vidPosition] ?: 0) + color.quantity_Achete
                }
            }

// Create new grossist entry
            val newGrossist = UiState.Produit_DataBase.Grossist_Choisi_Pour_Acheter_Ce_Produit_In_This_Transaction(
                vid = 1L,
                id_Supplier = 1L,
                position_Grossist_Don_Parent_Grossists_List = 0,  // Set appropriate position
                nom = "Default Grossist",  // Set appropriate name
                couleur = "#FFFFFF",  // Set appropriate color
                currentCreditBalance = 0.0,  // Set appropriate initial balance
                position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit = 0,  // Set appropriate position
                initialColours_Et_Gouts_Commende_Au_Supplier = emptyList()
            )

// Add colors to grossist where total quantity > 0
            new_produit_A_Update.colours_Et_Gouts.forEach { productColor ->
                val totalQuantity = totalQuantitiesByColor[productColor.position_Du_Couleur_Au_Produit] ?: 0

                if (totalQuantity > 0) {
                    newGrossist.colours_Et_Gouts_Commende.add(
                        UiState.Produit_DataBase.Grossist_Choisi_Pour_Acheter_Ce_Produit_In_This_Transaction.Colours_Et_Gouts_Commende_Au_Supplier(
                            position_Du_Couleur_Au_Produit = productColor.position_Du_Couleur_Au_Produit,
                            id_Don_Tout_Couleurs = productColor.position_Du_Couleur_Au_Produit,  // You might want to use a different ID
                            nom = productColor.nom,
                            quantity_Achete = totalQuantity,
                            imogi = productColor.imogi
                        )
                    )
                }
            }

// Only add the grossist if they have colors to order
            if (newGrossist.colours_Et_Gouts_Commende.isNotEmpty()) {
                new_produit_A_Update.grossist_Choisi_Pour_Acheter_CeProduit.add(newGrossist)
            }
            new_produit_A_Update.besoin_To_Be_Updated = false
        }

        initializationProgress = 1.0f
        Log.d(TAG_Snap, "Completed _1Initialize")
    } catch (e: Exception) {
        Log.e(TAG_Snap, "Error in _1Initialize", e)
        throw e
    }
}
