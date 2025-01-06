package com.example.Packages.A1_Fragment

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
import com.example.Packages.A1_Fragment.D_FloatingActionButton.GlobalEditesGFABsFragment_1
import com.example.Packages.A1_Fragment.D_FloatingActionButton.GrossisstsGroupedFABsFragment_1

internal const val DEBUG_LIMIT = 7

@Composable
internal fun A_ScreenMainFragment_1(
    modifier: Modifier = Modifier,
    initViewModel: InitViewModel = viewModel(),
) {
    val TAG = "A_ScreenMainFragment_1"
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
                    B_ListMainFragment_1(
                        visibleItems = visibleItems,
                        initViewModel=initViewModel,
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

            GlobalEditesGFABsFragment_1(
                appsHeadModel = initViewModel.appsHeadModel,
                modifier = modifier,
            )

            GrossisstsGroupedFABsFragment_1(
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
private fun PreviewScreenMainFragment_1() {
    A_ScreenMainFragment_1(modifier = Modifier.fillMaxSize())
}
