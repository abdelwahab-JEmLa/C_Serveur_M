package com.example.Packages._1.Fragment.UI._2.ListMain.Extensions.Z.Actions

import com.example.Apps_Head._1.Model.AppInitializeModel
import com.example.Apps_Head._2.ViewModel.AppInitialize_ViewModel

fun AppInitialize_ViewModel.OnClickMainCard(
    produit: AppInitializeModel.ProduitModel
) {
    try {
        val maxPosition = _app_Initialize_Model.produits_Main_DataBase
            .mapNotNull { it.bonCommendDeCetteCota?.position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit }
            .filter { it > 0 }
            .maxOrNull() ?: 0

        val newPosition = maxPosition + 1
        _app_Initialize_Model.produits_Main_DataBase.find { it.id== produit.id}
            ?.bonCommendDeCetteCota
            ?.position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit = newPosition

        _app_Initialize_Model.ref_Produits_Main_DataBase
            .setValue(_app_Initialize_Model.produits_Main_DataBase)

    } catch (e: Exception) {
        // Handle error silently or implement your preferred error handling
    }
}
