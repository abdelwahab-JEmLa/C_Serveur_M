package Z_MasterOfApps.Z_AppsFather.Kotlin._4.Modules.StorageFireBaseOffline

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.tasks.await
import java.io.File

object FirebaseStorageOfflineHandler {
    private const val TAG = "FirebaseStorageOffline"

    data class UploadResult(
        val isOffline: Boolean = false,
        val uploadPath: String = "",
        val downloadUrl: String = ""
    )

    suspend fun uploadFileWithOfflineQueue(
        storageRef: StorageReference,
        localFile: File,
        context: Context
    ): Result<UploadResult> = try {
        if (!isOnline(context)) {
            saveToUploadQueue(localFile, storageRef.path)
            Result.success(UploadResult(isOffline = true, uploadPath = storageRef.path))
        } else {
            val uploadTask = storageRef.putFile(localFile.toUri()).await()
            val downloadUrl = uploadTask.storage.downloadUrl.await().toString()
            Result.success(UploadResult(
                isOffline = false,
                uploadPath = storageRef.path,
                downloadUrl = downloadUrl
            ))
        }
    } catch (e: Exception) {
        Log.e(TAG, "Failed to upload file", e)
        Result.failure(e)
    }
    fun initializeStorageCache() {
        try {
            FirebaseStorage.getInstance().apply {
                maxDownloadRetryTimeMillis = 10000
                maxOperationRetryTimeMillis = 10000
                maxUploadRetryTimeMillis = 10000
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize Firebase Storage cache", e)
        }
    }

    suspend fun downloadAndCacheFile(
        storageRef: StorageReference,
        localFile: File,
        context: Context
    ): Result<File> = try {
        // Check if file exists in local cache
        if (localFile.exists()) {
            // Verify if we need to update from server
            val metadata = storageRef.metadata.await()
            val localLastModified = localFile.lastModified()
            
            if (metadata.updatedTimeMillis > localLastModified) {
                // File on server is newer, download it
                downloadFromServer(storageRef, localFile)
            }
        } else {
            // File doesn't exist locally, download it
            downloadFromServer(storageRef, localFile)
        }
        
        Result.success(localFile)
    } catch (e: Exception) {
        Log.e(TAG, "Failed to download/cache file", e)
        Result.failure(e)
    }

    private suspend fun downloadFromServer(
        storageRef: StorageReference,
        localFile: File
    ) {
        storageRef.getFile(localFile).await()
    }


    suspend fun processUploadQueue(context: Context) {
        // Implement queue processing
        // Upload files that were queued while offline
    }

    private fun saveToUploadQueue(file: File, storagePath: String) {
        // Implementation de la queue avec Room ou SharedPreferences
    }

    private fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)
        return capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
    }
}
// Usage example:
class YourViewModel(private val context: Context) : ViewModel() {
    suspend fun handleImageWithOfflineSupport(imageRef: StorageReference, localFile: File) {
        // Download and cache
        FirebaseStorageOfflineHandler.downloadAndCacheFile(imageRef, localFile, context)
            .onSuccess { cachedFile ->
                // Use cached file
            }
            .onFailure { error ->
                // Handle error
            }

        // Upload with offline support
        FirebaseStorageOfflineHandler.uploadFileWithOfflineQueue(imageRef, localFile, context)
            .onSuccess { 
                // Handle successful upload
            }
            .onFailure { error ->
                // Handle error
            }
    }
}
