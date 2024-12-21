package com.example.Apps_Produits_Main_DataBase._1.Images_Handler

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.Apps_Produits_Main_DataBase._2.ViewModel.Model.App_Initialize_Model
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

open class FireBase_Store_Handler : ViewModel() {
    protected val databaseRef = Firebase.database
        .getReference("0_UiState_3_Host_Package_3_Prototype11Dec")
        .child("produit_DataBase")

    // Pour suivre les opérations de mise à jour d'image en cours
    private var currentImageUpdateJobs = mutableMapOf<Long, Job>()

    protected fun startImageUpdate(_app_Initialize_Model: App_Initialize_Model, produitId: Long) {
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
                    _app_Initialize_Model.produit_Main_DataBase.indexOfFirst { it.id == produitId }
                if (productIndex != -1) {
                    _app_Initialize_Model.produit_Main_DataBase[productIndex].it_Image_besoin_To_Be_Updated =
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
