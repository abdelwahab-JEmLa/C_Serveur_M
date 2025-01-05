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
import com.example.Apps_Head._3.Modules.Images_Handler.ImageStoreUpdate
import com.example.Apps_Head._4.Init.initializer
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.storage.storage
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

    // Position change flow for observing position updates
    private val _positionChangeFlow = MutableSharedFlow<Pair<Long, Int>>()
    val positionChangeFlow = _positionChangeFlow.asSharedFlow()

    private val positionListeners = mutableMapOf<Long, ValueEventListener>()

    init {
        initAndObserveData()
        // Configurer le listener pour les images
        Firebase.database.getReference("Images Articles Data Base/AppsHeadModel.Produit_Main_DataBase")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    // Pour chaque image détectée
                    snapshot.children.forEach { imageData ->
                        val fileName = imageData.key ?: return@forEach
                        val productId = fileName.split("_")[0].toLongOrNull() ?: return@forEach
                        val product = _appsHeadModel.produitsMainDataBase.find { it.id == productId } ?: return@forEach

                        // Préparer le dossier local
                        val localDir = File("/storage/emulated/0/Abdelwahab_jeMla.com/IMGs/BaseDonne").apply {
                            if (!exists()) mkdirs()
                        }
                        val localFile = File(localDir, fileName)

                        // Télécharger si nécessaire
                        if (!localFile.exists() || product.itImageBesoinToBeUpdated) {
                            Firebase.storage.reference
                                .child("Images Articles Data Base/AppsHeadModel.Produit_Main_DataBase/$fileName")
                                .getFile(localFile)
                                .addOnSuccessListener {
                                    product.apply {
                                        itImageBesoinToBeUpdated = false
                                        besoin_To_Be_Updated = true
                                    }
                                    _appsHeadModel.produitsMainDataBase.updateProduitsFireBase()
                                    Log.d("InitViewModel", "Image téléchargée: $fileName")
                                }
                                .addOnFailureListener { e ->
                                    Log.e("InitViewModel", "Erreur téléchargement: $fileName", e)
                                }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("InitViewModel", "Erreur listener", error.toException())
                }
            })
    }

    private fun initAndObserveData() {
        viewModelScope.launch {
            try {
                isInitializing = true

                initializer()

                initializationComplete = true
            } catch (e: Exception) {
                Log.e("InitViewModel", "Init failed: ${e.message}")
                initializationProgress = 0f
                initializationComplete = false
            } finally {
                isInitializing = false
            }
        }

        AppsHeadModel.ref_produitsDataBase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                viewModelScope.launch {
                    try {
                        _appsHeadModel.produitsMainDataBase.forEach { produit ->
                            // Set position listener
                            val positionRef = AppsHeadModel.ref_produitsDataBase
                                .child(produit.id.toString())
                                .child("bonCommendDeCetteCota")
                                .child("positionProduitDonGrossistChoisiPourAcheterCeProduit")

                            // Remove old listener if exists
                            positionListeners[produit.id]?.let { oldListener ->
                                positionRef.removeEventListener(oldListener)
                            }

                            // Add new listener with positionChangeFlow emission
                            val newListener = object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    snapshot.getValue(Int::class.java)?.let { newPosition ->
                                        viewModelScope.launch {
                                            // Emit to positionChangeFlow
                                            _positionChangeFlow.emit(produit.id to newPosition)

                                            // Update Firebase
                                            val productRef = AppsHeadModel.ref_produitsDataBase
                                                .child(produit.id.toString())
                                                .child("grossist_Choisi_Pour_Acheter_CeProduit")
                                                .child(produit.bonCommendDeCetteCota?.grossistInformations?.id.toString())
                                                .child("position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit")

                                            productRef.setValue(newPosition)
                                        }
                                    }
                                }
                                override fun onCancelled(error: DatabaseError) {
                                    Log.e("InitViewModel", "Position update failed: ${error.message}")
                                }
                            }

                            positionListeners[produit.id] = newListener
                            positionRef.addValueEventListener(newListener)

                            // Handle image updates if needed
                            if (produit.itImageBesoinToBeUpdated) {
                                ImageStoreUpdate(this@InitViewModel,produit.id)
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("InitViewModel", "Update failed: ${e.message}")
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("InitViewModel", "Database error: ${error.message}")
            }
        })
    }



    override fun onCleared() {
        super.onCleared()
        positionListeners.forEach { (_, listener) ->
            AppsHeadModel.ref_produitsDataBase.removeEventListener(listener)
        }
        positionListeners.clear()
        _appsHeadModel.produitsMainDataBase.updateProduitsFireBase()
    }
    //-->
    //TODO(1): ajoute on datachange listner de val storageRef = Firebase.storage.reference
    //                        .child("Images Articles Data Base/AppsHeadModel.Produit_Main_DataBase/$fileName")
    // qui telechage val imagesBasePath = "/storage/emulated/0/Abdelwahab_jeMla.com/IMGs/BaseDonne"
    // si _appsHeadModel.produitsMainDataBase
}
