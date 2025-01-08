package com.example.Packages.A_GrosssitsCommendHandler.A1_Fragment

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.Apps_Head._2.ViewModel.InitViewModel
import com.example.Packages.A_GrosssitsCommendHandler.A1_Fragment.A_Head.ViewModel.ViewModel_Head
import com.example.Packages.A_GrosssitsCommendHandler.A1_Fragment.D_FloatingActionButton.GlobalEditesGFABsFragment_1
import com.example.Packages.A_GrosssitsCommendHandler.A1_Fragment.D_FloatingActionButton.GrossisstsGroupedFABsFragment_1

internal const val DEBUG_LIMIT = 7

@Composable
internal fun A_ScreenMainFragment_1(
    modifier: Modifier = Modifier,
    initViewModel: InitViewModel = viewModel(),
    viewModel_Head: ViewModel_Head = viewModel(),
    ) {
    val TAG = "A_ScreenMainFragment_1"
    if (!viewModel_Head.initializationComplete) {
        Box(modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator(
                progress = {
                    viewModel_Head.initializationProgress
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
                val databaseSize = viewModel_Head.mapsModel.mutableStatesVars.mapGrossistIdToProduitId.size

                if (databaseSize > 0) {
                    B_ListMainFragment_1(viewModel_Head, paddingValues)
                }
            }

            GlobalEditesGFABsFragment_1(
                appsHeadModel = initViewModel.appsHeadModel,
                modifier = modifier,
            )

            GrossisstsGroupedFABsFragment_1(
                viewModel_Head=viewModel_Head,

            )
        }
    }
}

@Preview
@Composable
private fun PreviewScreenMainFragment_1() {
    A_ScreenMainFragment_1(modifier = Modifier.fillMaxSize())
}
