package com.example.Apps_Head._2.ViewModel

import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.Apps_Head._1.Model.AppsHeadModel
import com.example.Apps_Head._4.Init.cree_New_Start
import com.example.Apps_Head._4.Init.loadFromFirebaseHandler
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

open class InitViewModel : ViewModel() {
    var _appsHead by mutableStateOf(AppsHeadModel())
    val appsHead: AppsHeadModel get() = this._appsHead

    var initializationProgress by mutableFloatStateOf(0f)
    var isInitializing by mutableStateOf(false)
    var initializationComplete by mutableStateOf(false)

    init {
        viewModelScope.launch {
            try {
                isInitializing = true
                initializationProgress = 0f

                val createStart = 0 // Changed to test Firebase loading

                if (createStart == 1) {
                    cree_New_Start()
                    initializationProgress = 1f
                } else {
                    // Convert Firebase callback to coroutine
                    val products = suspendCancellableCoroutine { continuation ->
                        loadFromFirebaseHandler.loadFromFirebase { products ->
                            if (continuation.isActive) {
                                continuation.resume(products)
                            }
                        }
                    }

                    // Update the products if loaded successfully
                    products?.let {
                        _appsHead.produits_Main_DataBase = it
                        Log.d(TAG, "Successfully loaded ${it.size} products from Firebase")
                    } ?: run {
                        Log.e(TAG, "Failed to load products from Firebase")
                        throw Exception("Failed to load products from Firebase")
                    }

                    initializationProgress = 1f
                }

                initializationComplete = true

            } catch (e: Exception) {
                Log.e(TAG, "Initialization failed", e)
                throw e // Rethrow to handle in UI if needed
            } finally {
                isInitializing = false
            }
        }
    }

}
