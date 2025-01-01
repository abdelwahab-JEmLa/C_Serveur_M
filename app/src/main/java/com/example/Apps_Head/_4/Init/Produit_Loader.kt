package com.example.Apps_Head._4.Init

import android.util.Log
import com.example.Apps_Head._1.Model.AppsHeadModel
import com.example.Apps_Head._2.ViewModel.InitViewModel
import com.google.firebase.Firebase
import com.google.firebase.database.database
import kotlinx.coroutines.tasks.await

private const val TAG = "ProduitModel"

suspend fun InitViewModel.load_Depuit_FireBase() {
    val CHEMIN_BASE = "0_UiState_3_Host_Package_3_Prototype11Dec/produit_DataBase"
    val NOMBRE_PRODUITS = 300
    val DEBUG_LIMIT = 7
    val baseRef = Firebase.database.getReference(CHEMIN_BASE)


    try {
        _appsHead.produits_Main_DataBase.clear()

        // First, check if the base reference exists
        val baseSnapshot = baseRef.get().await()
        if (!baseSnapshot.exists()) {
            Log.e(TAG, "❌ Base reference doesn't exist at path: $CHEMIN_BASE")
            return
        }

        Log.d(TAG, "📁 Base reference exists, contains ${baseSnapshot.childrenCount} items")

        repeat(NOMBRE_PRODUITS) { index ->
            try {
                val productId = index+1
                val startTime = System.currentTimeMillis()

                val productSnapshot = baseRef.child(productId.toString()).get().await()

                if (!productSnapshot.exists()) {
                    Log.d(TAG, "⚠️ No data found for product $index")
                    return@repeat
                }

                val product = AppsHeadModel.ProduitModel.fromSnapshot(productSnapshot)

                if (product != null) {
                    val loadTime = System.currentTimeMillis() - startTime

                    if (index < DEBUG_LIMIT) {
                        Log.d(TAG, """
                            ✅ Successfully loaded product $index:
                            - Name: ${product.nom}
                            - ID: ${product.id}
                            - Colors count: ${product.coloursEtGouts.size}
                            - Load time: ${loadTime}ms
                        """.trimIndent())
                    }

                    _appsHead.produits_Main_DataBase.add(product)

                    if (index < DEBUG_LIMIT) {
                        Log.d(TAG, "📝 Current database size: ${_appsHead.produits_Main_DataBase.size}")
                    }
                } else {
                    Log.e(TAG, "❌ Failed to parse product $index from snapshot: ${productSnapshot.value}")
                }

            } catch (e: Exception) {
                Log.e(TAG, """
                    ❌ Error loading product $index
                    - Error type: ${e.javaClass.simpleName}
                    - Message: ${e.message}
                    - Stack trace: ${e.stackTraceToString()}
                """.trimIndent())
            }
        }

        // Log final state
        Log.d(TAG, """
            🏁 Loading complete
            - Total products loaded: ${_appsHead.produits_Main_DataBase.size}
            - Expected products: $NOMBRE_PRODUITS
            - Success rate: ${(_appsHead.produits_Main_DataBase.size.toFloat() / NOMBRE_PRODUITS * 100).toInt()}%
        """.trimIndent())

    } catch (e: Exception) {
        Log.e(TAG, """
            💥 Critical error during loading
            - Error type: ${e.javaClass.simpleName}
            - Message: ${e.message}
            - Stack trace: ${e.stackTraceToString()}
        """.trimIndent())
        throw e
    }
}
