// CameraPickImageHandler.kt
package com.example.c_serveur.Modules

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.core.content.FileProvider
import com.example.c_serveur.ViewModel.Model.App_Initialize_Model
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.tasks.await
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CameraPickImageHandler(
    private val context: Context,
    private val appInitializeModel: App_Initialize_Model
) {
    companion object {
        private const val TAG = "CameraPickImageHandler"
        private const val BASE_PATH = "/storage/emulated/0/Abdelwahab_jeMla.com/IMGs/BaseDonne"
    }

    var tempImageUri: Uri? = null
    private var isHandlingImage = false
    private val storageBasePath = "Images Articles Data Base/App_Initialize_Model.Produit_Main_DataBase"

    init {
        Log.d(TAG, "Initializing CameraPickImageHandler")
        createBasePath()
    }

    private fun createBasePath() {
        val baseDir = File(BASE_PATH)
        if (!baseDir.exists()) {
            val created = baseDir.mkdirs()
            Log.d(TAG, "Base directory creation ${if (created) "successful" else "failed"}: $BASE_PATH")
        }
    }

    fun createTempImageUri(): Uri {
        Log.d(TAG, "Creating temporary image URI")
        return try {
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val tempFile = File.createTempFile(
                "JPEG_${timeStamp}_",
                ".jpg",
                context.cacheDir
            )
            FileProvider.getUriForFile(
                context,
                "com.example.c_serveur.fileprovider",
                tempFile
            ).also {
                tempImageUri = it
                Log.d(TAG, "Temporary URI created: $it")
            }
        } catch (e: IOException) {
            Log.e(TAG, "Failed to create temp file", e)
            throw IllegalStateException("Could not create temp file", e)
        }
    }

    suspend fun handleNewProductImageCapture(
        imageUri: Uri,
        produit: App_Initialize_Model.Produit_Main_DataBase?
    ) {
        if (isHandlingImage) {
            Log.w(TAG, "Already handling an image capture, skipping")
            return
        }

        isHandlingImage = true
        Log.d(TAG, "Starting image capture for URI: $imageUri")

        try {
            val maxId = appInitializeModel.produit_Main_DataBase.maxOfOrNull { it.id } ?: 2000         
            //TODO(1): fait que ici d evite les produit ou leur id >2000 ajoute au <2000
            val newId = maxId + 1
            Log.d(TAG, "Generated new ID: $newId")

            val fileName = "${newId}_1.jpg"
            val storageRef = Firebase.storage.reference
                .child("$storageBasePath/$fileName")

            // Save locally first
            val localFile = File(BASE_PATH, fileName)
            context.contentResolver.openInputStream(imageUri)?.use { input ->
                localFile.outputStream().use { output ->
                    input.copyTo(output)
                }
                Log.d(TAG, "Saved image locally: ${localFile.absolutePath}")
            }

            // Upload to Firebase
            localFile.inputStream().use { input ->
                storageRef.putStream(input).await()
                Log.d(TAG, "Uploaded to Firebase: $fileName")
            }

            // Create or update product
            val newProduct = createProductEntry(newId, fileName, produit)
            updateDatabase(newProduct, produit)

            Log.d(TAG, "Image handling completed successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to handle image capture", e)
            throw e
        } finally {
            isHandlingImage = false
        }
    }

    private fun createProductEntry(
        newId: Long,
        fileName: String,
        existingProduct: App_Initialize_Model.Produit_Main_DataBase?
    ): App_Initialize_Model.Produit_Main_DataBase {
        return existingProduct?.let {
            App_Initialize_Model.Produit_Main_DataBase(
                id = newId,
                it_ref_Id_don_FireBase = newId,
                it_ref_don_FireBase = fileName,
                init_nom = it.nom,
                init_besoin_To_Be_Updated = true,
                init_it_Image_besoin_To_Be_Updated = true,
                initialNon_Trouve = it.non_Trouve,
                init_colours_Et_Gouts = it.colours_Et_Gouts.toList(),
                initialDemmende_Achate_De_Cette_Produit = it.demmende_Achate_De_Cette_Produit.toList(),
                initialGrossist_Choisi_Pour_Acheter_CeProduit = it.grossist_Choisi_Pour_Acheter_CeProduit.toList()
            )
        } ?: App_Initialize_Model.Produit_Main_DataBase(
            id = newId,
            it_ref_Id_don_FireBase = newId,
            it_ref_don_FireBase = fileName,
            init_besoin_To_Be_Updated = true
        )
    }

    private suspend fun updateDatabase(
        newProduct: App_Initialize_Model.Produit_Main_DataBase,
        oldProduct: App_Initialize_Model.Produit_Main_DataBase?
    ) {
        oldProduct?.let {
            appInitializeModel.produit_Main_DataBase.removeAll { prod -> prod.id == it.id }
            try {
                val oldImageRef = Firebase.storage.reference
                    .child("$storageBasePath/${it.it_ref_don_FireBase}")
                oldImageRef.delete().await()
                Log.d(TAG, "Deleted old image: ${it.it_ref_don_FireBase}")
            } catch (e: Exception) {
                Log.w(TAG, "Failed to delete old image", e)
            }
        }

        appInitializeModel.produit_Main_DataBase.add(newProduct)
        appInitializeModel.update_Produits_FireBase()
        Log.d(TAG, "Database updated with new product: ${newProduct.id}")
    }
}

