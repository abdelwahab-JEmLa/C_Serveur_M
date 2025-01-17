package com.example.Packages.Z_F3._PhoneClientClient

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
import com.example.Packages.Z_F3.Modules.GlobalEditesGFABs_F3

private const val TAG = "A_ScreenMainFragment_1"

@Composable
internal fun MainScreen_F3(
    modifier: Modifier = Modifier,
    viewModelInitApp: ViewModelInitApp = viewModel(),
) {
    // Log state changes using LaunchedEffect
    LaunchedEffect(
        viewModelInitApp.isLoading,
        viewModelInitApp.loadingProgress
    ) {
        logLoadingState(viewModelInitApp.isLoading,
            viewModelInitApp.loadingProgress)
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

    val visibleProducts = viewModelInitApp._modelAppsFather.produitsMainDataBase
        .filter { product ->
            product.bonsVentDeCetteCota.firstOrNull()?.let { bonVent ->
                (bonVent.clientInformations?.id ==
                        viewModelInitApp
                            ._paramatersAppsViewModelModel
                            .phoneClientSelectedAcheteur
                        && (product
                    .bonCommendDeCetteCota
                    ?.mutableBasesStates
                    ?.positionProduitDonGrossistChoisiPourAcheterCeProduit ?: 0) > 0)
            } ?: false
        }
    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            if (databaseSize > 0) {
                MainList_F3(
                    visibleProducts = visibleProducts,
                    viewModelProduits = viewModelInitApp,
                    paddingValues = paddingValues
                )
            }
        }
        if (viewModelInitApp
                ._paramatersAppsViewModelModel
                .fabsVisibility
        ) {
            GlobalEditesGFABs_F3(
                appsHeadModel = viewModelInitApp.modelAppsFather,
                modifier = modifier,
            )

            MainScreenFilterFAB_F3(
                viewModelProduits = viewModelInitApp,
            )
        }
    }
}

private fun logLoadingState(isLoading: Boolean, progress: Float) {
    Log.d(TAG, "Loading State: isLoading=$isLoading, progress=${progress * 100}%")
}
