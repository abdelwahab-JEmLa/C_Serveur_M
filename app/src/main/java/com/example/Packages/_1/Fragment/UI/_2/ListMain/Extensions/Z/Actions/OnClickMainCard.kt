package com.example.Packages._1.Fragment.UI._2.ListMain.Extensions.Z.Actions

import com.example.Apps_Head._1.Model.AppInitializeModel

fun OnClickMainCard(
    app_Initialize_Model: AppInitializeModel,
    produit: AppInitializeModel.ProduitModel
) {
    try {
        val maxPosition = app_Initialize_Model.produits_Main_DataBase
            .mapNotNull { it.bonCommendDeCetteCota?.position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit }
            .filter { it > 0 }
            .maxOrNull() ?: 0

        val newPosition = maxPosition + 1
        produit.bonCommendDeCetteCota?.position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit = newPosition

        app_Initialize_Model.ref_Produits_Main_DataBase
            .setValue(app_Initialize_Model.produits_Main_DataBase)

    } catch (e: Exception) {
        // Handle error silently or implement your preferred error handling
    }
}
