package com.example.Apps_Head._2.ViewModel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.Apps_Head._1.Model.AppsHeadModel
import com.example.Apps_Head._4.Init.LoadFromFirebaseHandler
import com.example.Apps_Head._4.Init.cree_New_Start
import kotlinx.coroutines.launch

class InitViewModel : ViewModel() {
    var _appsHeadModel by mutableStateOf(AppsHeadModel())
    val appsHead: AppsHeadModel get() = _appsHeadModel

    var initializationProgress by mutableFloatStateOf(0f)
    var isInitializing by mutableStateOf(false)
    var initializationComplete by mutableStateOf(false)

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

                val createStart = 1

                if (createStart == 1) {
                    cree_New_Start()
                    initializationProgress = 1f
                } else {
                    LoadFromFirebaseHandler.loadFromFirebase(this@InitViewModel)
                }

                initializationComplete = true
            } catch (e: Exception) {
                Log.e(TAG, "Initialization failed", e)
                // Handle the error appropriately in your UI
            } finally {
                isInitializing = false
            }
        }
    }
}
