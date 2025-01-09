package com.example.Z_AppsFather.Kotlin._2.ViewModel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.Z_AppsFather.Kotlin._1.Model.ModelAppsFather
import com.example.Z_AppsFather.Kotlin._1.Model.ModelAppsFather.Companion.produitsFireBaseRef
import com.example.Z_AppsFather.Kotlin._3.Init.initializer
import com.example.c_serveur.Archives.A3_DiviseProduitsAuCamionFragment.D.Actions.onClickOn_Fragment_3
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ViewModelProduits : ViewModel() {
    var _appsHeadModel by mutableStateOf(ModelAppsFather())
    val appsHeadModel: ModelAppsFather get() = _appsHeadModel

    val onClickOn_Fragment_3 = onClickOn_Fragment_3(this@ViewModelProduits)

    var initializationProgress by mutableFloatStateOf(0f)
    var isInitializing by mutableStateOf(false)
    var initializationComplete by mutableStateOf(false)


    private var activeDownloads = mutableMapOf<Long, Job>()
    private val basePath = "/storage/emulated/0/Abdelwahab_jeMla.com/IMGs/BaseDonne"

    init {
        viewModelScope.launch {
            try {
                isInitializing = true
                initializer(_appsHeadModel, initializationProgress) {
                    { index, ancienData ->
                        initializationProgress=  0.1f + (0.8f * (index + 1) / ancienData.produitsDatabase.size)
                    }
                }
                setupDataListeners()
                initializationComplete = true
            } catch (e: Exception) {
                Log.e("ViewModelProduits", "Initialization failed", e)
                initializationProgress = 0f
                initializationComplete = false
            } finally {
                isInitializing = false
            }
        }
    }

    fun updateProduct(product: ModelAppsFather.ProduitModel) {
        viewModelScope.launch {
            try {
                produitsFireBaseRef.child(product.id.toString()).setValue(product).await()

                // Update local state
                val index = _appsHeadModel.produitsMainDataBase.indexOfFirst { it.id == product.id }
                if (index != -1) {
                    _appsHeadModel.produitsMainDataBase[index] = product
                }

                Log.d("ViewModelProduits", "Successfully updated product ${product.id}")
            } catch (e: Exception) {
                Log.e("ViewModelProduits", "Failed to update product ${product.id}", e)
            }
        }
    }    // Enhanced setupDataListeners function
    private fun setupDataListeners() {
        produitsFireBaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                viewModelScope.launch {
                    snapshot.children.forEach { child ->
                        child.getValue(ModelAppsFather.ProduitModel::class.java)?.let { updatedProduct ->
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
                Log.e("ViewModelProduits", "Firebase listener cancelled", error.toException())
            }
        })
    }
}
