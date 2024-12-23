package com.example.App_Produits_Main._2.ViewModel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.App_Produits_Main._1.Model.App_Initialize_Model
import com.example.App_Produits_Main._1.Model.load_Produits_FireBase
import com.example.App_Produits_Main._3.Modules.Images_Handler.FireBase_Store_Handler
import com.example.Packages._3.Fragment.ViewModel._2.Init.Main.Components.Initialise_ViewModel_Main
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

// Add TAG constant at the top of the class
private const val TAG = "CameraPickImageHandler"
open class Apps_Produits_Main_DataBase_ViewModel : ViewModel() {


    var _app_Initialize_Model by mutableStateOf(
        App_Initialize_Model()
    )

    val app_Initialize_Model: App_Initialize_Model get() = this._app_Initialize_Model

    // Progress tracking
    var initializationProgress by mutableFloatStateOf(0f)

    var isInitializing by mutableStateOf(false)
    var initializationComplete by mutableStateOf(false)

    init {
        viewModelScope.launch {
            try {
                isInitializing = true
                Initialise_ViewModel_Main()
                setupDatabaseListener()
            } finally {
                isInitializing = false
            }
        }
    }

    private fun setupDatabaseListener() {
        _app_Initialize_Model.ref_Produits_Main_DataBase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                viewModelScope.launch {
                    try {
                        // Store current positions before loading new data
                        val previousPositions = _app_Initialize_Model.produits_Main_DataBase.associate { produit ->
                            produit.id to produit.historique_Bons_Commend.map { grossist ->
                                grossist.vid to grossist.position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit
                            }
                        }

                        // Load new data
                        _app_Initialize_Model.load_Produits_FireBase()
                        Log.d(TAG, "Starting to check products for updates")

                        // Check for image updates and position changes
                        _app_Initialize_Model.produits_Main_DataBase.forEach { produit ->
                            // Handle image updates
                            if (produit.it_Image_besoin_To_Be_Updated) {
                                Log.d(TAG, "Product ${produit.id} needs image update, initiating update process")
                                FireBase_Store_Handler().startImageUpdate(_app_Initialize_Model,produit.id)
                            }

                            // Check for position changes
                            val previousProductPositions = previousPositions[produit.id] ?: emptyList()
                            produit.historique_Bons_Commend.forEach { grossist ->
                                val previousPosition = previousProductPositions.find { it.first == grossist.vid }?.second
                                if (previousPosition != null && previousPosition != grossist.position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit) {
                                    Log.d(TAG, "Position changed for product ${produit.id}, supplier ${grossist.vid}: $previousPosition -> ${grossist.position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit}")
                                    handlePositionChange(produit.id, grossist.vid, grossist.position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit)
                                }
                            }
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Error in database listener: ${e.message}", e)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Database error: ${error.message}", error.toException())
            }
        })
    }

    private fun handlePositionChange(productId: Long, supplierId: Long, newPosition: Int) {
        viewModelScope.launch {
            try {
                // Update the position in Firebase
                val productRef = _app_Initialize_Model.ref_Produits_Main_DataBase.child(productId.toString())
                    .child("grossist_Choisi_Pour_Acheter_CeProduit")
                    .child(supplierId.toString())
                    .child("position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit")

                productRef.setValue(newPosition).await()
                Log.d(TAG, "Successfully updated position for product $productId, supplier $supplierId")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to update position in Firebase: ${e.message}", e)
            }
        }
    }

}
