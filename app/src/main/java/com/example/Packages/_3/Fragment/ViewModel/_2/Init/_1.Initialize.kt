package com.example.Packages._3.Fragment.ViewModel._2.Init

import android.util.Log
import com.example.Packages._3.Fragment.Models.Ui_Mutable_State
import com.example.Packages._3.Fragment.ViewModel.P3_ViewModel
import com.google.firebase.Firebase
import com.google.firebase.database.database
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await
import kotlin.random.Random

internal const val TAG_Snap = "InitialeUiState"

internal suspend fun P3_ViewModel._1Initialize(
    onProgressUpdate: (Float) -> Unit = {}
) {
    try {
        Log.d(TAG_Snap, "Starting _1Initialize")
        onProgressUpdate(0.1f)

        // Load existing state from Firebase
        try {
            _uiState.loadFromFirebaseDataBase()
            Log.d(TAG_Snap, "Successfully loaded existing state from Firebase")
        } catch (e: Exception) {
            Log.w(TAG_Snap, "No existing state found or error loading, starting fresh", e)
        }

        onProgressUpdate(0.3f)

        // Create random products
        val randomProducts = List(200) {
            UiState.ReferencesFireBaseGroup.Produit_Update_Ref(
                id = Random.nextInt(500, 700).toLong(),
                initialTriggerTime = System.currentTimeMillis()
            )
        }

        onProgressUpdate(0.5f)

        // Create or update the default group
        val defaultGroup = UiState.ReferencesFireBaseGroup(
            id = 1L,
            position = 1,
            nom = "Produits_Commend_DataBase",
            initialProduits_Update_Ref = randomProducts
        )

        onProgressUpdate(0.7f)

        // Update or add the group using the new utility functions
        val existingGroup = _uiState.getReferenceFireBaseById(1L)
        if (existingGroup != null) {
            // Clear existing products and add new ones
            existingGroup.produit_Update_Ref.clear()
            randomProducts.forEach { product ->
                existingGroup.addProduct(product)
            }
        } else {
            _uiState.addReferencesSnap(defaultGroup)
        }

        val productsData = processes_Organiseur(
            referenceFireBas=_uiState.getReferenceFireBaseById(1),
            onProgressUpdate = { progress ->
                onProgressUpdate(0.2f + (progress * 0.6f))
            }
        )

        onProgressUpdate(0.9f)

        // Update the entire state in Firebase
        _uiState.updateUiStateSelfInFirebaseDataBase()

        onProgressUpdate(1.0f)
        Log.d(TAG_Snap, "Completed _1Initialize")
    } catch (e: Exception) {
        Log.e(TAG_Snap, "Error in _1Initialize", e)
        throw e
    }
}
internal suspend fun processes_Organiseur(
    referenceFireBas: UiState.ReferencesFireBaseGroup?,
    onProgressUpdate: (Float) -> Unit,
): List<Ui_Mutable_State.Produits_Commend_DataBase> = coroutineScope {
    try {
        onProgressUpdate(0.1f)

        val (productsSnapshot, soldArticlesSnapshot) = coroutineScope {
            val productsDeferred = async {
                Firebase.database.getReference("e_DBJetPackExport")
                    .get()
                    .await()
            }
            val soldArticlesDeferred = async {
                Firebase.database.getReference("ArticlesAcheteModele")
                    .get()
                    .await()
            }
            Pair(productsDeferred.await(), soldArticlesDeferred.await())
        }

        Log.d(TAG_Snap, "Retrieved data - Products: ${productsSnapshot.childrenCount}, Sold Articles: ${soldArticlesSnapshot.childrenCount}")
        onProgressUpdate(0.3f)

        val productsToProcess =
            if (referenceFireBas?.updateAllTrigger == true) {
                productsSnapshot.children.toList()
            } else {
                val productIds = referenceFireBas?.produit_Update_Ref?.map { it.id } ?: emptyList()
                productsSnapshot.children.filter { productSnapshot ->
                    val idArticle = productSnapshot.child("idArticle").getValue(Long::class.java)
                    productIds.contains(idArticle)
                }
            }

        val totalProducts = productsToProcess.size
        Log.d(TAG_Snap, "Processing $totalProducts products")
        var processedProducts = 0

        val processedProductsList = productsToProcess.mapNotNull { productSnapshot ->
            try {
                val idArticle = productSnapshot.child("idArticle").getValue(Long::class.java)
                Log.d(TAG_Snap, "Processing product ID: $idArticle")

                process_Cree_Product(productSnapshot, soldArticlesSnapshot).also {
                    processedProducts++
                    Log.d(
                        TAG_Snap,
                        "Completed processing product $idArticle ($processedProducts/$totalProducts)"
                    )
                    onProgressUpdate(0.3f + (processedProducts.toFloat() / totalProducts * 0.4f))
                }
            } catch (e: Exception) {
                Log.e(TAG_Snap, "Error processing product", e)
                null
            }
        }

        Log.d(TAG_Snap, "Completed processing all products. Total processed: ${processedProductsList.size}")
        onProgressUpdate(0.7f)
        processedProductsList

    } catch (e: Exception) {
        Log.e(TAG_Snap, "Error in processes_Organiseur", e)
        emptyList()
    }
}
