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
import com.example.Apps_Head._4.Init.LoadFromFirebaseHandler
import com.example.Apps_Head._4.Init.cree_New_Start
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class InitViewModel : ViewModel() {
    // Model state
    var _appsHeadModel by mutableStateOf(AppsHeadModel())
    val appsHead: AppsHeadModel get() = _appsHeadModel

    // Initialization states
    var initializationProgress by mutableFloatStateOf(0f)
    var isInitializing by mutableStateOf(false)
    var initializationComplete by mutableStateOf(false)
    private var createStart by mutableStateOf(false)

    // Event handling for position changes
    private val _positionChangeFlow = MutableSharedFlow<Pair<Long, Int>>()
    val positionChangeFlow = _positionChangeFlow.asSharedFlow()

    // Map to store position listeners for cleanup
    private val positionListeners = mutableMapOf<Long, ValueEventListener>()

    private companion object {
        const val TAG = "InitViewModel"
    }

    init {
        initializeData()
    }

    private fun initializeData() {
        viewModelScope.launch {
            try {
                isInitializing = true
                initializationProgress = 0f
             //   createStart = true

                if (createStart) {
                    cree_New_Start()
                    initializationProgress = 1f
                } else {
                    LoadFromFirebaseHandler.loadFromFirebase(this@InitViewModel)
                }

                initializationComplete = true
                setupProductPositionListeners()
            } catch (e: Exception) {
                Log.e(TAG, "Initialization failed", e)
                handleInitializationError(e)
            } finally {
                isInitializing = false
            }
        }
    }

    private fun setupProductPositionListeners() {
        _appsHeadModel.produitsMainDataBase.forEach { product ->
            observeProductPosition(product.id)
        }
    }

    private fun observeProductPosition(productId: Long) {
        val positionRef = AppsHeadModel.ref_produitsDataBase
            .child(productId.toString())
            .child("bonCommendDeCetteCota")
            .child("positionProduitDonGrossistChoisiPourAcheterCeProduit")

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val newPosition = snapshot.getValue(Int::class.java)
                newPosition?.let { position ->
                    viewModelScope.launch {
                        _positionChangeFlow.emit(productId to position)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Position listener cancelled for product $productId: ${error.message}")
            }
        }

        positionListeners[productId] = listener
        positionRef.addValueEventListener(listener)
    }

    private fun handleInitializationError(error: Exception) {
        Log.e(TAG, "Initialization error", error)
        initializationProgress = 0f
        initializationComplete = false
    }

    override fun onCleared() {
        super.onCleared()
        // Remove all position listeners
        positionListeners.forEach { (productId, listener) ->
            AppsHeadModel.ref_produitsDataBase
                .child(productId.toString())
                .child("bonCommendDeCetteCota")
                .child("positionProduitDonGrossistChoisiPourAcheterCeProduit")
                .removeEventListener(listener)
        }
        positionListeners.clear()

        // Update Firebase if needed
        _appsHeadModel.produitsMainDataBase.updateProduitsFireBase()
    }
}
