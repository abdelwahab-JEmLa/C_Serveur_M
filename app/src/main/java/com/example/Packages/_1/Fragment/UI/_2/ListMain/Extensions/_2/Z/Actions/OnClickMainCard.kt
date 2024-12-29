package com.example.Packages._1.Fragment.UI._2.ListMain.Extensions._2.Z.Actions

import com.example.Apps_Head._1.Model.AppInitializeModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

fun OnClickMainCard(
    coroutineScope: CoroutineScope,
    app_Initialize_Model: AppInitializeModel,
    produit: AppInitializeModel.ProduitModel
) {
    coroutineScope.launch {
        // Find the maximum position across all products
        val maxPosition = app_Initialize_Model.produits_Main_DataBase
            .mapNotNull { it.bonCommendDeCetteCota }
            .maxOfOrNull { it.position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit }
            ?: 0

        // Create new position as max + 1
        val newPosition = maxPosition + 1

        // Update the current product's position
        produit.bonCommendDeCetteCota?.position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit =
            newPosition

        // Update Firebase
        app_Initialize_Model.update_Produits_FireBase()
    }
}
