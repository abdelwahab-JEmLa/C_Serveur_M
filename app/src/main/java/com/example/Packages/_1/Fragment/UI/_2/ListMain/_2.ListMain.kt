package com.example.Packages._1.Fragment.UI._2.ListMain

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.Apps_Head._1.Model.AppInitializeModel
import com.example.Packages._1.Fragment.UI._2.ListMain.Extensions._1.DisplayGridMode.ListMain
import com.example.Packages._1.Fragment.UI._2.ListMain.Extensions._2.DisplayListMode
import com.example.Packages._1.Fragment.ViewModel.Models.UiState

@Composable
fun ListMain(
    modifier: Modifier = Modifier,
    app_Initialize_Model: AppInitializeModel,
    ui_State: UiState,
    contentPadding: PaddingValues = PaddingValues(horizontal = 8.dp, vertical = 12.dp),
) {
    // Filter visible items based on conditions
    val visibleItems = remember(app_Initialize_Model.produits_Main_DataBase) {
        app_Initialize_Model.produits_Main_DataBase.filter { product ->
            val anyGrossistFiltered = app_Initialize_Model.produits_Main_DataBase
                .any { it.bonCommendDeCetteCota?.grossistInformations?.auFilterFAB == true }

            if (!anyGrossistFiltered) {
                true
            } else {
                product.auFilterFAB && product.bonCommendDeCetteCota?.let { bon ->
                    bon.grossistInformations?.auFilterFAB == true &&
                            bon.coloursEtGoutsCommendee.sumOf { it.quantityAchete } > 0
                } ?: false
            }
        }
    }

    when (ui_State.currentMode) {
        UiState.Affichage_Et_Click_Modes.MODE_Click_Change_Position -> {
            ListMain(
                visibleItems = visibleItems,
                modifier = modifier,
                contentPadding = contentPadding,
                app_Initialize_Model = app_Initialize_Model,
                ui_State = ui_State
            )
        }

        UiState.Affichage_Et_Click_Modes.MODE_Affiche_Achteurs,
        UiState.Affichage_Et_Click_Modes.MODE_Affiche_Produits -> {
            DisplayListMode(
                app_Initialize_Model=app_Initialize_Model,
                visibleItems = visibleItems,
                modifier = modifier,
                contentPadding = contentPadding,
                ui_State = ui_State
            )
        }
    }
}

