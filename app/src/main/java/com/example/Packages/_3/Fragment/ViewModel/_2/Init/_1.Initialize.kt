package com.example.Packages._3.Fragment.ViewModel._2.Init

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
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

        try {
            _uiState.loadFromFirebaseDataBase()
            Log.d(TAG_Snap, "Successfully loaded existing state from Firebase")
        } catch (e: Exception) {
            Log.w(TAG_Snap, "No existing state found or error loading, starting fresh", e)
        }

        onProgressUpdate(0.3f)

        val randomProducts = List(200) {
            UiState.ReferencesFireBaseGroup.Produit_Update_Ref(
                id = Random.nextInt(500, 700).toLong(),
                initialTriggerTime = System.currentTimeMillis()
            )
        }

        onProgressUpdate(0.5f)

        val defaultGroup = UiState.ReferencesFireBaseGroup(
            id = 1L,
            position = 1,
            nom = "Produits_Commend_DataBase",
            initialProduits_Update_Ref = randomProducts
        )

        onProgressUpdate(0.7f)

        val existingRefIndex = _uiState.referencesFireBaseGroup.indexOfFirst { it.id == 1L }
        if (existingRefIndex != -1) {
            _uiState.referencesFireBaseGroup[existingRefIndex].produit_Update_Ref =
                randomProducts.toMutableStateList()
        } else {
            _uiState.referencesFireBaseGroup.add(defaultGroup)
        }

        val productsData = processes_Organiseur(
            referenceFireBas = _uiState.referencesFireBaseGroup.find { it.id == 1L },
            onProgressUpdate = { progress ->
                onProgressUpdate(0.2f + (progress * 0.6f))
            }
        )

        // Safely update or add products
        productsData.forEach { produitUpdate ->
            val existingIndex = _uiState.produit_DataBase.indexOfFirst { it.id == produitUpdate.id }
            if (existingIndex != -1) {
                _uiState.produit_DataBase[existingIndex] = produitUpdate
                _uiState.produit_DataBase[existingIndex].grossist_Choisi_Pour_Acheter_CeProduit= generate_Random_Supplier2()
                _uiState.produit_DataBase[existingIndex].updateSelfInFirebaseDataBase()
            } else {
                produitUpdate.updateSelfInFirebaseDataBase()
            }
        }

        onProgressUpdate(0.9f)

        _uiState.referencesFireBaseGroup.find { it.id == 1L }?.let { group ->
            group.produit_Update_Ref.clear()
            group.updateAllTrigger = !group.updateAllTrigger
            group.setSelfInFirebaseDataBase()
        }

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
): SnapshotStateList<UiState.Produit_DataBase> = coroutineScope {
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

        // Change here: Directly create a SnapshotStateList
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
        }.toMutableStateList() // Explicitly convert to SnapshotStateList

        Log.d(TAG_Snap, "Completed processing all products. Total processed: ${processedProductsList.size}")
        onProgressUpdate(0.7f)
        processedProductsList

    } catch (e: Exception) {
        Log.e(TAG_Snap, "Error in processes_Organiseur", e)
        // Return an empty SnapshotStateList in case of error
        mutableStateListOf()
    }
}
