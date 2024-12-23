package com.example.Packages._3.Fragment.ViewModel._2.Init.Main.Components

import android.util.Log
import com.example.App_Produits_Main._1.Model.App_Initialize_Model
import com.example.App_Produits_Main._1.Model.Components.Produits_Ancien_DataBase_Main
import com.example.App_Produits_Main._2.ViewModel.Apps_Produits_Main_DataBase_ViewModel
import com.example.App_Produits_Main._2.ViewModel.Init.Ancien_Resources_DataBase_Main
import com.example.App_Produits_Main._2.ViewModel.Init.get_Ancien_DataBases_Main
import com.example.App_Produits_Main._2.ViewModel.Init.init_load_Depuit_FireBase

private const val TAG_Snap = "InitialeUiState"

internal suspend fun Apps_Produits_Main_DataBase_ViewModel.Initialise_ViewModel_Main() {
    try {
        Log.d(TAG_Snap, "Starting Initialise_ViewModel")
        initializationProgress = 0.1f
        isInitializing = true

        val ancienData = get_Ancien_DataBases_Main()
        val load_Depuit_FireBase = true
        val cree_Randoms = true

        if (load_Depuit_FireBase) {
           init_load_Depuit_FireBase()
        } else {
            // Initialize products database
            ancienData.produitsDatabase.forEach { ancien ->
                val produit = App_Initialize_Model.Produit_Model(
                    id = ancien.idArticle,
                    it_ref_Id_don_FireBase = 1L,
                    it_ref_don_FireBase = "produit_DataBase",
                )
                _app_Initialize_Model.produits_Main_DataBase.add(produit)
            }
            initializationProgress = 0.3f

            // "0_UiState_3_Host_Package_3_Prototype11Dec"
            _app_Initialize_Model.produits_Main_DataBase
                .forEach { new_produit_A_Update ->
                    try {
                        // Find and update from ancient database
                        ancienData.produitsDatabase.find { it.idArticle == new_produit_A_Update.id }
                            ?.let { ancien_DataBase ->
                                new_produit_A_Update.nom = ancien_DataBase.nomArticleFinale
                                if (cree_Randoms) {
                                    new_produit_A_Update.mutable_App_Produit_Statues.dernier_Vent_date_time_String =
                                        process_Random_Model_Main()
                                }
                                // Process colors
                                processColors_Main(
                                    ancien_DataBase,
                                    ancienData,
                                    new_produit_A_Update
                                )
                            }

                        // Process sales data
                        processSalesData_Main(ancienData, new_produit_A_Update)
                        if (cree_Randoms) {
                            // Process wholesaler data
                            process_Random_WholesalerData_Main(new_produit_A_Update)
                        }

                        new_produit_A_Update.besoin_To_Be_Updated = false

                    } catch (e: Exception) {
                        Log.e(TAG_Snap, "Error processing product ${new_produit_A_Update.id}", e)
                    }
                }

            _app_Initialize_Model.update_Produits_FireBase()
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

private fun process_Random_Model_Main(): String {
    val dateFormat = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault())
    val calendar = java.util.Calendar.getInstance()
    calendar.set(java.util.Calendar.HOUR_OF_DAY, 12)
    calendar.set(java.util.Calendar.MINUTE, 5)
    calendar.set(java.util.Calendar.SECOND, 0)
    return dateFormat.format(calendar.time)
}
private fun processColors_Main(
    ancien_Produits_DataBase: Produits_Ancien_DataBase_Main,
    ancien_Data_References: Ancien_Resources_DataBase_Main,
    new_produit_A_Update: App_Initialize_Model.Produit_Model
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
                App_Initialize_Model.Produit_Model.Colours_Et_Gouts(
                    position_Du_Couleur_Au_Produit = position,
                    nom = color.nameColore,
                    imogi = color.iconColore
                )
            )
        }
    }
}

private fun processSalesData_Main(
    ancien_Data_References: Ancien_Resources_DataBase_Main,
    new_produit_A_Update: App_Initialize_Model.Produit_Model
) {
    val salesByClientAndArticle = ancien_Data_References.soldArticles
        .groupBy { it.clientSoldToItId }
        .mapValues { (_, sales) ->
            sales.groupBy { it.idArticle }
        }

    salesByClientAndArticle.forEach { (clientId, articleSales) ->
        articleSales[new_produit_A_Update.id]?.forEachIndexed { index, ancien_soldArticles ->
            ancien_Data_References.clients_List.find { it.idClientsSu == clientId }
                ?.let { client_Data ->

                    val newAchate =
                        App_Initialize_Model.Produit_Model.Client_Bon_Vent_Model(
                            vid = (index + 1).toLong(),
                            id_Acheteur = clientId,
                            nom_Acheteur = client_Data.nomClientsSu,
                            init_colours_achete = emptyList()
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
                                newAchate.colours_achete.add(
                                    App_Initialize_Model.Produit_Model.Client_Bon_Vent_Model.Color_Achat_Model(
                                        vidPosition = position,
                                        nom = color.nom,
                                        quantity_Achete = quantity,
                                        imogi = color.imogi
                                    )
                                )
                            }
                        }
                    }

                    if (newAchate.colours_achete.isNotEmpty()) {
                        new_produit_A_Update.historique_Vents.add(newAchate)
                    }
                }
        }
    }
}

private fun process_Random_WholesalerData_Main(new_produit_A_Update: App_Initialize_Model.Produit_Model) {
    val sampleWholesalers = listOf(
        createWholesaler_Main(1L, "Wholesaler Alpha", "#FF5733", 1000.0),
        createWholesaler_Main(2L, "Wholesaler Beta", "#33FF57", 1500.0),
        createWholesaler_Main(3L, "Wholesaler Gamma", "#5733FF", 2000.0)
    )

    new_produit_A_Update.historique_Commends.clear()

    // Add random wholesaler with minimum order
    val selectedWholesaler = sampleWholesalers.random()
    val wholesalerOrder = createWholesalerOrder_Main(selectedWholesaler, new_produit_A_Update)
    new_produit_A_Update.mutable_App_Produit_Statues =
        App_Initialize_Model.Produit_Model.Mutable_App_Produit_Statues(
            init_Son_Grossist_Pour_Acheter_Ce_Produit_In_This_Transaction = wholesalerOrder
        )
    new_produit_A_Update.bon_Commend_De_Cette_Cota= wholesalerOrder
    new_produit_A_Update.historique_Commends.add(wholesalerOrder)
}

private fun createWholesaler_Main(
    id: Long,
    name: String,
    color: String,
    balance: Double
): App_Initialize_Model.Produit_Model.Grossist_Bon_Commend_Model {
    return App_Initialize_Model.Produit_Model.Grossist_Bon_Commend_Model(
        vid = id,
        supplier_id = id,
        nom = name,
        init_position_Grossist_Don_Parent_Grossists_List = id.toInt() - 1,
        couleur = color,
        currentCreditBalance = balance
    )
}

private fun createWholesalerOrder_Main(
    wholesaler: App_Initialize_Model.Produit_Model.Grossist_Bon_Commend_Model,
    product: App_Initialize_Model.Produit_Model
): App_Initialize_Model.Produit_Model.Grossist_Bon_Commend_Model {
    return App_Initialize_Model.Produit_Model.Grossist_Bon_Commend_Model(
        vid = wholesaler.vid,
        supplier_id = wholesaler.supplier_id,
        nom = wholesaler.nom,
        init_position_Grossist_Don_Parent_Grossists_List = wholesaler.position_Grossist_Don_Parent_Grossists_List,
        couleur = wholesaler.couleur,
        currentCreditBalance = wholesaler.currentCreditBalance,
        date = System.currentTimeMillis().toString()
    ).apply {
        // Add at least one color with minimum quantity if available
        product.colours_Et_Gouts.firstOrNull()?.let { firstColor ->
            colours_Et_Gouts_Commende.add(
                App_Initialize_Model.Produit_Model.Grossist_Bon_Commend_Model.Colours_Et_Gouts_Commende_Au_Supplier(
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
