package com.example.Packages._1.Fragment.UI._2.ListMain.Extensions.Z.Actions

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.Apps_Head._1.Model.AppsHeadModel
import com.example.Apps_Head._2.ViewModel.InitViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

fun InitViewModel.updateProduitsParIndex(
    item: AppsHeadModel.ProduitModel
) {
    viewModelScope.launch {
        viewModelScope.launch {
            try {
                // Find the index of the product to update
                val productIndex =
                    _appsHead.produits_Main_DataBase.indexOfFirst { it.id == item.id }

                if (productIndex != -1) {
                    // Update the product in the list
                    _appsHead.produits_Main_DataBase[productIndex] = item

                    // Update Firebase safely
                    try {
                        withContext(Dispatchers.IO) {
                            _appsHead.ref_Produits_Main_DataBase
                                .setValue(_appsHead.produits_Main_DataBase)
                                .await() // Wait for the operation to complete
                            Log.d(TAG, "Successfully updated product with ID: ${item.id}")
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Failed to update Firebase", e)
                        throw e // Rethrow to be caught by outer try-catch
                    }
                } else {
                    Log.w(TAG, "Product with ID ${item.id} not found in database")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error updating product", e)
            }
        }
    }
}
