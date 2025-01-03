package com.example.Apps_Head.B_RelationalDataBase._2.ViewModel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.Apps_Head.B_RelationalDataBase._1.Model.RelationalDatabase
import com.example.Apps_Head.B_RelationalDataBase._3.Init.Cree
import kotlinx.coroutines.launch

class RelationalViewModel : ViewModel() {
    var _relationalDatabase by mutableStateOf(RelationalDatabase())
    val relationalDatabase: RelationalDatabase get() = _relationalDatabase

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
                    initializationProgress = 1f
                    Cree()
                } else {
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
