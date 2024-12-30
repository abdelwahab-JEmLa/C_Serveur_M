package com.example.Packages._1.Fragment.UI

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
import com.example.Packages._1.Fragment.UI._2.ListMain.ListMain
import com.example.Packages._1.Fragment.UI._5.FloatingActionButton.GlobalActions_FloatingActionButtons_Grouped
import com.example.Packages._1.Fragment.UI._5.FloatingActionButton.Grossissts_FloatingActionButtons_Grouped
import com.example.Packages._1.Fragment.ViewModel.F3_ViewModel

@Composable
internal fun ScreenMain(
    modifier: Modifier = Modifier,
    appInitializeViewModel: InitViewModel = viewModel(),
    p3_ViewModel: F3_ViewModel = viewModel()
) {
    if (!appInitializeViewModel.initializationComplete) {
        Box(modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator(
                progress = {
                    appInitializeViewModel.initializationProgress
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
                val databaseSize = appInitializeViewModel.appsHead.produits_Main_DataBase.size

                if (databaseSize > 0) {
                    ListMain(
                        initViewModel=appInitializeViewModel,
                        app_Initialize_Model = appInitializeViewModel.appsHead,
                        ui_State = p3_ViewModel.uiState,
                        contentPadding = paddingValues
                    )
                }
            }

            Grossissts_FloatingActionButtons_Grouped(
                headViewModel=appInitializeViewModel,
                modifier = Modifier,
                ui_State = p3_ViewModel.uiState,
                app_Initialize_Model = appInitializeViewModel.appsHead,
            )

            GlobalActions_FloatingActionButtons_Grouped(
                modifier = Modifier,
                fragment_Ui_State = p3_ViewModel.uiState,
                app_Initialize_Model = appInitializeViewModel.appsHead,
            )
        }
    }
}

@Preview
@Composable
private fun Preview_Fragment_4_Main_Screen() {
    ScreenMain(modifier = Modifier.fillMaxSize())
}
