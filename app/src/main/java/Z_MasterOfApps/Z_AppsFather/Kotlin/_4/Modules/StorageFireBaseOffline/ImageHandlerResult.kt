package Z_MasterOfApps.Z_AppsFather.Kotlin._4.Modules.StorageFireBaseOffline

import Z_MasterOfApps.Z_AppsFather.Kotlin._4.Modules.StorageFireBaseOffline.FirebaseStorageOfflineHandler
import android.content.Context
import android.util.Log
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.withTimeout
import java.io.File

sealed class ImageHandlerResult {
    data class Success(val file: File, val isFromCache: Boolean = false) : ImageHandlerResult()
    data class Error(val exception: Exception, val message: String) : ImageHandlerResult()
}

class ImageHandler(private val appContext: Context) {
    companion object {
        private const val TIMEOUT_MILLIS = 30000L // 30 seconds timeout
        private const val TAG = "ImageHandler"
    }

    suspend fun handleImage(imageRef: StorageReference, localFile: File): ImageHandlerResult {
        return try {
            withTimeout(TIMEOUT_MILLIS) {
                // First try to get from cache/download
                val downloadResult = FirebaseStorageOfflineHandler.downloadAndCacheFile(
                    imageRef,
                    localFile,
                    appContext
                )

                when {
                    downloadResult.isSuccess -> {
                        // If we have the image, either from cache or fresh download, return success
                        ImageHandlerResult.Success(
                            downloadResult.getOrNull()!!,
                            localFile.exists()
                        )
                    }
                    downloadResult.isFailure -> {
                        // If download failed but we have a cached version, use that
                        if (localFile.exists()) {
                            ImageHandlerResult.Success(localFile, true)
                        } else {
                            throw downloadResult.exceptionOrNull()!!
                        }
                    }
                    else -> throw IllegalStateException("Unexpected download result state")
                }
            }
        } catch (e: Exception) {
            when (e) {
                is CancellationException -> throw e // Don't catch cancellation exceptions
                else -> {
                    Log.e(TAG, "Error handling image", e)
                    ImageHandlerResult.Error(
                        e,
                        "Failed to handle image: ${e.localizedMessage}"
                    )
                }
            }
        }
    }

    suspend fun uploadImage(imageRef: StorageReference, localFile: File): ImageHandlerResult {
        return try {
            withTimeout(TIMEOUT_MILLIS) {
                val uploadResult = FirebaseStorageOfflineHandler.uploadFileWithOfflineQueue(
                    imageRef,
                    localFile,
                    appContext
                )

                when {
                    uploadResult.isSuccess -> {
                        val result = uploadResult.getOrNull()!!
                        if (result.isOffline) {
                            Log.d(TAG, "Image queued for upload: ${result.uploadPath}")
                        } else {
                            Log.d(TAG, "Image uploaded successfully: ${result.downloadUrl}")
                        }
                        ImageHandlerResult.Success(localFile, result.isOffline)
                    }
                    else -> throw uploadResult.exceptionOrNull()!!
                }
            }
        } catch (e: Exception) {
            when (e) {
                is CancellationException -> throw e
                else -> {
                    Log.e(TAG, "Error uploading image", e)
                    ImageHandlerResult.Error(
                        e,
                        "Failed to upload image: ${e.localizedMessage}"
                    )
                }
            }
        }
    }
}
