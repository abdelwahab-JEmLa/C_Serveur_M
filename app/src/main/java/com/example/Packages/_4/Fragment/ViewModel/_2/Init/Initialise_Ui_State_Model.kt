package com.example.Packages._3.Fragment.ViewModel._2.Init.Main.Components

import android.util.Log
import com.example.Packages._4.Fragment.ViewModel._1.Main.F4_ViewModel

private const val TAG_Snap = "InitialeUiState"

internal suspend fun F4_ViewModel.Initialise_ViewModel() {
    try {
        Log.d(TAG_Snap, "Starting Initialise_ViewModel")
            initializationProgress = 0.1f
            isInitializing = true

            _uiState.load_Self_FromFirebaseDataBase()
            _app_Initialize_Model.produit_Main_DataBase.load_Self_FromFirebaseDataBase()
        //->
        //FIXME: ("Unresolved reference: load_Self_FromFirebaseDataBase")
            _app_Initialize_Model.produit_Main_DataBase.forEach { new_produit_A_Update ->
                try {
                    new_produit_A_Update.besoin_To_Be_Updated = false

                } catch (e: Exception) {
                    Log.e(TAG_Snap, "Error processing product ${new_produit_A_Update.id}", e)
                }
            }

             _app_Initialize_Model.update_UiStateFirebaseDataBase()
        }                 //->
    //FIXME: ("Unresolved reference: update_UiStateFirebaseDataBase")

        initializationProgress = 1.0f
        initializationComplete = true
        Log.d(TAG_Snap, "Completed Initialise_ViewModel")

    } catch (e: Exception) {
        Log.e(TAG_Snap, "Error in Initialise_ViewModel", e)
        throw e
    } finally {
        isInitializing = false
    }                          //->
//FIXME: ("Conflicting overloads: public fun `<no name provided>`(): Unit defined in com.example.Packages._3.Fragment.ViewModel._2.Init.Main.Components in file Initialise_Ui_State_Model.kt, public fun `<no name provided>`(): Unit defined in com.example.Packages._3.Fragment.ViewModel._2.Init.Main.Components in file Initialise_Ui_State_Model.kt")
}
