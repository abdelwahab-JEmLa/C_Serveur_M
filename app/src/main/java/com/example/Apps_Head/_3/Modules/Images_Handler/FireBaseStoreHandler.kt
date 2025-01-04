package com.example.Apps_Head._3.Modules.Images_Handler

import androidx.lifecycle.viewModelScope
import com.example.Apps_Head._1.Model.AppsHeadModel
import com.example.Apps_Head._1.Model.AppsHeadModel.Companion.updateProduitsFireBase
import com.example.Apps_Head._2.ViewModel.InitViewModel
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

open class FireBaseStoreHandler(private val initViewModel: InitViewModel) {
   // Pour suivre les opérations de mise à jour d'image en cours
   private var currentImageUpdateJobs = mutableMapOf<Long, Job>()

   fun startImageUpdate(_app_Initialize_Model: AppsHeadModel, produitId: Long) {
       // Si une mise à jour est déjà en cours pour ce produit, on ne fait rien
       if (currentImageUpdateJobs[produitId]?.isActive == true) {
           return
       }

       // Lance une nouvelle mise à jour
       currentImageUpdateJobs[produitId] = initViewModel.viewModelScope.launch {
           try {
               updateProductImage(produitId)

               // Met à jour le flag dans le modèle
               val productIndex =
                   initViewModel._appsHeadModel.produitsMainDataBase.indexOfFirst { it.id == produitId }
               if (productIndex != -1) {
                   initViewModel._appsHeadModel.produitsMainDataBase[productIndex].itImageBesoinToBeUpdated =
                       false
               }

               // Met à jour Firebase
               initViewModel._appsHeadModel.produitsMainDataBase.updateProduitsFireBase()

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
           .child("Images Articles Data Base/AppsHeadModel.Produit_Main_DataBase/$fileName")

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

   override fun onCleared() {   //-->
   //TODO(1): 'onCleared' overrides nothing
       super.onCleared()    //-->
       //TODO(1): Unresolved reference: onCleared
       // Annule toutes les opérations de mise à jour d'image en cours
       currentImageUpdateJobs.values.forEach { job ->
           job.cancel()
       }
       currentImageUpdateJobs.clear()

       // Supprime le listener de la base de données
       AppsHeadModel.ref_produitsDataBase.removeEventListener(object : ValueEventListener {
           override fun onDataChange(snapshot: DataSnapshot) {}
           override fun onCancelled(error: DatabaseError) {}
       })
   }
}

