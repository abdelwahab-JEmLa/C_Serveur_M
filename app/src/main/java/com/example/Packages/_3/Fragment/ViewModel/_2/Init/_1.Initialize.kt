package com.example.Packages._3.Fragment.ViewModel._2.Init

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import com.example.Packages._3.Fragment.ViewModel.P3_ViewModel
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
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

        val (defaultGroup, Ref2) = refrences()

        _uiState.referencesFireBaseGroup.add(defaultGroup)
        _uiState.referencesFireBaseGroup.add(Ref2)

        val productsData = processes_Organiseur(
            referenceFireBas = _uiState.referencesFireBaseGroup.find { it.id == 1L },
            onProgressUpdate = { progress ->
                onProgressUpdate(0.2f + (progress * 0.6f))
            }
        )

        // Update or add products
        productsData.forEach { produitUpdate ->
            val existingIndex = _uiState.produit_DataBase.indexOfFirst { it.id == produitUpdate.id }
            if (existingIndex != -1) {
                _uiState.produit_DataBase[existingIndex] = produitUpdate
                _uiState.produit_DataBase[existingIndex].grossist_Choisi_Pour_Acheter_CeProduit = generate_Random_Supplier2()
                _uiState.produit_DataBase[existingIndex].updateSelfInFirebaseDataBase()
            } else {
                produitUpdate.updateSelfInFirebaseDataBase()
            }
        }

        onProgressUpdate(0.9f)

        _uiState.referencesFireBaseGroup.find { it.id == 1L }?.let { group ->
            group.produit_Update_Ref.clear()
            group.updateMode = if (group.updateMode == UiState.ReferencesFireBaseGroup.items_Need_To_Be_Updated_From_it.ALL)
                UiState.ReferencesFireBaseGroup.items_Need_To_Be_Updated_From_it.TOTAL_QUANTITY_GROSSIST_COMMAND_ABOVE_ZERO
            else
                UiState.ReferencesFireBaseGroup.items_Need_To_Be_Updated_From_it.ALL
            group.setSelfInFirebaseDataBase()
        }

        onProgressUpdate(1.0f)
        Log.d(TAG_Snap, "Completed _1Initialize")
    } catch (e: Exception) {
        Log.e(TAG_Snap, "Error in _1Initialize", e)
        throw e
    }
}

private fun refrences(): Pair<UiState.ReferencesFireBaseGroup, UiState.ReferencesFireBaseGroup> {
    // Create the ReferencesFireBaseGroup object with proper UpdateMode
    val defaultGroup = UiState.ReferencesFireBaseGroup(
        id = 1L,
        position = 1,
        nom = "Produits_Commend_DataBase",
        reference_key = "O_SoldArticlesTabelle",
        parent_Id = 2L,
        parent_key = "0_UiState_3_Host_Package_3_Prototype11Dec",
        initialUpdatesModes = UiState.ReferencesFireBaseGroup.items_Need_To_Be_Updated_From_it.TOTAL_QUANTITY_GROSSIST_COMMAND_ABOVE_ZERO
    )
    // Create the ReferencesFireBaseGroup object with proper UpdateMode
    val Ref2 = UiState.ReferencesFireBaseGroup(
        id = 2L,
        position = 2,
        nom = "O_SoldArticlesTabelle",
        reference_key = "O_SoldArticlesTabelle",
        parent_Id = 1L,
        parent_key = "https://abdelwahab-jemla-com-default-rtdb.europe-west1.firebasedatabase.app/",
        initialUpdatesModes = UiState.ReferencesFireBaseGroup.items_Need_To_Be_Updated_From_it.TOTAL_QUANTITY_GROSSIST_COMMAND_ABOVE_ZERO
    )
    return Pair(defaultGroup, Ref2)
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

        val productsToProcess = when (referenceFireBas?.updateMode) {
            UiState.ReferencesFireBaseGroup.items_Need_To_Be_Updated_From_it.ALL -> {
                productsSnapshot.children.toList()
            }
            UiState.ReferencesFireBaseGroup.items_Need_To_Be_Updated_From_it.TOTAL_QUANTITY_GROSSIST_COMMAND_ABOVE_ZERO -> {
                val productIds = referenceFireBas.produit_Update_Ref.map { it.id }
                productsSnapshot.children.filter { productSnapshot ->
                    val idArticle = productSnapshot.child("idArticle").getValue(Long::class.java)
                    val totalQuantity = calculateTotalSupplierQuantity(productSnapshot)       
                    //TODO(1): fait ici che
                    // rerche dont data class SoldArticlesTabelle internal constructor(
                    //    val vid: Long = 0,
                    //    val idArticle: Long = 0,     ou id =     productSnapshot.child("idArticle").g et
                    //    val nameArticle: String = "",
                    //    val clientSoldToItId: Long = 0,
                    //    val date: String = "",
                    //    val color1IdPicked: Long = 0,
                    //    val color1SoldQuantity: Int = 0,        calcule sum de <
                    //    val color2IdPicked: Long = 0,
                    //    val color2SoldQuantity: Int = 0,                 +
                    //    val color3IdPicked: Long = 0,
                    //    val color3SoldQuantity: Int = 0,             +
                    //    val color4IdPicked: Long = 0,
                    //    val color4SoldQuantity: Int = 0,             +
                    //    val confimed: Boolean = false,
                    //) {
                    //    constructor() : this(0)
                    //}
                    productIds.contains(idArticle) && totalQuantity > 0
                }
            }
            null -> {
                Log.w(TAG_Snap, "No update mode specified, processing all products")
                productsSnapshot.children.toList()
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
                    Log.d(TAG_Snap, "Completed processing product $idArticle ($processedProducts/$totalProducts)")
                    onProgressUpdate(0.3f + (processedProducts.toFloat() / totalProducts * 0.4f))
                }
            } catch (e: Exception) {
                Log.e(TAG_Snap, "Error processing product", e)
                null
            }
        }.toMutableStateList()

        Log.d(TAG_Snap, "Completed processing all products. Total processed: ${processedProductsList.size}")
        onProgressUpdate(0.7f)
        processedProductsList

    } catch (e: Exception) {
        Log.e(TAG_Snap, "Error in processes_Organiseur", e)
        mutableStateListOf()
    }
}

private fun calculateTotalSupplierQuantity(productSnapshot: DataSnapshot): Int {
    return try {
        productSnapshot.child("grossist_Choisi_Pour_Acheter_CeProduit")
            .children
            .sumOf { it.child("quantity_Achete").getValue(Int::class.java) ?: 0 }
    } catch (e: Exception) {
        Log.e(TAG_Snap, "Error calculating total supplier quantity", e)
        0
    }
}
