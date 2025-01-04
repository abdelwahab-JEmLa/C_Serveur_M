package com.example.Packages._1.A1_Fragment.ViewModel._2.Init.Main.Components

import android.util.Log
import com.example.Packages._4.Fragment._1.Main.ViewModel.F4_ViewModel

private const val TAG_Snap = "Initiale"

suspend fun F4_ViewModel.Initialise_ViewModel() {
    try {
        Log.d(TAG_Snap, "Starting Initialise_ViewModel")
        initializationProgress = 0.1f
        isInitializing = true

        _uiState.load_Self_FromFirebaseDataBase()


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
