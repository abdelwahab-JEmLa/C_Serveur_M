package com.example.Packages.A2_Fragment

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.Apps_Head._2.ViewModel.InitViewModel
import com.example.Packages.A2_Fragment.D_FloatingActionButton.GlobalEditesGFABsFragment_2
import com.example.Packages.A2_Fragment.D_FloatingActionButton.GrossisstsGroupedFABsFragment_2
import kotlinx.coroutines.flow.collectLatest

internal const val DEBUG_LIMIT = 7

@Preview(showBackground = true)
@Composable
private fun PreviewA_ScreenMainFragment_2() {
    A_ScreenMainFragment_2(modifier = Modifier.fillMaxSize())
}

@Composable
internal fun A_ScreenMainFragment_2(
    modifier: Modifier = Modifier,
    initViewModel: InitViewModel = viewModel(),
) {
    val produitsMainDataBase by remember(initViewModel._appsHeadModel.produitsMainDataBase) {
        derivedStateOf { initViewModel._appsHeadModel.produitsMainDataBase.toList() }
    }

    val visibleItems by remember(produitsMainDataBase) {
        derivedStateOf {
            produitsMainDataBase
                .filter { it.isVisible }
                .sortedBy { it.bonCommendDeCetteCota?.positionProduitDonGrossistChoisiPourAcheterCeProduit }
                .toMutableStateList()
        }
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        // Handle position changes
        LaunchedEffect(Unit) {
            initViewModel.positionChangeFlow.collectLatest { (productId, newPosition) ->
                val grossistVisibleMnt= visibleItems.first().bonCommendDeCetteCota?.grossistInformations
                initViewModel._appsHeadModel.produitsMainDataBase
                    .find { it.id == productId }
                    ?.let {
                        it.bonCommendDeCetteCota?.positionProduitDonGrossistChoisiPourAcheterCeProduit = newPosition
                        // Simplified visibility logic
                        it.isVisible = it.bonCommendDeCetteCota?.grossistInformations == grossistVisibleMnt && !it.isVisible
                    }
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            if (!initViewModel.initializationComplete) {
                CircularProgressIndicator(
                    progress = { initViewModel.initializationProgress },
                    modifier = Modifier.align(Alignment.Center),
                    trackColor = ProgressIndicatorDefaults.circularIndeterminateTrackColor
                )
            } else {

                Scaffold { paddingValues ->
                    Box(modifier = Modifier.fillMaxSize()) {
                        if (produitsMainDataBase.isNotEmpty()) {
                            B_ListMainFragment_2(
                                visibleItems = visibleItems,
                                contentPadding = paddingValues
                            )
                        }

                        GrossisstsGroupedFABsFragment_2(
                            onClickFAB = { newList ->
                                with(initViewModel._appsHeadModel.produitsMainDataBase) {
                                    clear()
                                    addAll(newList)
                                }
                            },
                            produitsMainDataBase = produitsMainDataBase,
                            modifier = modifier
                        )

                        GlobalEditesGFABsFragment_2(
                            appsHeadModel = initViewModel.appsHead,
                            modifier = modifier
                        )
                    }
                }
            }
        }
    }
}
