package com.example.Packages._3.Fragment.UI

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.Packages._3.Fragment.V.FABs.Modules.Grouped_FloatingActionButtons
import com.example.Packages._3.Fragment.V.FABs.Modules.Second_Grouped_FloatingActionButtons
import com.example.Packages._3.Fragment.ViewModel.F4_ViewModel

@Composable
internal fun Fragment_4_Main_Screen(
    modifier: Modifier = Modifier,
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
                    uiState = f4_ViewModel.app_Initialize_Model.produit_Main_DataBase
                )
            }

            Grouped_FloatingActionButtons(
                modifier = Modifier,
                uiState = f4_ViewModel.uiState,
            )

            Second_Grouped_FloatingActionButtons(
                modifier = Modifier,
                uiState = f4_ViewModel.uiState,
            )
        }
    }
}

@Preview
@Composable
private fun Preview_Fragment_4_Main_Screen() {
    Fragment_4_Main_Screen(modifier = Modifier.fillMaxSize())
}
