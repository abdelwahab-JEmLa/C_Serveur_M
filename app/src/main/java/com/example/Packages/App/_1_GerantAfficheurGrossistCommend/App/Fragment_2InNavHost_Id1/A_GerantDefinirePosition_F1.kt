package com.example.Packages.App._1_GerantAfficheurGrossistCommend.App.Fragment_2InNavHost_Id1

import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.Packages.App._1_GerantAfficheurGrossistCommend.App.Fragment_2InNavHost_Id1.Modules.ClientEditePositionDialog
import com.example.Packages.App._1_GerantAfficheurGrossistCommend.App.Fragment_2InNavHost_Id1.Modules.GlobalEditesGFABs_F1

private const val TAG = "A_GerantDefinirePosition_F1"

@Composable
internal fun A_GerantDefinirePosition_F1(
    modifier: Modifier = Modifier,
    viewModelInitApp: ViewModelInitApp = viewModel(),
) {
    // Log state changes using LaunchedEffect
    LaunchedEffect(viewModelInitApp.isLoading, viewModelInitApp.loadingProgress) {
        logLoadingState(viewModelInitApp.isLoading, viewModelInitApp.loadingProgress)
    }

    if (viewModelInitApp.isLoading) {
        Box(modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator(
                progress = {
                    viewModelInitApp.loadingProgress
                },
                modifier = Modifier.align(Alignment.Center),
                trackColor = ProgressIndicatorDefaults.circularIndeterminateTrackColor,
            )
        }
        return
    }

    val databaseSize = viewModelInitApp._modelAppsFather.produitsMainDataBase.size

    val visibleProducts = viewModelInitApp._modelAppsFather.produitsMainDataBase.filter { product ->
        product.bonCommendDeCetteCota
            ?.grossistInformations?.id ==
                viewModelInitApp
                    ._paramatersAppsViewModelModel
                    .telephoneClientParamaters
                    .selectedGrossistForServeur
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize()
        ) { paddingValues ->
            Box(modifier = Modifier.fillMaxSize()) {
                if (databaseSize > 0) {
                    B_ListMainFragment(
                        visibleProducts = visibleProducts,
                        viewModelProduits = viewModelInitApp,
                        paddingValues = paddingValues
                    )
                }

                // Déplacer GlobalEditesGFABs ici, à l'intérieur du Box mais en dehors du Scaffold
                if (viewModelInitApp._paramatersAppsViewModelModel.fabsVisibility) {
                    GlobalEditesGFABs_F1(
                        appsHeadModel = viewModelInitApp._modelAppsFather,
                        viewModelInitApp = viewModelInitApp,
                    )

                    MainScreenFilterFAB(
                        viewModelProduits = viewModelInitApp,
                    )
                }
            }

            ClientEditePositionDialog(    
                viewModelProduits = viewModelInitApp,
            )
        }
    }
}

private fun logLoadingState(isLoading: Boolean, progress: Float) {
    Log.d(TAG, "Loading State: isLoading=$isLoading, progress=${progress * 100}%")
}
