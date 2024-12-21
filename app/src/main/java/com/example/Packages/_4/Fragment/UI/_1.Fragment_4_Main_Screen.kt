package com.example.Packages._4.Fragment.UI

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.Packages._4.Fragment.UI._Y.Modules.Globale_Edites_GroupedFloatingActionButtons
import com.example.Packages._4.Fragment.UI._Y.Modules.Second_Grouped_FloatingActionButtons
import com.example.Packages._4.Fragment._1.Main.ViewModel.F4_ViewModel
import com.example.Apps_Produits_Main_DataBase._2.ViewModel.App_Initialize_ViewModel

@Composable
fun Fragment_4_Main_Screen(
    modifier: Modifier = Modifier,
    app_Initialize_ViewModel: App_Initialize_ViewModel = viewModel(),
    f4_ViewModel: F4_ViewModel = viewModel()
) {
    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            // Main content
            Column {
                Main_List(
                    contentPadding = paddingValues,
                    uiState = f4_ViewModel.uiState,
                    produit_Main_DataBase = app_Initialize_ViewModel.app_Initialize_Model.produit_Main_DataBase
                )
            }

            Globale_Edites_GroupedFloatingActionButtons(
                modifier = Modifier,
                uiState = f4_ViewModel.uiState,
            )

            Second_Grouped_FloatingActionButtons(
                modifier = Modifier,
                uiState = f4_ViewModel.uiState,
                produit_Main_DataBase = app_Initialize_ViewModel.app_Initialize_Model.produit_Main_DataBase
            )
        }
    }
}

@Preview
@Composable
private fun Preview_Fragment_4_Main_Screen() {
    Fragment_4_Main_Screen(modifier = Modifier.fillMaxSize())
}
