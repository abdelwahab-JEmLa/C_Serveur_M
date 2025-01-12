package com.example.Packages.A_GrosssitsCommendHandler.F1_ServeurGrossistCommendFragment

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
import com.example.Packages.A_GrosssitsCommendHandler.F1_ServeurGrossistCommendFragment.D_FloatingActionButton.GlobalEditesGFABsFragment_1
import com.example.Y_AppsFather.Kotlin.ViewModelProduits

private const val TAG = "A_ScreenMainFragment_1"

@Composable
internal fun A_ScreenMainFragment_1(
    modifier: Modifier = Modifier,
    viewModelProduits: ViewModelProduits = viewModel(),
) {
    // Log state changes using LaunchedEffect
    LaunchedEffect(viewModelProduits.isLoading, viewModelProduits.loadingProgress) {
        logLoadingState(viewModelProduits.isLoading, viewModelProduits.loadingProgress)
    }

    if (viewModelProduits.isLoading) {
        Box(modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator(
                progress = {
                    viewModelProduits.loadingProgress
                },
                modifier = Modifier.align(Alignment.Center),
                trackColor = ProgressIndicatorDefaults.circularIndeterminateTrackColor,
            )
        }
        return
    }

    val databaseSize = viewModelProduits.produitsAvecBonsGrossist.size

    val visibleProducts = viewModelProduits.produitsAvecBonsGrossist.filter { product ->
        product.bonCommendDeCetteCota
            ?.grossistInformations?.id ==
                viewModelProduits
                    ._paramatersAppsViewModelModel
                    .telephoneClientParamaters
                    .selectedGrossistForServeur

    }

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            if (databaseSize > 0) {
                B_ListMainFragment_1(
                    visibleProducts = visibleProducts,
                    viewModelProduits = viewModelProduits,
                    paddingValues = paddingValues
                )
            }
        }

        GlobalEditesGFABsFragment_1(
            appsHeadModel = viewModelProduits.modelAppsFather,
            modifier = modifier,
        )

        FilterScreenFab(
            viewModelProduits = viewModelProduits,

        )
    }
}

private fun logLoadingState(isLoading: Boolean, progress: Float) {
    Log.d(TAG, "Loading State: isLoading=$isLoading, progress=${progress * 100}%")
}
