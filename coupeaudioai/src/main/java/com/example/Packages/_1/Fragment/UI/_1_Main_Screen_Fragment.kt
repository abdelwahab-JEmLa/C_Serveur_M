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
import com.example.Packages._1.Fragment.V.FABs.Modules.GlobalActions_FloatingActionButtons_Grouped
import com.example.Packages._1.Fragment.V.FABs.Modules.Grossissts_FloatingActionButtons_Grouped
import com.example.Packages._1.Fragment.ViewModel.ViewModel_Fragment
import com.example.App_Produits_Main._2.ViewModel.Apps_Produits_Main_DataBase_ViewModel

@Composable
internal fun Main_Screen_Fragment(
    modifier: Modifier = Modifier,
    app_Initialize_ViewModel: Apps_Produits_Main_DataBase_ViewModel = viewModel(),
    viewModel: ViewModel_Fragment = viewModel()
) {
    if (!app_Initialize_ViewModel.initializationComplete) {
        Box(modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator(
                progress = {
                    app_Initialize_ViewModel.initializationProgress
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
                val databaseSize = app_Initialize_ViewModel.app_Initialize_Model.produits_Main_DataBase.size

                if (databaseSize > 0) {
                    List_Main(
                        app_Initialize_Model = app_Initialize_ViewModel.app_Initialize_Model,
                        ui_State = viewModel.uiState,
                        contentPadding = paddingValues
                    )
                }
            }

            Grossissts_FloatingActionButtons_Grouped(
                modifier = Modifier,
                ui_State = viewModel.uiState,
                app_Initialize_Model = app_Initialize_ViewModel.app_Initialize_Model,
            )

            GlobalActions_FloatingActionButtons_Grouped(
                modifier = Modifier,
                fragment_Ui_State = viewModel.uiState,
                app_Initialize_Model = app_Initialize_ViewModel.app_Initialize_Model,
            )
        }
    }
}

@Preview
@Composable
private fun Preview_Fragment_4_Main_Screen() {
    Main_Screen_Fragment(modifier = Modifier.fillMaxSize())
}
