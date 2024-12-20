package com.example.c_serveur.ViewModel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.Packages._3.Fragment.ViewModel._2.Init.Main.Components.Initialise_ViewModel_Main
import com.example.c_serveur.ViewModel.Model.App_Initialize_Model
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File
// Add TAG constant at the top of the class
    private const val TAG = "CameraPickImageHandler"
open class App_Initialize_ViewModel : ViewModel() {

    var _app_Initialize_Model by mutableStateOf(
        App_Initialize_Model()
    )

    val app_Initialize_Model: App_Initialize_Model get() = this._app_Initialize_Model

    // Progress tracking
    var initializationProgress by mutableFloatStateOf(0f)

    var isInitializing by mutableStateOf(false)
    var initializationComplete by mutableStateOf(false)

    private val databaseRef = Firebase.database
        .getReference("0_UiState_3_Host_Package_3_Prototype11Dec")
        .child("produit_DataBase")

    // Pour suivre les opérations de mise à jour d'image en cours
    private var currentImageUpdateJobs = mutableMapOf<Long, Job>()

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

    // In App_Initialize_ViewModel.kt
    private fun setupDatabaseListener() {
        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                viewModelScope.launch {
                    try {
                        _app_Initialize_Model.load_Produits_FireBase()
                        // Log the start of product checking
                        Log.d(TAG, "Starting to check products for image updates")

                        _app_Initialize_Model.produit_Main_DataBase.forEach { produit ->
                            if (produit.it_Image_besoin_To_Be_Updated) {
                                Log.d(TAG, "Product ${produit.id} needs image update, initiating update process")
                                startImageUpdate(produit.id)
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

    private fun startImageUpdate(produitId: Long) {
        // Si une mise à jour est déjà en cours pour ce produit, on ne fait rien
        if (currentImageUpdateJobs[produitId]?.isActive == true) {
            return
        }

        // Lance une nouvelle mise à jour
        currentImageUpdateJobs[produitId] = viewModelScope.launch {
            try {
                updateProductImage(produitId)

                // Met à jour le flag dans le modèle
                val productIndex = _app_Initialize_Model.produit_Main_DataBase.indexOfFirst { it.id == produitId }
                if (productIndex != -1) {
                    _app_Initialize_Model.produit_Main_DataBase[productIndex].it_Image_besoin_To_Be_Updated = false
                }

                // Met à jour Firebase
                _app_Initialize_Model.update_Produits_FireBase()

                // Nettoie le job terminé
                currentImageUpdateJobs.remove(produitId)
            } catch (e: Exception) {
                // En cas d'erreur, nettoie aussi le job
                currentImageUpdateJobs.remove(produitId)
            }
        }
    }

    private suspend fun updateProductImage(produitId: Long) = withContext(Dispatchers.IO) {
        val fileName = "${produitId}_1.jpg"
        val storageRef = Firebase.storage.reference
            .child("Images Articles Data Base/App_Initialize_Model.Produit_Main_DataBase/$fileName")

        val viewModelImagesPath = File("/storage/emulated/0/Abdelwahab_jeMla.com/IMGs/BaseDonne")
        if (!viewModelImagesPath.exists()) {
            viewModelImagesPath.mkdirs()
        }

        val localFile = File(viewModelImagesPath, fileName)
        try {
            storageRef.getFile(localFile).await()
        } catch (e: Exception) {
            throw Exception("Failed to download image: ${e.message}")
        }
    }

    override fun onCleared() {
        super.onCleared()
        // Annule toutes les opérations de mise à jour d'image en cours
        currentImageUpdateJobs.values.forEach { job ->
            job.cancel()
        }
        currentImageUpdateJobs.clear()

        // Supprime le listener de la base de données
        databaseRef.removeEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {}
            override fun onCancelled(error: DatabaseError) {}
        })
    }
}
