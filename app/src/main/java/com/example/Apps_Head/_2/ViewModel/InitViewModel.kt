package com.example.Apps_Head._2.ViewModel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.Apps_Head._1.Model.AppsHeadModel
import com.example.Apps_Head._1.Model.AppsHeadModel.Companion.produitsFireBaseRef
import com.example.Apps_Head._4.Init.initializer
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class InitViewModel : ViewModel() {
    var _appsHeadModel by mutableStateOf(AppsHeadModel())
    val appsHeadModel: AppsHeadModel get() = _appsHeadModel

    var initializationProgress by mutableFloatStateOf(0f)
    var isInitializing by mutableStateOf(false)
    var initializationComplete by mutableStateOf(false)

    private var activeDownloads = mutableMapOf<Long, Job>()
    private val basePath = "/storage/emulated/0/Abdelwahab_jeMla.com/IMGs/BaseDonne"

    init {
        viewModelScope.launch {
            try {
                isInitializing = true
                initializer()
                setupDataListeners()
                initializationComplete = true
            } catch (e: Exception) {
                Log.e("InitViewModel", "Initialization failed", e)
                initializationProgress = 0f
                initializationComplete = false
            } finally {
                isInitializing = false
            }
        }
    }

    fun updateProduct(product: AppsHeadModel.ProduitModel) {
        viewModelScope.launch {
            try {
                produitsFireBaseRef.child(product.id.toString()).setValue(product).await()

                // Update local state
                val index = _appsHeadModel.produitsMainDataBase.indexOfFirst { it.id == product.id }
                if (index != -1) {
                    _appsHeadModel.produitsMainDataBase[index] = product
                }

                Log.d("InitViewModel", "Successfully updated product ${product.id}")
            } catch (e: Exception) {
                Log.e("InitViewModel", "Failed to update product ${product.id}", e)
            }
        }
    }    // Enhanced setupDataListeners function
    private fun setupDataListeners() {
        produitsFireBaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                viewModelScope.launch {
                    snapshot.children.forEach { child ->
                        child.getValue(AppsHeadModel.ProduitModel::class.java)?.let { updatedProduct ->
                            val index = _appsHeadModel.produitsMainDataBase.indexOfFirst { it.id == updatedProduct.id }
                            if (index != -1) {
                                // Preserve local state that shouldn't be overwritten
                                val currentProduct = _appsHeadModel.produitsMainDataBase[index]
                                updatedProduct.statuesBase.imageGlidReloadTigger =
                                    currentProduct.statuesBase.imageGlidReloadTigger
                                _appsHeadModel.produitsMainDataBase[index] = updatedProduct
                            }
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("InitViewModel", "Firebase listener cancelled", error.toException())
            }
        })
    }
}
