package com.example.Packages._3.Fragment.ViewModel._2.Init.Main

import android.util.Log
import com.example.Packages._3.Fragment.ViewModel.P3_ViewModel
import com.example.Packages._3.Fragment.ViewModel._2.Init.Main.Model.UiState

internal const val TAG_Snap = "InitialeUiState"

internal suspend fun P3_ViewModel._1Initialize(
    onProgressUpdate: (Float) -> Unit = {}
) {
    try {
        Log.d(TAG_Snap, "Starting _1Initialize")
        initializationProgress = (1/10).toFloat()

        _uiState.loadFromFirebaseDataBase()

        initializationProgress = (3/10).toFloat()


        List(1500) { i ->
            val produit = UiState.Produit_DataBase(
                id = i.toLong(),
                init_besoin_To_Be_Updated = true,
            )
            _uiState.produit_DataBase.add(produit)
        }

        initializationProgress = 0.9f

        onProgressUpdate(1.0f)
        Log.d(TAG_Snap, "Completed _1Initialize")
    } catch (e: Exception) {
        Log.e(TAG_Snap, "Error in _1Initialize", e)
        throw e
    }
}



