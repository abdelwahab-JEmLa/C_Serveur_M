package com.example.Packages._1.Fragment.UI

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.Apps_Head._2.ViewModel.InitViewModel
import com.example.Packages._1.Fragment.UI._5.FloatingActionButton.GlobalActions_FloatingActionButtons_Grouped
import com.example.Packages._1.Fragment.UI._5.FloatingActionButton.Grossissts_FloatingActionButtons_Grouped
import com.example.Packages._1.Fragment.ViewModel.F3_ViewModel

@Composable
internal fun ScreenMain(
    modifier: Modifier = Modifier,
    initViewModel: InitViewModel = viewModel(),
    p3_ViewModel: F3_ViewModel = viewModel(),
) {
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

    // Create an immutable snapshot of the database list
    val currentItems by remember(initViewModel._appsHead.produits_Main_DataBase) {
        mutableStateOf(initViewModel._appsHead.produits_Main_DataBase)
    }

    // With this:
    val visibleItems by remember(currentItems) {
        derivedStateOf { currentItems.filter { it.isVisible }.toMutableStateList() }
    }
    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column {
                val databaseSize = initViewModel.appsHead.produits_Main_DataBase.size

                if (databaseSize > 0) {
                    ListMain(
                        visibleItems = visibleItems,
                        contentPadding = paddingValues,
                    )
                }
            }

            Grossissts_FloatingActionButtons_Grouped(
                headViewModel = initViewModel,
                modifier = modifier,
                ui_State = p3_ViewModel.uiState,
                app_Initialize_Model = initViewModel.appsHead,
            )

            GlobalActions_FloatingActionButtons_Grouped(
                modifier = modifier,
                fragment_Ui_State = p3_ViewModel.uiState,
                app_Initialize_Model = initViewModel.appsHead
            )
        }
    }
}

@Preview
@Composable
private fun Preview_Fragment_4_Main_Screen() {
    ScreenMain(modifier = Modifier.fillMaxSize())
}
