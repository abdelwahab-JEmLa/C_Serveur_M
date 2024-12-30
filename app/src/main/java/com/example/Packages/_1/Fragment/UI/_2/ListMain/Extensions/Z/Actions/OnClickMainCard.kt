package com.example.Packages._1.Fragment.UI._2.ListMain.Extensions.Z.Actions

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.Apps_Head._1.Model.AppsHeadModel
import com.example.Apps_Head._2.ViewModel.InitViewModel
import kotlinx.coroutines.launch

fun InitViewModel.OnClickMainCard(
    produit: AppsHeadModel.ProduitModel
) {
    viewModelScope.launch {
        try {
            Log.d("OnClickMainCard", "Starting position update for product ${produit.id}")

            // Find the maximum position among existing products
            val maxPosition = _appsHead.produits_Main_DataBase
                .mapNotNull { it.bonCommendDeCetteCota?.position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit }
                .filter { it >= 0 }
                .maxOrNull() ?: -1

            Log.d("OnClickMainCard", "Current max position: $maxPosition")

            // Calculate new position
            val newPosition = maxPosition + 1

            // Create or update bonCommendDeCetteCota if necessary
            if (produit.bonCommendDeCetteCota == null) {
                produit.bonCommendDeCetteCota = AppsHeadModel.ProduitModel.GrossistBonCommandes()
            }

            produit.bonCommendDeCetteCota?.position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit = newPosition

            updateProduitsParProduit(produit)


        } catch (e: Exception) {
            Log.e("OnClickMainCard", "Error updating position", e)
        }
    }
}
fun InitViewModel.updateProduitsParProduit(
    produit: AppsHeadModel.ProduitModel
) {
    viewModelScope.launch {
        try {
            Log.d("OnClickMainCard", "Starting position update for product ${produit.id}")

            // Find and update the product in the database
            val productIndex = _appsHead.produits_Main_DataBase.indexOfFirst { it.id == produit.id }
            if (productIndex != -1) {
                _appsHead.produits_Main_DataBase[productIndex] = produit
            } else {
                Log.e("OnClickMainCard", "Product not found in database: ${produit.id}")
                return@launch
            }

            // Update Firebase
            try {
                _appsHead.update_Produits_FireBase()
                Log.d("OnClickMainCard", "Successfully updated Firebase")
            } catch (e: Exception) {
                Log.e("OnClickMainCard", "Failed to update Firebase", e)
                // Consider implementing a retry mechanism or error handling here
            }

        } catch (e: Exception) {
            Log.e("OnClickMainCard", "Error updating position", e)
        }
    }
}
