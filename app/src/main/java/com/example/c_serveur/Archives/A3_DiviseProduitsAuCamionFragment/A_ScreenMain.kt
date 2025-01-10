package com.example.c_serveur.Archives.A3_DiviseProduitsAuCamionFragment

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
import com.example.Y_AppsFather.Kotlin.ViewModelProduits
import com.example.c_serveur.Archives.A3_DiviseProduitsAuCamionFragment.D_FloatingActionButton.GlobalEditesGFABs_Fragment_3

internal const val DEBUG_LIMIT_Fragment_3 = 7

@Preview
@Composable
private fun PreviewScreenMain_Fragment_3() {
    ScreenMain_Fragment_3(modifier = Modifier.fillMaxSize())
}

@Composable
internal fun ScreenMain_Fragment_3(
    modifier: Modifier = Modifier,
    initViewModel: ViewModelProduits = viewModel(),
) {
    val TAG = "ScreenMainFragment"
    if (!initViewModel.initializationComplete) {
        Box(modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator(
                progress = {
                    initViewModel.initializationProgress
                },
                modifier = Modifier.align(Alignment.Center),
                trackColor = ProgressIndicatorDefaults.circularIndeterminateTrackColor,
            )
        }
        return
    }

    val produitsMainDataBase =
        initViewModel._modelAppsFather.produitsMainDataBase

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column {
                val databaseSize = produitsMainDataBase.size

                if (databaseSize > 0) {
                    ListMain_Fragment_3(produitsMainDataBase, initViewModel, paddingValues)
                }
            }
        }

        GlobalEditesGFABs_Fragment_3(
            appsHeadModel = initViewModel.modelAppsFather,
            modifier = modifier,
        )

      //  ClientsGroupedFABs_Fragment_3(produitsMainDataBase ,initViewModel,modifier)

    }
}


