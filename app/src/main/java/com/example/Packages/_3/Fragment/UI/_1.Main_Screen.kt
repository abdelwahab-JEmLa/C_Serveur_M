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
import com.example.Packages._3.Fragment.ViewModel.P3_ViewModel

@Composable
internal fun Fragment3_Main_Screen(
    modifier: Modifier = Modifier,
    p3_ViewModel: P3_ViewModel = viewModel()
) {


    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            // Main content
            Column {
                Produits_Main_List(
                    ui_State = p3_ViewModel.uiState,
                    contentPadding = paddingValues
                )
            }

            // FABs
            Grossissts_FloatingActionButtons_Grouped(
                modifier = Modifier,
                ui_State = p3_ViewModel.uiState,
            )

            GlobalActions_FloatingActionButtons_Grouped(
                modifier = Modifier,
                ui_Mutable_State = p3_ViewModel.uiState,
            )
        }
    }
}

@Preview
@Composable
private fun MainScreenPreview() {
    Fragment3_Main_Screen(modifier = Modifier.fillMaxSize())
}
