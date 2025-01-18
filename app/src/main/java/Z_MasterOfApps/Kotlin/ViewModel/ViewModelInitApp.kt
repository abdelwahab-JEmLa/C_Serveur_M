package Z_MasterOfApps.Kotlin.ViewModel

import Z_MasterOfApps.Kotlin.Model._ModelAppsFather
import Z_MasterOfApps.Kotlin.Model._ModelAppsFather.Companion.imagesProduitsFireBaseStorageRef
import Z_MasterOfApps.Kotlin.Model._ModelAppsFather.Companion.produitsFireBaseRef
import Z_MasterOfApps.Z_AppsFather.Kotlin._1.Model.ParamatersAppsModel
import Z_MasterOfApps.Z_AppsFather.Kotlin._3.Init.CreeNewStart
import Z_MasterOfApps.Z_AppsFather.Kotlin._3.Init.LoadFireBase.LoadFromFirebaseProduits
import Z_MasterOfApps.Z_AppsFather.Kotlin._4.Modules.StorageFireBaseOffline.FirebaseStorageOfflineHandler
import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.launch
import java.io.File
import android.content.Context

@SuppressLint("SuspiciousIndentation")
class ViewModelInitApp(context: Context) : ViewModel() {
    var _paramatersAppsViewModelModel by mutableStateOf(ParamatersAppsModel())
    var _modelAppsFather by mutableStateOf(_ModelAppsFather())

    val modelAppsFather: _ModelAppsFather get() = _modelAppsFather
    val produitsMainDataBase = _modelAppsFather.produitsMainDataBase

    var isLoading by mutableStateOf(false)
    var loadingProgress by mutableFloatStateOf(0f)

    init {
        viewModelScope.launch {
            try {
                isLoading = true
                val nombre = 0
                if (nombre == 0) {
                    LoadFromFirebaseProduits.loadFromFirebase(this@ViewModelInitApp)
                 //   loadCalculateurOktapuluse(this@ViewModelInitApp)
                }
                else
                CreeNewStart(_modelAppsFather)

                setupDataListeners()

                isLoading = true
            } catch (e: Exception) {
                Log.e("ViewModelInitApp", "Init failed", e)
            } finally {
                isLoading = false
            }
        }
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
                                       _modelAppsFather.produitsMainDataBase[index] =updatedProduct
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
    // État pour gérer les uploads
    private var pendingUploads = mutableStateListOf<String>()
    var isUploading by mutableStateOf(false)
    var uploadProgress by mutableFloatStateOf(0f)

    // Fonction pour uploader une image de produit
    suspend fun uploadProductImage(productId: Long, imageFile: File): Result<String> {
        val storageRef = imagesProduitsFireBaseStorageRef
            .child(productId.toString())
            .child("main_image.jpg")

        return try {
            isUploading = true

            FirebaseStorageOfflineHandler.uploadFileWithOfflineQueue(
                storageRef = storageRef,
                localFile = imageFile,
                context = context   //->
                //TODO(FIXME):Fix erreur Unresolved reference: context
            ).map { result ->
                if (result.isOffline) {
                    pendingUploads.add(imageFile.path)
                    "" // Return empty URL for offline case
                } else {
                    result.downloadUrl
                }
            }.also {
                isUploading = false
                uploadProgress = 0f
            }
        } catch (e: Exception) {
            Log.e("ViewModelInitApp", "Failed to upload image", e)
            isUploading = false
            uploadProgress = 0f
            Result.failure(e)
        }
    }

    // Fonction pour retenter les uploads en attente
    fun retryPendingUploads() {
        viewModelScope.launch {
            pendingUploads.toList().forEach { filePath ->
                val file = File(filePath)
                if (file.exists()) {
                    // Implement retry logic here
                    pendingUploads.remove(filePath)
                }
            }
        }
    }
}

