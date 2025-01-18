package Z_MasterOfApps.Z_AppsFather.Kotlin._4.Modules.StorageFireBaseOffline

import Z_MasterOfApps.Kotlin.Model._ModelAppsFather.Companion.imagesProduitsFireBaseStorageRef
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.launch
import java.io.File

private suspend fun ViewModelInitApp.initializeProductImages() {
        _modelAppsFather.produitsMainDataBase.forEachIndexed { index, produit ->
            loadingProgress = index.toFloat() / _modelAppsFather.produitsMainDataBase.size

            val imageRef = imagesProduitsFireBaseStorageRef.child("${produit.id}_1.jpg")
            val localFile = File(
                "/storage/emulated/0/Abdelwahab_jeMla.com/IMGs/BaseDonne/${produit.id}_1.jpg"
            )

            handleImageWithOfflineSupport(imageRef, localFile)
        }
        loadingProgress = 1f
    }

    suspend fun ViewModelInitApp.handleImageWithOfflineSupport(imageRef: StorageReference, localFile: File) {
        viewModelScope.launch {
            try {
                when (val result = imageHandler.handleImage(imageRef, localFile)) {
                    is ImageHandlerResult.Success -> {
                        Log.d("ViewModelInitApp",
                            if (result.isFromCache) "Using cached image: ${localFile.path}"
                            else "Using fresh download: ${localFile.path}"
                        )
                    }
                    is ImageHandlerResult.Error -> {
                        Log.e("ViewModelInitApp", "Image handling failed", result.exception)
                    }
                }

                when (val uploadResult = imageHandler.uploadImage(imageRef, localFile)) {
                    is ImageHandlerResult.Success -> {
                        Log.d("ViewModelInitApp",
                            if (uploadResult.isFromCache) "Image queued for upload: ${localFile.path}"
                            else "Image uploaded successfully: ${localFile.path}"
                        )
                    }
                    is ImageHandlerResult.Error -> {
                        Log.e("ViewModelInitApp", "Image upload failed", uploadResult.exception)
                    }
                }
            } catch (e: Exception) {
                Log.e("ViewModelInitApp", "Error in handleImageWithOfflineSupport", e)
            }
        }
    }
