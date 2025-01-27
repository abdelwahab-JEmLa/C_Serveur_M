package com.example.Packages.Views._1_GerantAfficheurGrossistCommend.App.NH_3.id2_TravaillieurListProduitAchercheChezLeGrossist.ViewModel.Extension

import Z_MasterOfApps.Kotlin.Model._ModelAppsFather.ProduitModel
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope

class ViewModelExtension_App1_F2(
    val viewModel: ViewModelInitApp,
    val produitsMainDataBase: MutableList<ProduitModel>,
    val viewModelScope: CoroutineScope,
) {
    var afficheProduitsPourRegleConflites by mutableStateOf(false)
}


