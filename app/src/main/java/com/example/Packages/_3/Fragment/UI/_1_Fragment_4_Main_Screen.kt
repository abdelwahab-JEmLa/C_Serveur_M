package com.example.Packages._3.Fragment.UI

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.Packages._3.Fragment.V.FABs.Modules.GlobalActions_FloatingActionButtons_Grouped
import com.example.Packages._3.Fragment.V.FABs.Modules.Grossissts_FloatingActionButtons_Grouped
import com.example.Packages._3.Fragment.ViewModel.F3_ViewModel
import com.example.App_Produits_Main._2.ViewModel.Apps_Produits_Main_DataBase_ViewModel

@Composable
internal fun Fragment3_Main_Screen(
    modifier: Modifier = Modifier,
    app_Initialize_ViewModel: Apps_Produits_Main_DataBase_ViewModel = viewModel(),
    p3_ViewModel: F3_ViewModel = viewModel()
) {
    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            // Main content
            Column {
                Produits_Main_List(
                    app_Initialize_Model=app_Initialize_ViewModel.app_Initialize_Model,
                    ui_State = p3_ViewModel.uiState,
                    contentPadding = paddingValues
                )
            }

            // FABs
            Grossissts_FloatingActionButtons_Grouped(
                modifier = Modifier,
                ui_State = p3_ViewModel.uiState,
                app_Initialize_Model=app_Initialize_ViewModel.app_Initialize_Model,
                )

            GlobalActions_FloatingActionButtons_Grouped(
                modifier = Modifier,
                fragment_Ui_State = p3_ViewModel.uiState,
                app_Initialize_Model=app_Initialize_ViewModel.app_Initialize_Model,

                )
        }
    }
}

@Preview
@Composable
private fun Preview_Fragment_4_Main_Screen() {
    Fragment3_Main_Screen(modifier = Modifier.fillMaxSize())
}
