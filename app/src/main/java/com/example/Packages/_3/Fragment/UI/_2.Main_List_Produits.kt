package com.example.Packages._3.Fragment.UI

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.Packages._3.Fragment.Models.UiState
import com.example.Packages._3.Fragment.ViewModel.P3_ViewModel

// In _2.Main_List_Produits.kt
@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun Produits_Main_List(
    modifier: Modifier = Modifier,
    ui_State: UiState,
    viewModel: P3_ViewModel,
    contentPadding: PaddingValues = PaddingValues(horizontal = 8.dp, vertical = 12.dp)
) {
    val visibleItems = ui_State.produit_DataBase
        .filter { produit ->
            val totalQuantity = produit.grossist_Choisi_Pour_Acheter_CeProduit
                .find { it.vid == 1L }
                ?.colours_Et_Gouts_Commende
                ?.sumOf { it.quantity_Achete } ?: 0

            // Filter by selectedSupplierId if not 0 (default)
            val supplierMatch = if (ui_State.selectedSupplierId != 0L) {
                produit.grossist_Choisi_Pour_Acheter_CeProduit.any {
                    it.supplier_id == ui_State.selectedSupplierId
                }
            } else true

            totalQuantity > 0 && supplierMatch
        }
        .sortedWith(compareBy<UiState.Produit_DataBase> { produit ->
            // Get position for the most recent supplier
            produit.grossist_Choisi_Pour_Acheter_CeProduit
                .maxByOrNull { it.date }
                ?.position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit
                ?: Int.MAX_VALUE
        }.thenBy { produit ->
            // Secondary sort by name for items without position
            val hasPosition = produit.grossist_Choisi_Pour_Acheter_CeProduit
                .maxByOrNull { it.date }
                ?.position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit

            if (hasPosition == null) produit.nom else ""
        })

    if (ui_State.currentMode == UiState.Affichage_Et_Click_Modes.MODE_Click_Change_Position) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            modifier = modifier
                .fillMaxWidth()
                .background(
                    color = Color(0xE3C85858).copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                ),
            contentPadding = contentPadding,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(visibleItems) { produit ->
                Produit_Item_MODE_Click_Change_Position(
                    uiState = ui_State,
                    produit = produit, viewModel=viewModel
                )
            }
        }
    } else {
        LazyColumn(
            modifier = modifier
                .fillMaxWidth()
                .background(
                    color = Color(0xE3C85858).copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                ),
            contentPadding = contentPadding,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Add sticky headers for positioned and non-positioned items
            var currentHasPosition: Boolean? = null

            visibleItems.forEach { produit ->
                val hasPosition = produit.grossist_Choisi_Pour_Acheter_CeProduit
                    .find { it.vid == 1L }
                    ?.position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit != null

                if (hasPosition != currentHasPosition) {
                    stickyHeader {
                        Text(
                            text = if (hasPosition) "Positioned Items" else "Non-Positioned Items",
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surface)
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    currentHasPosition = hasPosition
                }

                item {
                    Produit_Item(
                        uiState = ui_State,
                        produit = produit,
                    )
                }
            }
        }
    }
}
