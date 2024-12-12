package com.example.Packages._3.Fragment.ViewModel._2.Init.Main

import android.util.Log
import com.example.Packages._3.Fragment.ViewModel.P3_ViewModel
import com.example.Packages._3.Fragment.ViewModel._2.Init.UiState
import kotlin.random.Random

internal const val TAG_Snap = "InitialeUiState"

internal suspend fun P3_ViewModel._1Initialize(
    onProgressUpdate: (Float) -> Unit = {}
) {
    try {
        Log.d(TAG_Snap, "Starting _1Initialize")
        initializationProgress = ( 1 /10).toFloat() 
        
        try {
            _uiState.loadFromFirebaseDataBase()
            Log.d(TAG_Snap, "Successfully loaded existing state from Firebase")
        } catch (e: Exception) {
            Log.w(TAG_Snap, "No existing state found or error loading, starting fresh", e)
        }

        initializationProgress = ( 3 /10).toFloat()

        phase1_Insert_Refrences()

        phase2_insert_Produits(9)
        
        initializationProgress = 0.9f


        onProgressUpdate(1.0f)
        Log.d(TAG_Snap, "Completed _1Initialize")
    } catch (e: Exception) {
        Log.e(TAG_Snap, "Error in _1Initialize", e)
        throw e
    }
}


private suspend fun P3_ViewModel.phase2_insert_Produits(
    progress: Int
) {
    initializationProgress = ( progress /10).toFloat()
    
    List(1000){i->
        _uiState.produit_DataBase.add(id=i)
        
    }
    
}
private fun P3_ViewModel.phase1_Insert_Refrences() {
    val randomProducts = List(200) {
        UiState.ReferencesFireBaseGroup.Produit_Update_Ref(
            id = Random.nextInt(500, 700).toLong(),
            initialTriggerTime = System.currentTimeMillis()
        )
    }
    // Create the ReferencesFireBaseGroup object with proper UpdateMode
    val defaultGroup = UiState.ReferencesFireBaseGroup(
        id = 1L,
        position = 1,
        nom = "Produits_Commend_DataBase",
        reference_key = "O_SoldArticlesTabelle",
        parent_Id = 2L,
        parent_key = "0_UiState_3_Host_Package_3_Prototype11Dec",
    )
    // Create the ReferencesFireBaseGroup object with proper UpdateMode
    val Ref2 = UiState.ReferencesFireBaseGroup(
        id = 2L,
        position = 2,
        nom = "O_SoldArticlesTabelle",
        reference_key = "O_SoldArticlesTabelle",
        parent_Id = 1L,
        parent_key = "https://abdelwahab-jemla-com-default-rtdb.europe-west1.firebasedatabase.app/",
    )

    _uiState.referencesFireBaseGroup.add(defaultGroup)
    _uiState.referencesFireBaseGroup.add(Ref2)
}

    
    
    
    
    
    

    
    


