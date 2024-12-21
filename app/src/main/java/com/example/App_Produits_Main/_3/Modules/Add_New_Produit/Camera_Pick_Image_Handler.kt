package com.example.App_Produits_Main._3.Modules.Add_New_Produit

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.core.content.FileProvider
import com.example.App_Produits_Main._1.Model.App_Initialize_Model
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.tasks.await
import java.io.File

class CameraPickImageHandler(
    private val context: Context,
    private val appInitializeModel: App_Initialize_Model
) {
    companion object {
        private const val TAG = "CameraPickImageHandler"
    }

    var tempImageUri: Uri? = null
    private var pendingProduct: App_Initialize_Model.Produit_Main_DataBase? = null

    private fun createTempImageUri(): Uri {
        val tempFile = File.createTempFile("temp_image", ".jpg", context.cacheDir)
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            tempFile
        ).also {
            tempImageUri = it
        }
    }

    fun handleNewProductImageCapture(existingProduct: App_Initialize_Model.Produit_Main_DataBase?): Uri {
        pendingProduct = existingProduct
        return createTempImageUri()
    }

    private fun findNextAvailableId(): Number {
        val maxId = appInitializeModel.produits_Main_DataBase
            .filter { it.id < 2000 }
            .maxOfOrNull { it.id } ?: 0

        return if (maxId + 1 < 2000) {
            maxId + 1
        } else {
            val existingIds = appInitializeModel.produits_Main_DataBase
                .filter { it.id < 2000 }
                .map { it.id }
                .toSet()

            (1..2000).firstOrNull { it.toLong() !in existingIds }
                ?: throw IllegalStateException("No available IDs under 2000")
        }
    }

    suspend fun handleImageCaptureResult(imageUri: Uri?) {
        if (imageUri == null) {
            Log.d(TAG, "Image capture cancelled or failed")
            return
        }

        try {
            // Remove the original product if it exists
            pendingProduct?.let { original ->
                appInitializeModel.produits_Main_DataBase.removeAll { it.id == original.id }
                Log.d(TAG, "Removed original product with ID: ${original.id}")
            }

            val newId = findNextAvailableId().toLong()
            val fileName = "${newId}_1.jpg"
            val storageRef = Firebase.storage.reference
                .child("Images Articles Data Base/App_Initialize_Model.Produit_Main_DataBase/$fileName")

            val newProduct = if (pendingProduct != null) {
                App_Initialize_Model.Produit_Main_DataBase(
                    id = newId,
                    it_ref_Id_don_FireBase = newId,
                    it_ref_don_FireBase = fileName,
                    init_nom = pendingProduct!!.nom,
                    init_besoin_To_Be_Updated = true,
                    init_it_Image_besoin_To_Be_Updated = true,
                    initialNon_Trouve = pendingProduct!!.non_Trouve,
                    init_colours_Et_Gouts = pendingProduct!!.colours_Et_Gouts.toList(),
                    initialDemmende_Achate_De_Cette_Produit = pendingProduct!!.demmende_Achate_De_Cette_Produit.toList(),
                    initialGrossist_Choisi_Pour_Acheter_CeProduit = pendingProduct!!.grossist_Choisi_Pour_Acheter_CeProduit.toList()
                )
            } else {
                App_Initialize_Model.Produit_Main_DataBase(
                    id = newId,
                    it_ref_Id_don_FireBase = newId,
                    it_ref_don_FireBase = fileName,
                    init_besoin_To_Be_Updated = true
                )
            }

            appInitializeModel.produits_Main_DataBase.add(newProduct)

            context.contentResolver.openInputStream(imageUri)?.use { inputStream ->
                val bytes = inputStream.readBytes()
                storageRef.putBytes(bytes).await()
            }

            appInitializeModel.update_Produits_FireBase()
            Log.d(TAG, "Successfully created new product with ID: $newId")
            pendingProduct = null

        } catch (e: Exception) {
            Log.e(TAG, "Failed to create new product", e)
            throw e
        }
    }
}
