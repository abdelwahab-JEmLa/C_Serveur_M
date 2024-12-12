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
         /**First Init*/
        val cree = true
        if (cree) {
            List(1500) { i ->
                val produit = UiState.Produit_DataBase(
                    id = i.toLong(),
                    it_ref_Id_don_FireBase=1L,
                    it_ref_don_FireBase = "produit_DataBase",
                    init_besoin_To_Be_Updated = true
                )
                _uiState.produit_DataBase.add(produit)
            }
        }

        _uiState.produit_DataBase.forEach {
               it.besoin_To_Be_Updated = false
        }
        /**------------------------------------------*/



        initializationProgress = 0.9f

        onProgressUpdate(1.0f)
        Log.d(TAG_Snap, "Completed _1Initialize")
    } catch (e: Exception) {
        Log.e(TAG_Snap, "Error in _1Initialize", e)
        throw e
    }
}



