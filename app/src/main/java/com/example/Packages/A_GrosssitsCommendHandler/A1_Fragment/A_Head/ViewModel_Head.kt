package com.example.Packages.A_GrosssitsCommendHandler.A1_Fragment.A_Head

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.Apps_Head._1.Model.AncienResourcesDataBaseMain
import com.example.Apps_Head._4.Init.GetAncienDataBasesMain
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ViewModel_Head : ViewModel() {
    var _maps by mutableStateOf(Maps())
    val maps: Maps get() = _maps

    var _mapsModel by mutableStateOf(Model_CodingWithMaps())
    val mapsModel: Model_CodingWithMaps get() = _mapsModel

    // Initialize _ancienModels with a default empty state
    var _ancienModels by mutableStateOf(
        AncienResourcesDataBaseMain(
            produitsDatabase = emptyList(),
            soldArticles = emptyList(),
            couleurs_List = emptyList(),
            clients_List = emptyList()
        )
    )

    private var isInitializing by mutableStateOf(false)
    var initializationComplete by mutableStateOf(false)
    var initializationProgress by mutableFloatStateOf(0f)

    val positionedProduits = mapsModel.mutableStatesVars.positionedProduits
    val unPositionedProduits = mapsModel.mutableStatesVars.unPositionedProduits

    private var activeDownloads = mutableMapOf<Long, Job>()
    private val basePath = "/storage/emulated/0/Abdelwahab_jeMla.com/IMGs/BaseDonne"

    init {
        initializeData()
    }

    private fun initializeData() {
        viewModelScope.launch {
            try {
                isInitializing = true

                // Load ancien databases
                _ancienModels = GetAncienDataBasesMain()

                start(this@ViewModel_Head)

                initializationComplete = true
            } catch (e: Exception) {
                // Handle any errors that occur during initialization
                // You might want to set an error state here
            } finally {
                isInitializing = false
            }
        }
    }
}
