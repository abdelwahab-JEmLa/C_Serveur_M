package com.example.Packages._3.Fragment.ViewModel.init._1.Aliment_From_Authers_Refs.initial

import android.util.Log
import com.example.Packages._3.Fragment.Models.Test.UiStateSnapshotStateList
import com.example.Packages._3.Fragment.ViewModel.P3_ViewModel
import kotlin.random.Random

const val TAG_2 = "InitialeUiState"

internal suspend fun P3_ViewModel._1Initialize(
    onProgressUpdate: (Float) -> Unit = {}
) {
    try {
        Log.d(TAG_2, "Starting _1Initialize")
        onProgressUpdate(0.1f)

        // Load existing state from Firebase
        try {
            _uiStateSnapshotStateList.loadFromFirebase()
            Log.d(TAG_2, "Successfully loaded existing state from Firebase")
        } catch (e: Exception) {
            Log.w(TAG_2, "No existing state found or error loading, starting fresh", e)
        }

        onProgressUpdate(0.3f)

        // Create random products
        val randomProducts = List(200) {
            UiStateSnapshotStateList.Product(
                id = Random.nextInt(500, 700).toLong(),
                initialTriggerTime = System.currentTimeMillis()
            )
        }

        onProgressUpdate(0.5f)

        // Create or update the default group
        val defaultGroup = UiStateSnapshotStateList.GroupFireBaseReference(
            id = 1L,
            position = 1,
            nom = "Produits_Commend_DataBase",
            initialProductsToUpdate = randomProducts
        )

        onProgressUpdate(0.7f)

        // Update or add the group using the new utility functions
        val existingGroup = _uiStateSnapshotStateList.getGroupById(1L)
        if (existingGroup != null) {
            // Clear existing products and add new ones
            existingGroup.productsToUpdate.clear()
            randomProducts.forEach { product ->
                existingGroup.addProduct(product)
            }
            // Update the group in the state
            _uiStateSnapshotStateList.updateGroup(existingGroup)
            // Update the specific group in Firebase
            existingGroup.updateSelfInFirebase()
        } else {
            // Add new group if it doesn't exist
            _uiStateSnapshotStateList.addGroup(defaultGroup)
        }

        onProgressUpdate(0.9f)

        // Update the entire state in Firebase
        _uiStateSnapshotStateList.updateSelfInFirebase()

        onProgressUpdate(1.0f)
        Log.d(TAG_2, "Completed _1Initialize")
    } catch (e: Exception) {
        Log.e(TAG_2, "Error in _1Initialize", e)
        throw e
    }
}
