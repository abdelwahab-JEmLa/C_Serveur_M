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
    }

    var tempImageUri: Uri? = null

    fun createTempImageUri(): Uri {
        return try {
            val tempFile = File.createTempFile(
                "temp_image",
                ".jpg",
                context.cacheDir
            )
            FileProvider.getUriForFile(
                context,
                "com.example.c_serveur.fileprovider",
                tempFile
            ).also { tempImageUri = it }
        } catch (e: IOException) {
            throw IllegalStateException("Could not create temp file", e)
        } catch (e: IllegalArgumentException) {
            throw IllegalStateException("Could not create URI for file", e)
        }
    }

    suspend fun handleNewProductImageCapture(
        imageUri: Uri,
        produit: App_Initialize_Model.Produit_Main_DataBase?
    ) {
        try {
            // Get max ID from existing products
            val maxId = appInitializeModel.produit_Main_DataBase.filter { it.id>2000 }.maxOfOrNull { it.id } ?: 0
            val newId = maxId + 1

            // Upload image to Firebase Storage
            val fileName = "${newId}_1.jpg"
            val storageRef = Firebase.storage.reference
                .child("Images Articles Data Base/App_Initialize_Model.Produit_Main_DataBase/$fileName")

            // Create new product using existing product data or default values
            val newProduct = if (produit != null) {
                App_Initialize_Model.Produit_Main_DataBase(
                    id = newId,
                    it_ref_Id_don_FireBase = newId,
                    it_ref_don_FireBase = fileName,
                    init_nom = produit.nom,
                    init_besoin_To_Be_Updated = true,
                    init_it_Image_besoin_To_Be_Updated = true,
                    initialNon_Trouve = produit.non_Trouve,
                    init_colours_Et_Gouts = produit.colours_Et_Gouts.toList(),
                    initialDemmende_Achate_De_Cette_Produit = produit.demmende_Achate_De_Cette_Produit.toList(),
                    initialGrossist_Choisi_Pour_Acheter_CeProduit = produit.grossist_Choisi_Pour_Acheter_CeProduit.toList()
                )
            } else {
                App_Initialize_Model.Produit_Main_DataBase(
                    id = newId,
                    it_ref_Id_don_FireBase = newId,
                    it_ref_don_FireBase = fileName,
                    init_besoin_To_Be_Updated = true
                )
            }

            // Add to database
            appInitializeModel.produit_Main_DataBase.add(newProduct)

            // Delete the original product if it exists
            produit?.let { originalProduct ->
                appInitializeModel.produit_Main_DataBase.removeAll { it.id == originalProduct.id }
                // Optionally delete the old image from Firebase Storage
                try {
                    val oldImageRef = Firebase.storage.reference
                        .child("Images Articles Data Base/App_Initialize_Model.Produit_Main_DataBase/${originalProduct.it_ref_don_FireBase}")
                    oldImageRef.delete().await()
                } catch (e: Exception) {
                    Log.w(TAG, "Failed to delete old image from storage", e)
                    // Continue execution even if old image deletion fails
                }
            }

            // Upload image
            context.contentResolver.openInputStream(imageUri)?.use { inputStream ->
                val bytes = inputStream.readBytes()
                storageRef.putBytes(bytes).await()
            }

            // Update Firebase
            appInitializeModel.update_Produits_FireBase()

            Log.d(TAG, "Successfully created new product with ID: $newId")

        } catch (e: Exception) {
            Log.e(TAG, "Failed to create new product", e)
            throw e
        }
    }
}
