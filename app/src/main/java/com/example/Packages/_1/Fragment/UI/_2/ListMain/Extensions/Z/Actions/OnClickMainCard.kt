package com.example.Packages._1.Fragment.UI._2.ListMain.Extensions.Z.Actions

import android.util.Log
import com.example.Apps_Head._1.Model.AppInitializeModel
import com.example.Apps_Head._2.ViewModel.AppInitialize_ViewModel

private const val TAG = "OnClickMainCard"

fun AppInitialize_ViewModel.OnClickMainCard(
    produit: AppInitializeModel.ProduitModel
) {
    try {
        Log.d(TAG, "Starting OnClickMainCard for product ${produit.id} (${produit.nom})")

        // Find the maximum position among existing products
        val maxPosition = _app_Initialize_Model.produits_Main_DataBase
            .mapNotNull { it.bonCommendDeCetteCota?.position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit }
            .filter { it > 0 }
            .maxOrNull() ?: 0

        Log.d(TAG, "Current maximum position: $maxPosition")

        // Calculate new position
        val newPosition = maxPosition + 1
        Log.d(TAG, "Calculated new position: $newPosition")

        // Update the position for the matching product
        _app_Initialize_Model.produits_Main_DataBase.find { it.id == produit.id }?.let { matchingProduct ->
            val oldPosition = matchingProduct.bonCommendDeCetteCota?.position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit
            matchingProduct.bonCommendDeCetteCota?.position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit = newPosition

            Log.d(TAG, "Updated position for product ${matchingProduct.id}: $oldPosition -> $newPosition")
        } ?: Log.e(TAG, "Product ${produit.id} not found in database")

        // Save the updated database
        Log.d(TAG, "Saving updated positions to Firebase")
        _app_Initialize_Model.ref_Produits_Main_DataBase
            .setValue(_app_Initialize_Model.produits_Main_DataBase)
            .addOnSuccessListener {
                Log.d(TAG, "Successfully saved position updates to Firebase")
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Failed to save position updates to Firebase", e)
            }

    } catch (e: Exception) {
        Log.e(TAG, "Error in OnClickMainCard", e)
    }
}
