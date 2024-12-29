package com.example.Packages._1.Fragment.UI._2.ListMain.Extensions._2.Z.Actions

import android.util.Log
import com.example.Apps_Head._1.Model.AppInitializeModel

fun OnClickMainCard(
    app_Initialize_Model: AppInitializeModel,
    produit: AppInitializeModel.ProduitModel
) {
        try {
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

            // Use setValue() with completion listener instead of direct await
            app_Initialize_Model.ref_Produits_Main_DataBase
                .setValue(app_Initialize_Model.produits_Main_DataBase)
                .addOnSuccessListener {
                    // Handle success
                }
                .addOnFailureListener { exception ->
                    // Handle failure
                    Log.e("Firebase", "Error updating data", exception)
                }

        } catch (e: Exception) {
            Log.e("Firebase", "Error in OnClickMainCard", e)
            // Handle the error appropriately
        }
}
