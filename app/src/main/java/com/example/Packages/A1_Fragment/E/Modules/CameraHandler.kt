// CameraHandler.kt
package com.example.Packages.A1_Fragment.E.Modules

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.Apps_Head._1.Model.AppsHeadModel
import com.example.Apps_Head._1.Model.AppsHeadModel.Companion.imagesProduitsFireBaseStorageRef
import com.example.Apps_Head._1.Model.AppsHeadModel.Companion.imagesProduitsLocalExternalStorageBasePath
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class CameraHandler(private val context: Context) {
    private var tempImageUri: Uri? = null
    private var pendingProduct: AppsHeadModel.ProduitModel? = null

    suspend fun handleImageCapture(uri: Uri) {
        try {
            if (uri.toString().isEmpty()) {
                throw IllegalArgumentException("Invalid URI")
            }

            pendingProduct?.let { product ->
                val fileName = "${product.id}_1.jpg"
                val localStorageDir = File(imagesProduitsLocalExternalStorageBasePath).apply {
                    if (!exists()) mkdirs()
                }

                val localFile = File(localStorageDir, fileName)
                var uploadSuccess = false

                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    val imageBytes = inputStream.readBytes()

                    try {
                        // Save to local storage
                        withContext(Dispatchers.IO) {
                            FileOutputStream(localFile).use { output ->
                                output.write(imageBytes)
                            }
                        }

                        // Upload to Firebase Storage
                        val uploadTask = imagesProduitsFireBaseStorageRef
                            .child(fileName)
                            .putBytes(imageBytes)
                            .await()

                        // Verify upload was successful
                        if (uploadTask.metadata != null) {
                            uploadSuccess = true
                        }

                        // Only update product status if both operations succeeded
                        if (uploadSuccess && localFile.exists() && localFile.length() > 0) {
                            // Update product status
                            product.apply {
                                statuesBase.apply {
                                    prePourCameraCapture = false
                                    naAucunImage = false
                                    imageGlidReloadTigger += 1
                                }
                                besoinToBeUpdated = true
                            }

                            // Update in Firebase Realtime Database
                            AppsHeadModel.produitsFireBaseRef
                                .child(product.id.toString())
                                .setValue(product)
                                .await()

                            withContext(Dispatchers.Main) {
                                Toast.makeText(context, "Image uploaded successfully", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            throw IOException("Upload verification failed")
                        }
                    } catch (e: Exception) {
                        // Clean up local file if upload failed
                        if (localFile.exists() && !uploadSuccess) {
                            localFile.delete()
                        }
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "Failed to upload image: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                        throw e
                    }
                } ?: throw IllegalStateException("Could not open input stream for image URI")

            } ?: throw IllegalStateException("No pending product found")

        } catch (e: Exception) {
            Log.e("ImageUpload", "Failed to handle image capture", e)
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Error handling image: ${e.message}", Toast.LENGTH_LONG).show()
            }
        } finally {
            pendingProduct = null
            tempImageUri = null
        }
    }

    fun createTempImageUri(): Uri? {
        return try {
            val tempFile = File.createTempFile("temp_image", ".jpg", context.cacheDir).apply {
                deleteOnExit()
            }
            FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                tempFile
            ).also { tempImageUri = it }
        } catch (e: IOException) {
            Log.e("ImageCapture", "Failed to create temp file", e)
            null
        }
    }

    fun checkAndRequestPermissions(
        permissionLauncher: ActivityResultLauncher<Array<String>>,
        cameraLauncher: ActivityResultLauncher<Uri>,
        appsHeadModel: AppsHeadModel
    ) {
        val permissions = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        val hasPermissions = permissions.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }

        if (!hasPermissions) {
            permissionLauncher.launch(permissions)
        } else {
            val productForCapture = appsHeadModel.produitsMainDataBase
                .find { it.statuesBase.prePourCameraCapture }

            if (productForCapture != null) {
                pendingProduct = productForCapture
                createTempImageUri()?.let { uri ->
                    cameraLauncher.launch(uri)
                }
            }
        }
    }

    fun getPendingProduct(): AppsHeadModel.ProduitModel? = pendingProduct
    fun getTempImageUri(): Uri? = tempImageUri
}
