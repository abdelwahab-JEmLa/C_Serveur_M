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
import com.example.c_serveur.ViewModel.App_Initialize_ViewModel

@Composable
internal fun Fragment3_Main_Screen(
    modifier: Modifier = Modifier,
    app_Initialize_ViewModel: App_Initialize_ViewModel = viewModel(),
    p3_ViewModel: F3_ViewModel = viewModel()
) {
    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            // Main content
            Column {
                Produits_Main_List(
                    ui_State = p3_ViewModel.uiState,
                    produits_Main_DataBase=app_Initialize_ViewModel.app_Initialize_Model.produit_Main_DataBase,
                    contentPadding = paddingValues
                )
            }

            // FABs
            Grossissts_FloatingActionButtons_Grouped(
                modifier = Modifier,
                ui_State = p3_ViewModel.uiState,
                produits_Main_DataBase=app_Initialize_ViewModel.app_Initialize_Model.produit_Main_DataBase,
                )

            GlobalActions_FloatingActionButtons_Grouped(
                modifier = Modifier,
                ui_Mutable_State = p3_ViewModel.uiState,
                produits_Main_DataBase=app_Initialize_ViewModel.app_Initialize_Model.produit_Main_DataBase,
                )
        }
    }
}

@Preview
@Composable
private fun Preview_Fragment_4_Main_Screen() {
    Fragment3_Main_Screen(modifier = Modifier.fillMaxSize())
}
