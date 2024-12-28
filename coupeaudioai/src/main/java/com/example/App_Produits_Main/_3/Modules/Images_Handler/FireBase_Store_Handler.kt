package com.example.App_Produits_Main._3.Modules.Images_Handler

import androidx.lifecycle.viewModelScope
import com.example.App_Produits_Main._1.Model.AppInitializeModel
import com.example.App_Produits_Main._2.ViewModel.AppInitialize_ViewModel
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File

open class FireBase_Store_Handler : AppInitialize_ViewModel() {
    // Pour suivre les opérations de mise à jour d'image en cours
    private var currentImageUpdateJobs = mutableMapOf<Long, Job>()

    fun startImageUpdate(_app_Initialize_Model: AppInitializeModel, produitId: Long) {
        // Si une mise à jour est déjà en cours pour ce produit, on ne fait rien
        if (currentImageUpdateJobs[produitId]?.isActive == true) {
            return
        }

        // Lance une nouvelle mise à jour
        currentImageUpdateJobs[produitId] = viewModelScope.launch {
            try {
                updateProductImage(produitId)

                // Met à jour le flag dans le modèle
                val productIndex =
                    _app_Initialize_Model.produits_Main_DataBase.indexOfFirst { it.id == produitId }
                if (productIndex != -1) {
                    _app_Initialize_Model.produits_Main_DataBase[productIndex].it_Image_besoin_To_Be_Updated =
                        false
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
            .child("Images Articles Data Base/AppInitializeModel.Produit_Main_DataBase/$fileName")

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
        _app_Initialize_Model.ref_Produits_Main_DataBase.removeEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {}
            override fun onCancelled(error: DatabaseError) {}
        })
    }
}
