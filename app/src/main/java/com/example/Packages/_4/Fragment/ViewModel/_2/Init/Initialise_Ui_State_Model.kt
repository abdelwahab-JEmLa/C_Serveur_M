package com.example.Packages._3.Fragment.ViewModel._2.Init.Main.Components

import android.util.Log
import com.example.Packages._4.Fragment.ViewModel._3.Main.F4_ViewModel

private const val TAG_Snap = "Initiale"

suspend fun F4_ViewModel.Initialise_ViewModel() {
    try {
        Log.d(TAG_Snap, "Starting Initialise_ViewModel")
        initializationProgress = 0.1f
        isInitializing = true

        _uiState.load_Self_FromFirebaseDataBase()

        _app_Initialize_Model.load_Produits_FireBase()

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
