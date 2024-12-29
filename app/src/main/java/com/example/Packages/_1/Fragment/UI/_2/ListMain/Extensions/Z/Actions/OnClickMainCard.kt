package com.example.Packages._1.Fragment.UI._2.ListMain.Extensions.Z.Actions

import com.example.Apps_Head._1.Model.AppInitializeModel
import com.example.Apps_Head._2.ViewModel.AppInitialize_ViewModel

fun AppInitialize_ViewModel.OnClickMainCard(
    produit: AppInitializeModel.ProduitModel
) {
    try {
        // Find the maximum position among existing products
        val maxPosition = _app_Initialize_Model.produits_Main_DataBase
            .mapNotNull { it.bonCommendDeCetteCota?.position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit }
            .filter { it > 0 }
            .maxOrNull() ?: 0

        // Calculate new position
        val newPosition = maxPosition + 1

        // Update the position for the matching product
        _app_Initialize_Model.produits_Main_DataBase.find { it.id == produit.id }?.let { matchingProduct ->
            matchingProduct.bonCommendDeCetteCota?.position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit = newPosition
        }

        // Save the updated database
        _app_Initialize_Model.ref_Produits_Main_DataBase
            .setValue(_app_Initialize_Model.produits_Main_DataBase)

    } catch (e: Exception) {
        // Handle error silently or implement your preferred error handling
    }
}
