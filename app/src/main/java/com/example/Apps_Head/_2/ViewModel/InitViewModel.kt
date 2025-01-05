package com.example.Apps_Head._2.ViewModel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.Apps_Head._1.Model.AppsHeadModel
import com.example.Apps_Head._1.Model.AppsHeadModel.Companion.updateProduitsFireBase
import com.example.Apps_Head._4.Init.initializer
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.storage.storage
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.io.File

class InitViewModel : ViewModel() {
    var _appsHeadModel by mutableStateOf(AppsHeadModel())
    val appsHead: AppsHeadModel get() = _appsHeadModel

    var initializationProgress by mutableFloatStateOf(0f)
    var isInitializing by mutableStateOf(false)
    var initializationComplete by mutableStateOf(false)

    private val _positionChangeFlow = MutableSharedFlow<Pair<Long, Int>>()
    val positionChangeFlow = _positionChangeFlow.asSharedFlow()

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

    private fun setupDataListeners() {
        // Listen for image changes
        Firebase.database.getReference("Images Articles Data Base/AppsHeadModel.Produit_Main_DataBase")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach { imageData ->
                        val fileName = imageData.key ?: return@forEach
                        val productId = fileName.split("_")[0].toLongOrNull() ?: return@forEach
                        val product = _appsHeadModel.produitsMainDataBase.find { it.id == productId } ?: return@forEach

                        if (activeDownloads[productId]?.isActive == true) return@forEach

                        val localFile = File("$basePath/$fileName")
                        if (!localFile.exists() || product.statuesBase.naAucunImage == true) {
                            activeDownloads[productId] = viewModelScope.launch {
                                try {
                                    File(basePath).mkdirs()
                                    Firebase.storage.reference
                                        .child("Images Articles Data Base/AppsHeadModel.Produit_Main_DataBase/$fileName")
                                        .getFile(localFile)
                                        .addOnSuccessListener {
                                            product.apply {
                                                statuesBase.naAucunImage = false
                                                statuesBase.sonImageBesoinActualisation=true
                                                besoin_To_Be_Updated = true
                                            }
                                            _appsHeadModel.produitsMainDataBase.updateProduitsFireBase()
                                        }
                                        .addOnFailureListener { e ->
                                            Log.e("InitViewModel", "Failed to download $fileName", e)
                                        }
                                } finally {
                                    activeDownloads.remove(productId)
                                }
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("InitViewModel", "Image listener cancelled", error.toException())
                }
            })

        // Listen for position changes
        AppsHeadModel.ref_produitsDataBase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                _appsHeadModel.produitsMainDataBase.forEach { product ->
                    viewModelScope.launch {
                        snapshot.child(product.id.toString())
                            .child("bonCommendDeCetteCota")
                            .child("positionProduitDonGrossistChoisiPourAcheterCeProduit")
                            .getValue(Int::class.java)?.let { newPosition ->
                                _positionChangeFlow.emit(product.id to newPosition)

                                AppsHeadModel.ref_produitsDataBase
                                    .child(product.id.toString())
                                    .child("grossist_Choisi_Pour_Acheter_CeProduit")
                                    .child(product.bonCommendDeCetteCota?.grossistInformations?.id.toString())
                                    .child("position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit")
                                    .setValue(newPosition)
                            }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("InitViewModel", "Position listener cancelled", error.toException())
            }
        })
    }

    override fun onCleared() {
        super.onCleared()
        activeDownloads.values.forEach { it.cancel() }
        activeDownloads.clear()
        _appsHeadModel.produitsMainDataBase.updateProduitsFireBase()
    }
}
