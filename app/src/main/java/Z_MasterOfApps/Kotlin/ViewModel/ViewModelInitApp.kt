package Z_MasterOfApps.Kotlin.ViewModel

import Z_MasterOfApps.Kotlin.Model._ModelAppsFather
import Z_MasterOfApps.Kotlin.Model._ModelAppsFather.Companion.imagesProduitsFireBaseStorageRef
import Z_MasterOfApps.Kotlin.Model._ModelAppsFather.Companion.produitsFireBaseRef
import Z_MasterOfApps.Z_AppsFather.Kotlin._1.Model.ParamatersAppsModel
import Z_MasterOfApps.Z_AppsFather.Kotlin._3.Init.CreeNewStart
import Z_MasterOfApps.Z_AppsFather.Kotlin._3.Init.LoadFireBase.LoadFromFirebaseProduits
import Z_MasterOfApps.Z_AppsFather.Kotlin._4.Modules.StorageFireBaseOffline.ImageHandler
import Z_MasterOfApps.Z_AppsFather.Kotlin._4.Modules.StorageFireBaseOffline.ImageHandlerResult
import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.launch
import java.io.File

@SuppressLint("SuspiciousIndentation")
class ViewModelInitApp(private val appContext: Context) : ViewModel() {
    var _paramatersAppsViewModelModel by mutableStateOf(ParamatersAppsModel())
    var _modelAppsFather by mutableStateOf(_ModelAppsFather())

    val modelAppsFather: _ModelAppsFather get() = _modelAppsFather
    val produitsMainDataBase = _modelAppsFather.produitsMainDataBase

    var isLoading by mutableStateOf(false)
    var loadingProgress by mutableFloatStateOf(0f)

    private val imageHandler by lazy { ImageHandler(appContext) }

    init {
        viewModelScope.launch {
            try {
                isLoading = true
                val nombre = 0
                if (nombre == 0) {
                    LoadFromFirebaseProduits.loadFromFirebase(this@ViewModelInitApp)
                    initializeProductImages()
                } else {
                    CreeNewStart(_modelAppsFather)
                }

                setupDataListeners()
                isLoading = false
            } catch (e: Exception) {
                Log.e("ViewModelInitApp", "Init failed", e)
                isLoading = false
            }
        }
    }

    private suspend fun initializeProductImages() {
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

    private fun setupDataListeners() {
        _modelAppsFather.produitsMainDataBase.forEach { produit ->
            Log.d("SetupListener", "Setting up listener for product ${produit.id}")
            produitsFireBaseRef.child(produit.id.toString())
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        viewModelScope.launch {
                            try {
                                val updatedProduct = LoadFromFirebaseProduits.parseProduct(snapshot)
                                if (updatedProduct != null) {
                                    val index = _modelAppsFather.produitsMainDataBase.indexOfFirst {
                                        it.id == updatedProduct.id
                                    }
                                    if (index != -1) {
                                        _modelAppsFather.produitsMainDataBase[index] = updatedProduct
                                    }
                                }
                            } catch (e: Exception) {
                                Log.e("SetupListener", "Error updating product", e)
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e("SetupListener", "Database error: ${error.message}")
                    }
                })
        }
    }

    suspend fun handleImageWithOfflineSupport(imageRef: StorageReference, localFile: File) {
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
}
