package com.example.Packages.A_GrosssitsCommendHandler.A1_Fragment.A_Head

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.Apps_Head._1.Model.AppsHeadModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ViewModel_Head : ViewModel() {
    var _appsHeadModel by mutableStateOf(AppsHeadModel())

    var _mapsModel by mutableStateOf(Model_CodingWithMaps())
    val mapsModel: Model_CodingWithMaps get() = _mapsModel

    private var isInitializing by mutableStateOf(false)
    var initializationComplete by mutableStateOf(false)
    var initializationProgress by mutableFloatStateOf(0f)


    private var activeDownloads = mutableMapOf<Long, Job>()
    private val basePath = "/storage/emulated/0/Abdelwahab_jeMla.com/IMGs/BaseDonne"


    init {
        initializeData()
    }

    private fun initializeData() {
        viewModelScope.launch {
            try {
                isInitializing = true

                start(this@ViewModel_Head)

                initializationComplete = true
            } finally {
                isInitializing = false
            }
        }
    }



}
