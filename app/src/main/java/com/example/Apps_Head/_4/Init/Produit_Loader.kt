package com.example.Apps_Head._4.Init

import android.util.Log
import com.example.Apps_Head._1.Model.AppsHeadModel
import com.example.Apps_Head._2.ViewModel.InitViewModel
import com.google.firebase.Firebase
import com.google.firebase.database.database
import kotlinx.coroutines.tasks.await

suspend fun InitViewModel.load_Depuit_FireBase() {
    val TAG = "Produit_Loader"
    val CHEMIN_BASE = "0_UiState_3_Host_Package_3_Prototype11Dec/produit_DataBase"
    val NOMBRE_PRODUITS = 20
    val baseRef = Firebase.database.getReference(CHEMIN_BASE)

    try {
        // Initial data fetch to check existing products
        val existingData = baseRef.get().await()
        val existingProducts = existingData.children.mapNotNull { snapshot ->
            try {
                snapshot.getValue(AppsHeadModel.ProduitModel::class.java)
            } catch (e: Exception) {
                Log.w(TAG, "Failed to parse product from snapshot: ${snapshot.key}", e)
                null
            }
        }

        // Clear and prepare the products list
        _appsHead.produits_Main_DataBase.clear()

        // Initialize products list with existing or new products
        repeat(NOMBRE_PRODUITS) { index ->
            val existingProduct = existingProducts.find { it.id == index.toLong() }
            val product = existingProduct ?: AppsHeadModel.ProduitModel(id = index.toLong())

            try {
                val refProduit = baseRef.child(index.toString())

                // Load and validate product name
                val nomSnapshot = refProduit.child("nom").get().await()
                if (nomSnapshot.exists()) {
                    product.nom = nomSnapshot.value?.toString() ?: "Produit $index"
                } else if (product.nom.isEmpty() && index > 0) {
                    product.nom = "Produit $index"
                }

                // Load colors with validation
                product.coloursEtGouts.clear()
                refProduit.child("coloursEtGouts").get().await().children.forEach { colorSnapshot ->
                    try {
                        colorSnapshot.getValue(AppsHeadModel.ProduitModel.ColourEtGout_Model::class.java)?.let { color ->
                            if (color.nom.isNotEmpty()) {
                                product.coloursEtGouts.add(color)
                            }
                        }
                    } catch (e: Exception) {
                        Log.w(TAG, "Failed to parse color for product $index", e)
                    }
                }

                // Load current order with validation
                try {
                    val bonCommandeSnapshot = refProduit.child("bonCommendDeCetteCota").get().await()
                    if (bonCommandeSnapshot.exists()) {
                        bonCommandeSnapshot.getValue(AppsHeadModel.ProduitModel.GrossistBonCommandes::class.java)?.let { bonCommande ->
                            if (bonCommande.grossistInformations != null) {
                                product.bonCommendDeCetteCota = bonCommande
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.w(TAG, "Failed to load current order for product $index", e)
                }

                // Load current sales with validation
                product.bonsVentDeCetteCota.clear()
                refProduit.child("bonsVentDeCetteCota").get().await().children.forEach { saleSnapshot ->
                    try {
                        saleSnapshot.getValue(AppsHeadModel.ProduitModel.ClientBonVent_Model::class.java)?.let { sale ->
                            if (sale.nom_Acheteur.isNotEmpty() && sale.colours_Achete.isNotEmpty()) {
                                product.bonsVentDeCetteCota.add(sale)
                            }
                        }
                    } catch (e: Exception) {
                        Log.w(TAG, "Failed to parse current sale for product $index", e)
                    }
                }

                // Load sales history with validation
                product.historiqueBonsVents.clear()
                refProduit.child("historiqueBonsVents").get().await().children.forEach { historySnapshot ->
                    try {
                        historySnapshot.getValue(AppsHeadModel.ProduitModel.ClientBonVent_Model::class.java)?.let { history ->
                            if (history.nom_Acheteur.isNotEmpty() && history.colours_Achete.isNotEmpty()) {
                                product.historiqueBonsVents.add(history)
                            }
                        }
                    } catch (e: Exception) {
                        Log.w(TAG, "Failed to parse sale history for product $index", e)
                    }
                }

                // Load order history with validation
                product.historiqueBonsCommend.clear()
                refProduit.child("historiqueBonsCommend").get().await().children.forEach { orderSnapshot ->
                    try {
                        orderSnapshot.getValue(AppsHeadModel.ProduitModel.GrossistBonCommandes::class.java)?.let { order ->
                            if (order.grossistInformations != null) {
                                product.historiqueBonsCommend.add(order)
                            }
                        }
                    } catch (e: Exception) {
                        Log.w(TAG, "Failed to parse order history for product $index", e)
                    }
                }

                // Set update flags based on data completeness
                product.besoin_To_Be_Updated = product.nom.isEmpty() ||
                        product.coloursEtGouts.isEmpty() ||
                        (index > 0 && product.historiqueBonsVents.isEmpty() && product.bonsVentDeCetteCota.isEmpty())

                product.it_Image_besoin_To_Be_Updated = product.besoin_To_Be_Updated

            } catch (e: Exception) {
                Log.e(TAG, "Error loading product $index", e)
                product.apply {
                    nom = if (index.toLong() == 0L) "" else "Produit $index (Erreur)"
                    coloursEtGouts.clear()
                    besoin_To_Be_Updated = true
                    it_Image_besoin_To_Be_Updated = true
                }
            }

            _appsHead.produits_Main_DataBase.add(product)
        }

    } catch (e: Exception) {
        Log.e(TAG, "Critical error during loading", e)
        throw e
    }
}
