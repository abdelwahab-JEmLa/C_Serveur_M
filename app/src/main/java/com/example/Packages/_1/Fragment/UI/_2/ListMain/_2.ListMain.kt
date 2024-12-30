package com.example.Packages._1.Fragment.UI._2.ListMain

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.Apps_Head._1.Model.AppsHeadModel
import com.example.Apps_Head._2.ViewModel.InitViewModel
import com.example.Packages._1.Fragment.UI._2.ListMain.Extensions._1.DisplayGridMode.ListMain_DisplayGridMode
import com.example.Packages._1.Fragment.UI._2.ListMain.Extensions._2.DisplayListMode.ListMain_DisplayListMode
import com.example.Packages._1.Fragment.ViewModel.Models.UiState

@Composable
fun ListMain(
    appInitializeViewModel: InitViewModel,
    modifier: Modifier = Modifier,
    app_Initialize_Model: AppsHeadModel,
    ui_State: UiState,
    contentPadding: PaddingValues = PaddingValues(horizontal = 8.dp, vertical = 12.dp),
) {
    // Filter visible items based on filtered grossists and their products
    val visibleItems = remember(
        app_Initialize_Model.produits_Main_DataBase.map { it.isVisible },
    ) {
        app_Initialize_Model.produits_Main_DataBase.filter { produit ->
            produit.isVisible
        }
    }

    // Display the filtered and sorted items based on current mode
    when (ui_State.currentMode) {
        UiState.Affichage_Et_Click_Modes.MODE_Click_Change_Position -> {
            ListMain_DisplayGridMode(
                appInitializeViewModel = appInitializeViewModel,
                visibleItems = visibleItems,
                modifier = modifier,
                contentPadding = contentPadding,
                appInitializeModel = app_Initialize_Model,
                ui_State = ui_State
            )
        }

        UiState.Affichage_Et_Click_Modes.MODE_Affiche_Achteurs,
        UiState.Affichage_Et_Click_Modes.MODE_Affiche_Produits -> {
            ListMain_DisplayListMode(
                app_Initialize_Model = app_Initialize_Model,
                visibleItems = visibleItems,
                modifier = modifier,
                contentPadding = contentPadding,
                ui_State = ui_State
            )
        }
    }
}
