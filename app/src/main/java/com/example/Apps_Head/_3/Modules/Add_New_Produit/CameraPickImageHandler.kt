package com.example.Apps_Head._3.Modules.Add_New_Produit

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.core.content.FileProvider
import com.example.Apps_Head._1.Model.AppsHeadModel
import com.example.Apps_Head._1.Model.AppsHeadModel.Companion.updateProduitsFireBase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.tasks.await
import java.io.File

class CameraPickImageHandler(
    private val context: Context,
    private val appsHeadModel: AppsHeadModel
) {
    companion object {
        private const val TAG = "CameraPickImageHandler"
        private const val ID_THRESHOLD = 2000L // Changed to Long
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

    private fun generateNewProductId(): Long {
        // Get the maximum ID from all products, regardless of their ID range
        val maxExistingId = appsHeadModel.produitsMainDataBase
            .maxOfOrNull { it.id } ?: 0L

        // If we're in the lower range (< 2000), stay in that range
        return if (maxExistingId < ID_THRESHOLD) {
            maxExistingId + 1
        } else {
            // Find the next available ID in the lower range
            val usedIds = appsHeadModel.produitsMainDataBase
                .filter { it.id < ID_THRESHOLD }
                .map { it.id }
                .toSet()

            // Find first available ID in the lower range, ensuring Long values
            (1L..ID_THRESHOLD).first { it !in usedIds }
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
                appsHeadModel.produitsMainDataBase.removeAll { it.id == original.id }
                Log.d(TAG, "Removed original product with ID: ${original.id}")
            }

            val newId = generateNewProductId()
            val fileName = "${newId}_1.jpg"
            val storageRef = Firebase.storage.reference
                .child("Images Articles Data Base/AppsHeadModel.Produit_Main_DataBase/$fileName")

            val newProduct = if (pendingProduct != null) {
                AppsHeadModel.ProduitModel(
                    id = newId,
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
                    init_besoin_To_Be_Updated = true
                )
            }

            appsHeadModel.produitsMainDataBase.add(newProduct)

            context.contentResolver.openInputStream(imageUri)?.use { inputStream ->
                val bytes = inputStream.readBytes()
                storageRef.putBytes(bytes).await()
            }

            appsHeadModel.produitsMainDataBase.updateProduitsFireBase()
            Log.d(TAG, "Successfully created new product with ID: $newId")
            pendingProduct = null

        } catch (e: Exception) {
            Log.e(TAG, "Failed to create new product", e)
            throw e
        }
    }
}
