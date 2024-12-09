package com.example.Packages._3.Fragment.ViewModel.init

import android.os.Build
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.Packages._3.Fragment.Models.Ui_Mutable_State
import com.example.Packages._3.Fragment.ViewModel.P3_ViewModel
import com.example.Packages._3.Fragment.ViewModel.init._1.Aliment_From_Authers_Refs.Model.ColorArticle
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.database
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

private const val TAG = "InitialeUiState"
private const val TAG_COLOR = "ColorLoading"

internal suspend fun getSupplierArticlesData(idArticle: Long): DataSnapshot? {
    return try {
        Firebase.database.getReference("K_SupplierArticlesRecived")
            .orderByChild("a_c_idarticle_c")
            .equalTo(idArticle.toDouble())
            .get()
            .await()
            .children
            .firstOrNull()
    } catch (e: Exception) {
        Log.e(TAG, "Error fetching supplier data for article $idArticle", e)
        null
    }
}

private suspend fun getColorData(colorId: Long): ColorArticle? {
    return try {
        // Improved error handling and logging
        val colorSnapshot = Firebase.database.getReference("H_ColorsArticles")
            .orderByChild("idColore")
            .equalTo(colorId.toDouble())
            .get()
            .await()
            .children
            .firstOrNull()

        val colorData = colorSnapshot?.getValue(ColorArticle::class.java)

        // Detailed logging for debugging
        when {
            colorData == null -> Log.w(TAG_COLOR, "No color data found for ID $colorId")
            colorData.iconColore.isNullOrEmpty() -> Log.w(TAG_COLOR, "Empty emoji for colorId: $colorId")
            else -> Log.d(TAG_COLOR, "Successfully loaded emoji for colorId: $colorId, emoji: ${colorData.iconColore}")
        }                                        //

        // Fallback mechanism for retrieving color data
        colorData ?: colorSnapshot?.let {
            ColorArticle(
                idColore = it.child("idColore").getValue(Long::class.java) ?: colorId,
                nameColore = it.child("nameColore").getValue(String::class.java) ?: "",
                iconColore = it.child("iconColore").getValue(String::class.java) ?: "",
                classementColore = it.child("classementColore").getValue(Int::class.java) ?: 0
            )
        }
    } catch (e: Exception) {
        Log.e(TAG_COLOR, "Error fetching color data for color $colorId", e)
        null
    }
}

internal suspend fun Aliment_Fragment3_Ui_State(): List<Ui_Mutable_State.Produits_Commend_DataBase> = coroutineScope {
    try {
        // Fetching products from Firebase
        val productsSnapshot = Firebase.database.getReference("e_DBJetPackExport")
            .get()
            .await()

        productsSnapshot.children.map { productSnapshot ->
            async {
                // Extract article ID
                val idArticle = productSnapshot.child("idArticle").getValue(Long::class.java) ?: 0L

                // Fetch supplier data
                val supplierData = getSupplierArticlesData(idArticle)

                // Build colors list with null safety and comprehensive data extraction
                val colorsList = buildList {
                    for (i in 1..4) {
                        val colorField = "couleur$i"
                        val idColorField = "idcolor$i"

                        val color = productSnapshot.child(colorField).getValue(String::class.java)
                        if (!color.isNullOrEmpty()) {
                            val colorId = productSnapshot.child(idColorField).getValue(Long::class.java) ?: 0L
                            val quantity = supplierData?.child("color${i}SoldQuantity")?.getValue(Int::class.java) ?: 0

                            // Fetch color data with comprehensive logging
                            val colorData = getColorData(colorId)

                            add(
                                Ui_Mutable_State.Produits_Commend_DataBase.Colours_Et_Gouts_Commende(
                                    position_Du_Couleur_Au_Produit = i.toLong(),
                                    id_Don_Tout_Couleurs = colorId,
                                    nom = color,
                                    quantity_Achete = quantity,
                                    imogi = colorData?.iconColore ?: ""
                                )
                            )
                        }
                    }
                }

                // Create product data object
                Ui_Mutable_State.Produits_Commend_DataBase(
                    id = idArticle.toInt(),
                    nom = productSnapshot.child("nomArticleFinale").getValue(String::class.java) ?: "",
                    colours_Et_Gouts_Commende = colorsList
                )
            }
        }.awaitAll()
    } catch (e: Exception) {
        Log.e(TAG, "Error processing products", e)
        emptyList()
    }
}

internal suspend fun P3_ViewModel.Init_ImportCalcules_Ui_Stat() {
    viewModelScope.launch(Dispatchers.IO) {
        try {
            // Fetch product data
            val productsData = Aliment_Fragment3_Ui_State()

            // Get device name
            val phoneName = "${Build.MANUFACTURER} ${Build.MODEL}".trim()

            // Create a copy of products with potential future-proofing
            val updatedProducts = productsData.map { product ->
                product.copy(
                    grossist_Choisi_Pour_Acheter_CeProduit = product.grossist_Choisi_Pour_Acheter_CeProduit?.copy()
                )
            }

            // Update UI state
            _ui_Mutable_State.apply {
                clear_Ui_Mutable_State_C_produits_Commend_DataBase()
                addAll_TO_Ui_Mutable_State_C_produits_Commend_DataBase(updatedProducts)
                namePhone = phoneName
            }

            // Sync with Firebase
            refFirebase.setValue(_ui_Mutable_State.toMap())

            // Log grouping details for debugging
            _ui_Mutable_State.logGroupingDetails("P3_ViewModel")

        } catch (e: Exception) {
            Log.e(TAG, "Initialization error", e)
        }
    }
}
