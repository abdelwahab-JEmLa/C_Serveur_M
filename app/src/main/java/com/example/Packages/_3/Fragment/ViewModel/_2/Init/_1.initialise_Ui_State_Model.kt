package com.example.Packages._3.Fragment.ViewModel._2.Init.Main.Components

import android.util.Log
import com.example.Packages._3.Fragment.ViewModel.F3_ViewModel
import com.example.Packages._3.Fragment.Models.UiState
import com.example.Packages._3.Fragment.ViewModel._2.Init.Components.Ancien_Resources_DataBase
import com.example.Packages._3.Fragment.ViewModel._2.Init.Components.get_Ancien_Datas
import com.example.Packages._3.Fragment.ViewModel._2.Init.Main.Model.Components.Produits_Ancien_DataBase

private const val TAG_Snap = "InitialeUiState"

internal suspend fun F3_ViewModel.Initialise_ViewModel() {
    try {
        Log.d(TAG_Snap, "Starting Initialise_ViewModel")
        initializationProgress = 0.1f
        isInitializing = true

        val ancienData = get_Ancien_Datas()
        val besoin_update_initialise = false

        if (!besoin_update_initialise) {
            _uiState.loadFromFirebaseDataBase()
        } else {
            // Initialize products database
            ancienData.produitsDatabase.forEach { ancien ->
                val produit = UiState.Produit_DataBase(
                    id = ancien.idArticle,
                    it_ref_Id_don_FireBase = 1L,
                    it_ref_don_FireBase = "produit_DataBase",
                    init_besoin_To_Be_Updated = besoin_update_initialise
                )
                _uiState.produit_DataBase.add(produit)
            }
            initializationProgress = 0.3f

            // Update products with details
            _uiState.produit_DataBase.forEach { new_produit_A_Update ->
                try {
                    // Find and update from ancient database
                    ancienData.produitsDatabase.find { it.idArticle == new_produit_A_Update.id }
                        ?.let { ancien_DataBase ->
                            new_produit_A_Update.nom = ancien_DataBase.nomArticleFinale

                            // Process colors
                            processColors(ancien_DataBase, ancienData, new_produit_A_Update)
                        }

                    // Process sales data
                    processSalesData(ancienData, new_produit_A_Update)

                    // Process wholesaler data
                    processWholesalerData(new_produit_A_Update)

                    new_produit_A_Update.besoin_To_Be_Updated = false

                } catch (e: Exception) {
                    Log.e(TAG_Snap, "Error processing product ${new_produit_A_Update.id}", e)
                }
            }

            _uiState.update_UiStateFirebaseDataBase()
        }

        initializationProgress = 1.0f
        initializationComplete = true
        Log.d(TAG_Snap, "Completed Initialise_ViewModel")

    } catch (e: Exception) {
        Log.e(TAG_Snap, "Error in Initialise_ViewModel", e)
        throw e
    } finally {
        isInitializing = false
    }
}

private fun processColors(
    ancien_Produits_DataBase: Produits_Ancien_DataBase,
    ancien_Data_References: Ancien_Resources_DataBase,
    new_produit_A_Update: UiState.Produit_DataBase
) {
    val colorIds = listOf(
        ancien_Produits_DataBase.idcolor1 to 1L,
        ancien_Produits_DataBase.idcolor2 to 2L,
        ancien_Produits_DataBase.idcolor3 to 3L,
        ancien_Produits_DataBase.idcolor4 to 4L
    )

    colorIds.forEach { (colorId, position) ->
        ancien_Data_References.couleurs_List.find { it.idColore == colorId }?.let { color ->
            new_produit_A_Update.colours_Et_Gouts.add(
                UiState.Produit_DataBase.Colours_Et_Gouts(
                    position_Du_Couleur_Au_Produit = position,
                    nom = color.nameColore,
                    imogi = color.iconColore
                )
            )
        }
    }
}

private fun processSalesData(
    ancien_Data_References: Ancien_Resources_DataBase,
    new_produit_A_Update: UiState.Produit_DataBase
) {
    val salesByClientAndArticle = ancien_Data_References.soldArticles
        .groupBy { it.clientSoldToItId }
        .mapValues { (_, sales) ->
            sales.groupBy { it.idArticle }
        }

    salesByClientAndArticle.forEach { (clientId, articleSales) ->
        articleSales[new_produit_A_Update.id]?.forEachIndexed { index, ancien_soldArticles ->
            ancien_Data_References.clients_List.find { it.idClientsSu == clientId }?.let { client_Data ->

                val newAchate = UiState.Produit_DataBase.Demmende_Achate_De_Cette_Produit(
                    vid = (index + 1).toLong(),
                    id_Acheteur = clientId,
                    nom_Acheteur = client_Data.nomClientsSu,
                    initial_Colours_Et_Gouts_Acheter_Depuit_Client = emptyList()
                )

                // Process color quantities
                val colorQuantities = listOf(
                    1L to ancien_soldArticles.color1SoldQuantity,
                    2L to ancien_soldArticles.color2SoldQuantity,
                    3L to ancien_soldArticles.color3SoldQuantity,
                    4L to ancien_soldArticles.color4SoldQuantity
                )

                colorQuantities.forEach { (position, quantity) ->
                    if (quantity > 0) {
                        new_produit_A_Update.colours_Et_Gouts.find {
                            it.position_Du_Couleur_Au_Produit == position
                        }?.let { color ->
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

                if (newAchate.colours_Et_Gouts_Acheter_Depuit_Client.isNotEmpty()) {
                    new_produit_A_Update.demmende_Achate_De_Cette_Produit.add(newAchate)
                }
            }
        }
    }
}

private fun processWholesalerData(new_produit_A_Update: UiState.Produit_DataBase) {
    val sampleWholesalers = listOf(
        createWholesaler(1L, "Wholesaler Alpha", "#FF5733", 1000.0),
        createWholesaler(2L, "Wholesaler Beta", "#33FF57", 1500.0),
        createWholesaler(3L, "Wholesaler Gamma", "#5733FF", 2000.0)
    )

    new_produit_A_Update.grossist_Choisi_Pour_Acheter_CeProduit.clear()

    // Add random wholesaler with minimum order
    val selectedWholesaler = sampleWholesalers.random()
    val wholesalerOrder = createWholesalerOrder(selectedWholesaler, new_produit_A_Update)
    new_produit_A_Update.grossist_Choisi_Pour_Acheter_CeProduit.add(wholesalerOrder)
}

private fun createWholesaler(
    id: Long,
    name: String,
    color: String,
    balance: Double
): UiState.Produit_DataBase.Grossist_Choisi_Pour_Acheter_Ce_Produit_In_This_Transaction {
    return UiState.Produit_DataBase.Grossist_Choisi_Pour_Acheter_Ce_Produit_In_This_Transaction(
        vid = id,
        supplier_id = id,
        nom = name,
        position_Grossist_Don_Parent_Grossists_List = id.toInt() - 1,
        couleur = color,
        currentCreditBalance = balance
    )
}

private fun createWholesalerOrder(
    wholesaler: UiState.Produit_DataBase.Grossist_Choisi_Pour_Acheter_Ce_Produit_In_This_Transaction,
    product: UiState.Produit_DataBase
): UiState.Produit_DataBase.Grossist_Choisi_Pour_Acheter_Ce_Produit_In_This_Transaction {
    return UiState.Produit_DataBase.Grossist_Choisi_Pour_Acheter_Ce_Produit_In_This_Transaction(
        vid = wholesaler.vid,
        supplier_id = wholesaler.supplier_id,
        nom = wholesaler.nom,
        position_Grossist_Don_Parent_Grossists_List = wholesaler.position_Grossist_Don_Parent_Grossists_List,
        couleur = wholesaler.couleur,
        currentCreditBalance = wholesaler.currentCreditBalance,
        date = System.currentTimeMillis().toString()
    ).apply {
        // Add at least one color with minimum quantity if available
        product.colours_Et_Gouts.firstOrNull()?.let { firstColor ->
            colours_Et_Gouts_Commende.add(
                UiState.Produit_DataBase.Grossist_Choisi_Pour_Acheter_Ce_Produit_In_This_Transaction.Colours_Et_Gouts_Commende_Au_Supplier(
                    position_Du_Couleur_Au_Produit = firstColor.position_Du_Couleur_Au_Produit,
                    id_Don_Tout_Couleurs = firstColor.position_Du_Couleur_Au_Produit,
                    nom = firstColor.nom,
                    quantity_Achete = 1,
                    imogi = firstColor.imogi
                )
            )
        }
    }
}
