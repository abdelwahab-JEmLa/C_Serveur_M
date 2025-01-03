package com.example.Packages._1.Fragment.UI

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.Apps_Head.B_RelationalDataBase._2.ViewModel.RelationalViewModel
import com.example.Apps_Head._1.Model.AppsHeadModel
import com.example.Apps_Head._2.ViewModel.InitViewModel
import com.example.Packages._1.Fragment.UI._5.FloatingActionButton.GrossisstsGroupedFABs
import com.example.Packages._2.Fragment.UI._5.FloatingActionButton.GlobalEditesGroupedFloatingActionButtons
import com.example.Packages._2.Fragment.ViewModel.Frag_ViewModel

internal const val DEBUG_LIMIT = 7

@Composable
internal fun ScreenMain(
    modifier: Modifier = Modifier,
    initViewModel: InitViewModel = viewModel(),
    relationalViewModel: RelationalViewModel = viewModel(),
    frag_ViewModel: Frag_ViewModel = viewModel()
) {
    val TAG = "ScreenMain"

    if (!initViewModel.initializationComplete) {
        Box(modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator(
                progress = { initViewModel.initializationProgress },
                modifier = Modifier.align(Alignment.Center),
                trackColor = ProgressIndicatorDefaults.circularIndeterminateTrackColor
            )
        }
        return
    }

    // State for visible items and their associated clients
    var mapVisibleItemeSetEtleurClient by remember {
        mutableStateOf<Map<AppsHeadModel.ProduitModel.GrossistBonCommandes.GrossistInformations, Set<AppsHeadModel.ProduitModel>>>(
            emptyMap()
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column {
                val databaseSize = initViewModel._appsHeadModel.produitsMainDataBase.size

                if (databaseSize > 0) {
                    ListMain(
                        mapVisibleItemeSetEtleurClient = mapVisibleItemeSetEtleurClient,
                        contentPadding = paddingValues
                    )
                }
            }

            // Floating Action Buttons
            GrossisstsGroupedFABs(
                onClickFAB = { newVisibleItems ->
                    mapVisibleItemeSetEtleurClient = newVisibleItems
                },
                produitsMainDataBase = initViewModel._appsHeadModel.produitsMainDataBase,
                modifier = modifier
            )

            GlobalEditesGroupedFloatingActionButtons(
                produitsMainDataBase = initViewModel._appsHeadModel.produitsMainDataBase,
                app_Initialize_Model = initViewModel.appsHead,
                modifier = modifier,
                fragment_Ui_State = frag_ViewModel.uiState
            )
        }
    }
}

@Preview
@Composable
private fun PreviewScreen() {
    ScreenMain(modifier = Modifier.fillMaxSize())
}
