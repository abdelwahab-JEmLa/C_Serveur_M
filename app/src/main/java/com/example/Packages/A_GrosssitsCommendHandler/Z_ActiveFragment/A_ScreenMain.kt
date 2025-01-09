package com.example.Packages.A_GrosssitsCommendHandler.Z_ActiveFragment

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.Packages.A_GrosssitsCommendHandler.Z_ActiveFragment.D_FloatingActionButton.GlobalEditesGFABsFragment_1
import com.example.Packages.A_GrosssitsCommendHandler.Z_ActiveFragment.D_FloatingActionButton.GrossisstsGroupedFABs
import com.example.Y_AppsFather.Kotlin.ViewModelProduits
import com.example.Z_AppsFather.Kotlin._2.ViewModel.Z.Parent.ViewModel_Head

private const val TAG = "A_ScreenMainFragment_1"

@Composable
internal fun A_ScreenMainFragment_1(
    modifier: Modifier = Modifier,
    initViewModel: ViewModelProduits = viewModel(),
    viewModel_Head: ViewModel_Head = viewModel(),
) {
    // Log state changes using LaunchedEffect
    LaunchedEffect(viewModel_Head.isLoading, viewModel_Head.loadingProgress) {
        logLoadingState(viewModel_Head.isLoading, viewModel_Head.loadingProgress)
    }

    LaunchedEffect(viewModel_Head.maps.mapGroToMapPositionToProduits.size) {
        logDatabaseSize(viewModel_Head.maps.mapGroToMapPositionToProduits.size)
    }

    if (viewModel_Head.isLoading) {  // Changed from !isLoading to isLoading
        Box(modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator(
                progress = {
                    viewModel_Head.loadingProgress
                },
                modifier = Modifier.align(Alignment.Center),
                trackColor = ProgressIndicatorDefaults.circularIndeterminateTrackColor,
            )
        }
        return
    }

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column {
                val databaseSize = viewModel_Head.maps.mapGroToMapPositionToProduits.size

                if (databaseSize > 0) {
                    B_ListMainFragment_1(viewModel_Head, paddingValues)
                }
            }

            GlobalEditesGFABsFragment_1(
                appsHeadModel = initViewModel.appsHeadModel,
                modifier = modifier,
            )

            GrossisstsGroupedFABs(
                viewModel_Head = viewModel_Head,
            )
        }
    }
}

private fun logLoadingState(isLoading: Boolean, progress: Float) {
    Log.d(TAG, "Loading State: isLoading=$isLoading, progress=${progress * 100}%")
}

private fun logDatabaseSize(size: Int) {
    Log.d(TAG, "Database Size: $size entries")
}


