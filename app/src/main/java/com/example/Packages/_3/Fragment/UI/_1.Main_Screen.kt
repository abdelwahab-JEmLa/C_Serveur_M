package com.example.Packages._3.Fragment.UI

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
    // Group products by their last Grossist_Choisi_Pour_Acheter_Ce_Produit_In_This_Transaction vid
    val groupedProducts = remember(p3_ViewModel.uiState.produit_DataBase) {
        p3_ViewModel.uiState.produit_DataBase.groupBy { produit ->
            produit.grossist_Choisi_Pour_Acheter_CeProduit
                .maxByOrNull { it.date }?.vid ?: -1L
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            // Main content
            Column {
                Produits_Main_List(
                    ui_State = p3_ViewModel.uiState,
                    viewModel = p3_ViewModel,
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
