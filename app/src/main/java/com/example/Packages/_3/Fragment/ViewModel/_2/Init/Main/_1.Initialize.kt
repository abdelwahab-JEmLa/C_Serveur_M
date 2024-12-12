package com.example.Packages._3.Fragment.ViewModel._2.Init.Main.Components

import android.util.Log
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import com.example.Packages._3.Fragment.ViewModel.P3_ViewModel
import com.example.Packages._3.Fragment.ViewModel._2.Init.Main.Model.Archives.Ancien_Produits_DataBase
import com.example.Packages._3.Fragment.ViewModel._2.Init.Main.Model.Archives.Ancien_SoldArticlesTabelle
import com.example.Packages._3.Fragment.ViewModel._2.Init.Main.Model.UiState
import com.google.firebase.Firebase
import com.google.firebase.database.database
import kotlinx.coroutines.tasks.await


const val TAG_Snap = "InitialeUiState"

// _1.Initialize.kt update
internal suspend fun P3_ViewModel._1Initialize(
) {
    try {
        Log.d(TAG_Snap, "Starting _1Initialize")
        initializationProgress = (1/10).toFloat()
        _uiState.loadFromFirebaseDataBase()

        initializationProgress = 0.3f
        initializationProgress = (3/10).toFloat()
        // First Init
        if (true) { // Replace with actual condition
            List(1500) { i ->
                val produit = UiState.Produit_DataBase(
                    id = i.toLong(),
                    it_ref_Id_don_FireBase = 1L,
                    it_ref_don_FireBase = "produit_DataBase",
                    init_besoin_To_Be_Updated = true
                )
                _uiState.produit_DataBase.add(produit)
            }
        }

        // Get ancient data
        val ancienData = get_Datas()

        // Update products
        _uiState.produit_DataBase.forEach { produit ->
            produit.besoin_To_Be_Updated = false
            // Find matching ancient product
            ancienData.produitsDatabase.find { it.idArticle == produit.id }?.let { ancien ->
                produit.nom = ancien.nomArticleFinale

            }
        }


        initializationProgress = (1/10).toFloat()

        Log.d(TAG_Snap, "Completed _1Initialize")
    } catch (e: Exception) {
        Log.e(TAG_Snap, "Error in _1Initialize", e)
        throw e
    }
}
