package com.example.Apps_Head._3.Modules.Images_Handler

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.Apps_Head._1.Model.AppsHeadModel.Companion.updateProduitsFireBase
import com.example.Apps_Head._2.ViewModel.InitViewModel
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File

fun ImageStoreUpdate(initViewModel: InitViewModel, produitId: Long) {
    val activeJobs = mutableMapOf<Long, Job>()
    val imagesBasePath = "/storage/emulated/0/Abdelwahab_jeMla.com/IMGs/BaseDonne"

    // Skip if already updating this product
    if (activeJobs[produitId]?.isActive == true) return

    // Start new update job
    activeJobs[produitId] = initViewModel.viewModelScope.launch {
        withContext(Dispatchers.IO) {
            try {
                // Setup file paths
                val fileName = "${produitId}_1.jpg"
                val localDir = File(imagesBasePath).apply {
                    if (!exists()) mkdirs()
                }
                val localFile = File(localDir, fileName)

                // Download image
                Firebase.storage.reference
                    .child("Images Articles Data Base/AppsHeadModel.Produit_Main_DataBase/$fileName")
                    .getFile(localFile)
                    .await()

                // Update model and Firebase
                initViewModel._appsHeadModel.produitsMainDataBase
                    .find { it.id == produitId }
                    ?.let { product ->
                        product.itImageBesoinActualisation = false
                        initViewModel._appsHeadModel.produitsMainDataBase.updateProduitsFireBase()
                    }

                Log.d("FireBaseStore", "Updated image for product $produitId")
            } catch (e: Exception) {
                Log.e("FireBaseStore", "Failed updating product $produitId: ${e.message}")
            } finally {
                activeJobs.remove(produitId)
            }
        }
    }
}

