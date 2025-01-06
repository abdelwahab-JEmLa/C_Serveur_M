package com.example.Packages.A3_Fragment

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.Apps_Head._2.ViewModel.InitViewModel
import com.example.Packages.A3_Fragment.D_FloatingActionButton.GlobalEditesGFABs_Fragment_3
import com.example.Packages.A3_Fragment.D_FloatingActionButton.GrossisstsGroupedFABs_Fragment_3

internal const val DEBUG_LIMIT_Fragment_3 = 7

@Composable
internal fun ScreenMain_Fragment_3(
    modifier: Modifier = Modifier,
    initViewModel: InitViewModel = viewModel(),
) {
    val TAG = "A_ScreenMainFragment"
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

    val produitsMainDataBase by remember(initViewModel._appsHeadModel.produitsMainDataBase) {
        derivedStateOf { initViewModel._appsHeadModel.produitsMainDataBase.toList() }
    }

    val visibleItems by remember(produitsMainDataBase) {
        derivedStateOf {
            produitsMainDataBase.filter { it.isVisible }.toMutableStateList()
        }
    }
    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column {
                val databaseSize = produitsMainDataBase.size

                if (databaseSize > 0) {
                    ListMain_Fragment_3(
                        initViewModel=initViewModel,
                        visibleItems = visibleItems,
                        contentPadding = paddingValues,
                        onClickCamera = {item->
                            initViewModel._appsHeadModel.produitsMainDataBase
                                .find { it.id==item.id }
                                .let {
                                    it?.statuesBase?.prePourCameraCapture
                                }
                        },
                        onCLickOnMainEtitsTempProduit = { product ->
                            initViewModel._appsHeadModel
                                .produitsMainDataBase.find { it.id==product.id }.let {
                                    if (it != null) {
                                        it.statuesBase.prePourCameraCapture=true
                                    }
                                }
                        }
                    )
                }
            }

            GlobalEditesGFABs_Fragment_3(
                appsHeadModel = initViewModel.appsHead,
                modifier = modifier,
            )

            GrossisstsGroupedFABs_Fragment_3(
                onClickFAB = { newList ->
                    initViewModel._appsHeadModel.produitsMainDataBase.clear()
                    initViewModel._appsHeadModel.produitsMainDataBase.addAll(newList)
                },
                produitsMainDataBase = produitsMainDataBase,
                modifier = modifier
            )

        }
    }
}

@Preview
@Composable
private fun PreviewScreenMain_Fragment_3() {
    ScreenMain_Fragment_3(modifier = Modifier.fillMaxSize())
}
