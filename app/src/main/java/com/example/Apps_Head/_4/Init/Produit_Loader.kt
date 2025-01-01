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
    val NOMBRE_PRODUITS = 300
    val DEBUG_LIMIT = 7
    val baseRef = Firebase.database.getReference(CHEMIN_BASE)

    try {
        val existingData = baseRef.get().await()

        repeat(NOMBRE_PRODUITS) { index ->
            try {
                val productSnapshot = existingData.child(index.toString())
                val product = AppsHeadModel.ProduitModel(id = index.toLong())
                val shouldLog = index < DEBUG_LIMIT

                // Load coloursEtGouts
                if (shouldLog) Log.d(TAG, "Product $index - Loading coloursEtGouts")
                product.coloursEtGouts.clear()
                productSnapshot.child("coloursEtGouts").children.forEach { colorSnapshot ->
                    try {
                        colorSnapshot.getValue(AppsHeadModel.ProduitModel.ColourEtGout_Model::class.java)?.let { color ->
                            if (color.nom.isNotEmpty()) {
                                product.coloursEtGouts.add(color)
                                if (shouldLog) Log.d(TAG, "Product $index - Added color: ${color.nom}")
                            }
                        }
                    } catch (e: Exception) {
                        if (shouldLog) Log.w(TAG, "Failed to parse color for product $index", e)
                    }
                }

                // Load bonsVentDeCetteCota
                if (shouldLog) Log.d(TAG, "Product $index - Loading bonsVentDeCetteCota")
                product.bonsVentDeCetteCota.clear()
                productSnapshot.child("bonsVentDeCetteCota").children.forEach { saleSnapshot ->
                    try {
                        saleSnapshot.getValue(AppsHeadModel.ProduitModel.ClientBonVent_Model::class.java)?.let { sale ->
                            if (sale.nom_Acheteur.isNotEmpty() && sale.colours_Achete.isNotEmpty()) {
                                product.bonsVentDeCetteCota.add(sale)
                                if (shouldLog) Log.d(TAG, "Product $index - Added current sale for: ${sale.nom_Acheteur}")
                            }
                        }
                    } catch (e: Exception) {
                        if (shouldLog) Log.w(TAG, "Failed to parse current sale for product $index", e)
                    }
                }

                // Load historiqueBonsVents
                if (shouldLog) Log.d(TAG, "Product $index - Loading historiqueBonsVents")
                product.historiqueBonsVents.clear()
                productSnapshot.child("historiqueBonsVents").children.forEach { historySnapshot ->
                    try {
                        historySnapshot.getValue(AppsHeadModel.ProduitModel.ClientBonVent_Model::class.java)?.let { history ->
                            if (history.nom_Acheteur.isNotEmpty() && history.colours_Achete.isNotEmpty()) {
                                product.historiqueBonsVents.add(history)
                                if (shouldLog) Log.d(TAG, "Product $index - Added sale history for: ${history.nom_Acheteur}")
                            }
                        }
                    } catch (e: Exception) {
                        if (shouldLog) Log.w(TAG, "Failed to parse sale history for product $index", e)
                    }
                }

                // Load historiqueBonsCommend
                if (shouldLog) Log.d(TAG, "Product $index - Loading historiqueBonsCommend")
                product.historiqueBonsCommend.clear()
                productSnapshot.child("historiqueBonsCommend").children.forEach { orderSnapshot ->
                    try {
                        orderSnapshot.getValue(AppsHeadModel.ProduitModel.GrossistBonCommandes::class.java)?.let { order ->
                            if (order.grossistInformations != null) {
                                product.historiqueBonsCommend.add(order)
                                if (shouldLog) Log.d(TAG, "Product $index - Added order history for grossist: ${order.grossistInformations?.nom}")
                            }
                        }
                    } catch (e: Exception) {
                        if (shouldLog) Log.w(TAG, "Failed to parse order history for product $index", e)
                    }
                }

                // Load bonCommendDeCetteCota (existing code)
                if (shouldLog) Log.d(TAG, "Product $index - Before loading bon commande:")
                if (shouldLog) Log.d(TAG, "- Current bonCommendDeCetteCota: ${product.bonCommendDeCetteCota}")

                try {
                    val bonCommandeSnapshot = productSnapshot.child("bonCommendDeCetteCota")
                    if (bonCommandeSnapshot.exists()) {
                        if (shouldLog) Log.d(TAG, "Product $index - Bon commande snapshot exists")

                        bonCommandeSnapshot.getValue(AppsHeadModel.ProduitModel.GrossistBonCommandes::class.java)?.let { bonCommande ->
                            if (shouldLog) Log.d(TAG, "Product $index - Parsed bon commande: $bonCommande")

                            if (bonCommande.grossistInformations != null) {
                                if (shouldLog) Log.d(TAG, "Product $index - Grossist info exists")
                                product.bonCommendDeCetteCota = bonCommande

                                if (shouldLog) {
                                    Log.d(TAG, "Product $index - After update:")
                                    Log.d(TAG, "- Updated bonCommendDeCetteCota: ${product.bonCommendDeCetteCota}")
                                    Log.d(TAG, "- Grossist info: ${product.bonCommendDeCetteCota?.grossistInformations}")
                                    Log.d(TAG, "- Colors ordered: ${product.bonCommendDeCetteCota?.coloursEtGoutsCommendee}")
                                }
                            }
                        }
                    } else {
                        if (shouldLog) Log.d(TAG, "Product $index - No bon commande snapshot found")
                    }
                } catch (e: Exception) {
                    if (shouldLog) Log.e(TAG, "Product $index - Error loading bon commande", e)
                }

                _appsHead.produits_Main_DataBase.add(product)

            } catch (e: Exception) {
                if (index < DEBUG_LIMIT) Log.e(TAG, "Error loading product $index", e)
            }
        }

    } catch (e: Exception) {
        Log.e(TAG, "Critical error during loading", e)
        throw e
    }
}
