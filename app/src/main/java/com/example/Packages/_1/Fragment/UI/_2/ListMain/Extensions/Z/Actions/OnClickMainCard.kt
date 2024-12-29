package com.example.Packages._1.Fragment.UI._2.ListMain.Extensions.Z.Actions

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.Apps_Head._1.Model.AppInitializeModel
import com.example.Apps_Head._2.ViewModel.AppInitialize_ViewModel
import kotlinx.coroutines.launch


// OnClickMainCard.kt
fun AppInitialize_ViewModel.OnClickMainCard(
    produit: AppInitializeModel.ProduitModel
) {
    viewModelScope.launch {
        try {
            Log.d("OnClickMainCard", "Starting position update for product ${produit.id}")

            // Find the maximum position among existing products
            val maxPosition = _app_Initialize_Model.produits_Main_DataBase
                .mapNotNull { it.bonCommendDeCetteCota?.position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit }
                .filter { it > 0 }
                .maxOrNull() ?: 0

            Log.d("OnClickMainCard", "Current max position: $maxPosition")

            // Calculate new position
            val newPosition = maxPosition + 1

            // Update the position for the matching product
            _app_Initialize_Model.produits_Main_DataBase.find { it.id == produit.id }?.let { matchingProduct ->
                matchingProduct.bonCommendDeCetteCota?.position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit = newPosition
                Log.d("OnClickMainCard", "Updated position to $newPosition")
            }

            // Update Firebase using the suspend function
            _app_Initialize_Model.update_Produits_FireBase()
            Log.d("OnClickMainCard", "Successfully updated Firebase")

        } catch (e: Exception) {
            Log.e("OnClickMainCard", "Error updating position", e)
        }
    }
}
