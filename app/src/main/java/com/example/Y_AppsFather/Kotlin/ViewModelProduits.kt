package com.example.Y_AppsFather.Kotlin

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.Y_AppsFather.Kotlin.ModelAppsFather.Companion.produitsFireBaseRef
import com.example.Y_AppsFather.Kotlin.ModelAppsFather.ProduitModel
import com.example.Z_AppsFather.Kotlin._1.Model.ParamatersAppsModel
import com.example.Z_AppsFather.Kotlin._3.Init.initializer
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ViewModelProduits : ViewModel() {
    var _paramatersAppsViewModelModel by mutableStateOf(ParamatersAppsModel())

    var _modelAppsFather by mutableStateOf(ModelAppsFather())
    val modelAppsFather: ModelAppsFather get() = _modelAppsFather
    val produitsMainDataBase = _modelAppsFather.produitsMainDataBase

    var _produitsAvecBonsGrossist = mutableStateListOf<ProduitModel>()
    val produitsAvecBonsGrossist: List<ProduitModel> get() = _produitsAvecBonsGrossist
    // Add this getter for when we need the mutable list
    val produitsAvecBonsGrossistMutable: SnapshotStateList<ProduitModel> get() = _produitsAvecBonsGrossist


    private fun updateProduitsAvecBonsGrossist() {
        _produitsAvecBonsGrossist.clear()
        _produitsAvecBonsGrossist
            .addAll(produitsMainDataBase
                .filter { it.bonCommendDeCetteCota != null }
                .sortedBy {
                    it.bonCommendDeCetteCota
                        ?.positionProduitDonGrossistChoisiPourAcheterCeProduit
                }
            )
    }

    var initializationProgress by mutableFloatStateOf(0f)
    var isInitializing by mutableStateOf(false)
    var initializationComplete by mutableStateOf(false)

    var isLoading by mutableStateOf(false)
    var loadingProgress by mutableFloatStateOf(0f)

    private var activeDownloads = mutableMapOf<Long, Job>()
    private val basePath = "/storage/emulated/0/Abdelwahab_jeMla.com/IMGs/BaseDonne"

    init {
        viewModelScope.launch {
            try {
                isInitializing = true
                initializer(this@ViewModelProduits,_modelAppsFather, initializationProgress) {
                    { index, ancienData ->
                        initializationProgress =
                            0.1f + (0.8f * (index + 1) / ancienData.produitsDatabase.size)
                    }
                }
                setupDataListeners()
                updateProduitsAvecBonsGrossist()
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
    private fun setupDataListeners() {
        produitsFireBaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                viewModelScope.launch {
                    snapshot.children.forEach { child ->
                        child.getValue(ProduitModel::class.java)
                            ?.let { updatedProduct ->
                                val index = _modelAppsFather.produitsMainDataBase.indexOfFirst { it.id == updatedProduct.id }
                                if (index != -1) {
                                    _modelAppsFather.produitsMainDataBase[index] = updatedProduct
                                    updateProduitsAvecBonsGrossist()
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
