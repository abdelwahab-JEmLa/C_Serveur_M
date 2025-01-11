package com.example.Packages.A_GrosssitsCommendHandler.Z_NowActiveFragment

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.Packages.A_GrosssitsCommendHandler.Z_NowActiveFragment.Modules.GlobalEditesGFABs_F2
import com.example.Y_AppsFather.Kotlin.ViewModelProduits

private const val TAG = "A_ScreenMainFragment_1"

@Composable
internal fun MainScreen_F2(
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

    var selectedGrossist by remember { mutableLongStateOf(0L) }
    val databaseSize = viewModelProduits.produitsAvecBonsGrossist.size

    // Use the filtered products directly from produitsAvecBonsGrossist
    val visibleProducts = viewModelProduits.produitsAvecBonsGrossist.filter { product ->
        product.bonCommendDeCetteCota?.grossistInformations?.id == selectedGrossist
    }

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            if (databaseSize > 0) {
                MainList_F2(
                    visibleProducts = visibleProducts,
                    viewModelProduits = viewModelProduits,
                    paddingValues = paddingValues
                )
            }
        }
        //
        GlobalEditesGFABs_F2(
            appsHeadModel = viewModelProduits.modelAppsFather,
            modifier = modifier,
        )

        MainScreenFilterFAB_F2(
            viewModelProduits = viewModelProduits,
            onClick = {
                selectedGrossist = it
            }
        )
    }
}

private fun logLoadingState(isLoading: Boolean, progress: Float) {
    Log.d(TAG, "Loading State: isLoading=$isLoading, progress=${progress * 100}%")
}
