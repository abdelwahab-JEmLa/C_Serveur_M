package com.example.c_serveur.Modules

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.core.content.FileProvider
import com.example.c_serveur.ViewModel.Model.App_Initialize_Model
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

class CameraPickImageHandler(
    private val context: Context,
    private val appInitializeModel: App_Initialize_Model
) {
    companion object {
        private const val TAG = "CameraPickImageHandler"
        private const val BUFFER_SIZE = 8192
        private const val MAX_ID_THRESHOLD = 2000
        private const val MAX_UPLOAD_RETRIES = 3
        private const val STORAGE_BASE_PATH = "Images Articles Data Base/App_Initialize_Model.Produit_Main_DataBase"
    }

    private val basePath by lazy {
        File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "BaseDonne").apply {
            if (!exists()) mkdirs()
        }.absolutePath
    }

    var tempImageUri: Uri? = null
    private var isHandlingImage = false

    init {
        Log.d(TAG, "Initializing CameraPickImageHandler")
        createBasePath()
    }

    private fun createBasePath() {
        val baseDir = File(basePath)
        if (!baseDir.exists()) {
            val created = baseDir.mkdirs()
            Log.d(TAG, "Base directory creation ${if (created) "successful" else "failed"}: $basePath")
            if (!created) {
                Log.e(TAG, "Failed to create directory: $basePath")
                throw IOException("Failed to create base directory: $basePath")
            }
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

    private fun findNextAvailableId(): Number {
        val maxId = appInitializeModel.produit_Main_DataBase
            .filter { it.id < MAX_ID_THRESHOLD }
            .maxOfOrNull { it.id } ?: 0

        return if (maxId + 1 < MAX_ID_THRESHOLD) {
            maxId + 1
        } else {
            val existingIds = appInitializeModel.produit_Main_DataBase
                .filter { it.id < MAX_ID_THRESHOLD }
                .map { it.id }
                .toSet()

            (1..MAX_ID_THRESHOLD).firstOrNull { it.toLong() !in existingIds }
                ?: throw IllegalStateException("No available IDs under $MAX_ID_THRESHOLD")
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
        var localFile: File? = null

        try {
            validateInputUri(imageUri)
            val newId = findNextAvailableId()
            val fileName = "${newId}_1.jpg"
            localFile = createLocalFile(fileName)

            copyImageToLocal(imageUri, localFile)
            val downloadUrl = uploadToFirebase(localFile, fileName)

            val newProduct = createProductEntry(newId.toLong(), fileName, produit)
            updateDatabase(newProduct)

            Log.d(TAG, "Image handling completed successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to handle image capture", e)
            Log.e(TAG, "Error details: ${e.message}")
            e.printStackTrace()
            localFile?.delete()
            throw e
        } finally {
            isHandlingImage = false
        }
    }

    private suspend fun validateInputUri(imageUri: Uri) {
        Log.d(TAG, "Validating input URI: $imageUri")
        val inputSize = context.contentResolver.openInputStream(imageUri)?.use {
            it.available().also { size ->
                Log.d(TAG, "Input stream size: $size bytes")
                if (size == 0) throw IOException("Input stream is empty")
            }
        } ?: throw IOException("Cannot open input stream for URI: $imageUri")
    }

    private fun createLocalFile(fileName: String): File {
        val file = File(basePath, fileName)
        Log.d(TAG, "Creating local file at: ${file.absolutePath}")

        file.parentFile?.let { parent ->
            if (!parent.exists() && !parent.mkdirs()) {
                throw IOException("Failed to create parent directories for ${file.absolutePath}")
            }
        }
        return file
    }

    private suspend fun copyImageToLocal(sourceUri: Uri, destinationFile: File) {
        var totalBytesCopied = 0L
        context.contentResolver.openInputStream(sourceUri)?.use { input ->
            BufferedInputStream(input, BUFFER_SIZE).use { bufferedInput ->
                destinationFile.outputStream().use { output ->
                    BufferedOutputStream(output, BUFFER_SIZE).use { bufferedOutput ->
                        val buffer = ByteArray(BUFFER_SIZE)
                        var bytes: Int
                        var lastLogged = 0L

                        while (bufferedInput.read(buffer).also { bytes = it } != -1) {
                            bufferedOutput.write(buffer, 0, bytes)
                            totalBytesCopied += bytes

                            if (totalBytesCopied - lastLogged > 102400) {
                                Log.d(TAG, "Copied $totalBytesCopied bytes")
                                lastLogged = totalBytesCopied
                            }
                        }
                        bufferedOutput.flush()
                    }
                }
            }
        } ?: throw IOException("Failed to open input stream for copying")

        if (totalBytesCopied == 0L || !destinationFile.exists() || destinationFile.length() == 0L) {
            throw IOException("File copy failed or resulted in empty file")
        }

        Log.d(TAG, "Successfully copied $totalBytesCopied bytes to local file")
    }

    private suspend fun uploadToFirebase(localFile: File, fileName: String): String = withContext(Dispatchers.IO) {
        val storageRef = Firebase.storage.reference.child("$STORAGE_BASE_PATH/$fileName")
        var lastException: Exception? = null

        repeat(MAX_UPLOAD_RETRIES) { attempt ->
            try {
                return@withContext performUpload(localFile, storageRef)
            } catch (e: Exception) {
                lastException = e
                if (attempt < MAX_UPLOAD_RETRIES - 1) {
                    Log.w(TAG, "Upload attempt ${attempt + 1} failed, retrying...", e)
                    delay(1000L * (attempt + 1))
                }
            }
        }

        throw lastException ?: IOException("Upload failed after $MAX_UPLOAD_RETRIES attempts")
    }

    private suspend fun performUpload(localFile: File, storageRef: StorageReference): String {
        val uploadTask = localFile.inputStream().use { input ->
            storageRef.putStream(input)
        }

        uploadTask.addOnProgressListener { taskSnapshot ->
            val progress = (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount)
            Log.d(TAG, "Upload progress: $progress% (${taskSnapshot.bytesTransferred}/${taskSnapshot.totalByteCount} bytes)")
        }

        uploadTask.await()
        return storageRef.downloadUrl.await().toString()
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

    private suspend fun updateDatabase(newProduct: App_Initialize_Model.Produit_Main_DataBase) {
        appInitializeModel.produit_Main_DataBase.add(newProduct)
        appInitializeModel.update_Produits_FireBase()
        Log.d(TAG, "Database updated with new product: ${newProduct.id}")
    }
}
