package com.example.Packages._3.Fragment.ViewModel._2.Init.Main.Components

import android.util.Log
import com.example.Packages._4.Fragment.ViewModel._1.Main.F4_ViewModel

private const val TAG_Snap = "InitialeUiState"

suspend fun F4_ViewModel.Initialise_ViewModel() {
    try {
        Log.d(TAG_Snap, "Starting Initialise_ViewModel")
        initializationProgress = 0.1f
        isInitializing = true

        _uiState.load_Self_FromFirebaseDataBase()

        _app_Initialize_Model.produit_Main_DataBase.forEach { product ->
            try {
                product.load_Self_FromFirebaseDataBase()

                product.besoin_To_Be_Updated = false
            } catch (e: Exception) {
                Log.e(TAG_Snap, "Error processing product ${product.id}", e)
            }
        }

        _uiState.update_UiStateFirebaseDataBase()

        initializationProgress = 1.0f
        initializationComplete = true

        Log.d(TAG_Snap, "Completed Initialise_ViewModel")
    } catch (e: Exception) {
        Log.e(TAG_Snap, "Error in Initialise_ViewModel", e)
        throw e
    } finally {
        isInitializing = false
    }
}
