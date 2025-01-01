package com.example.Apps_Head._3.Modules.Add_New_Produit

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.core.content.FileProvider
import com.example.Apps_Head._1.Model.AppsHeadModel
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.tasks.await
import java.io.File

class CameraPickImageHandler(
    private val context: Context,
    private val appInitializeModel: AppsHeadModel
) {
    companion object {
        private const val TAG = "CameraPickImageHandler"
    }

    var tempImageUri: Uri? = null
    private var pendingProduct: AppsHeadModel.ProduitModel? = null

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

    fun handleNewProductImageCapture(existingProduct: AppsHeadModel.ProduitModel?): Uri {
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
                .child("Images Articles Data Base/AppsHeadModel.Produit_Main_DataBase/$fileName")

            val newProduct = if (pendingProduct != null) {
                AppsHeadModel.ProduitModel(
                    id = newId,
                    it_ref_Id_don_FireBase = newId,
                    it_ref_don_FireBase = fileName,
                    init_nom = pendingProduct!!.nom,
                    init_besoin_To_Be_Updated = true,
                    init_it_Image_besoin_To_Be_Updated = true,
                    initialNon_Trouve = pendingProduct!!.non_Trouve,
                    init_colours_Et_Gouts = pendingProduct!!.coloursEtGouts.toList(),
                    init_historiqueBonsCommend = pendingProduct!!.historiqueBonsCommend.toList()
                )
            } else {
                AppsHeadModel.ProduitModel(
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

            appInitializeModel.updateProduitsFireBase()
            Log.d(TAG, "Successfully created new product with ID: $newId")
            pendingProduct = null

        } catch (e: Exception) {
            Log.e(TAG, "Failed to create new product", e)
            throw e
        }
    }
}
