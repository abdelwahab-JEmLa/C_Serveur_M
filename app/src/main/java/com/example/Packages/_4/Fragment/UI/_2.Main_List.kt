package com.example.Packages._3.Fragment.UI

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.Packages._3.Fragment.Models.UiState

@Composable
fun Main_List(
    modifier: Modifier = Modifier,
    uiState: UiState,
    contentPadding: PaddingValues = PaddingValues(horizontal = 8.dp, vertical = 12.dp)
) {
    val visibleItems = uiState.produit_DataBase
        .filter { produit ->
            val totalQuantity = produit.grossist_Choisi_Pour_Acheter_CeProduit
                .flatMap { it.colours_Et_Gouts_Commende }
                .sumOf { it.quantity_Achete }

            val supplierMatch = if (uiState.selectedSupplierId != 0L) {
                produit.grossist_Choisi_Pour_Acheter_CeProduit.any {
                    it.supplier_id == uiState.selectedSupplierId
                }
            } else true

            totalQuantity > 0 && supplierMatch
        }

    when (uiState.currentMode) {
        UiState.Affichage_Et_Click_Modes.MODE_Click_Change_Position -> {
            // Partition items into those with and without positions
            val (itemsWithPosition, itemsWithoutPosition) = visibleItems.partition { produit ->
                val position = produit.grossist_Choisi_Pour_Acheter_CeProduit
                    .find { it.supplier_id == uiState.selectedSupplierId }
                    ?.position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit

                position != null && position > 0
            }

            // Sort items with positions by their position value (ascending)
            val sortedPositionItems = itemsWithPosition.sortedBy { produit ->
                produit.grossist_Choisi_Pour_Acheter_CeProduit
                    .find { it.supplier_id == uiState.selectedSupplierId }
                    ?.position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit ?: Int.MAX_VALUE
            }

            // Sort items without position by name
            val sortedNoPositionItems = itemsWithoutPosition.sortedBy { it.nom }

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
                // First display items with positions > 0
                if (sortedPositionItems.isNotEmpty()) {
                    item(span = { GridItemSpan(4) }) {
                        Text(
                            text = "Products with Position",
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surface)
                                .padding(16.dp),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                    items(sortedPositionItems) { produit ->
                        Main_Item_Second_Type_D_Affiche(
                            uiState = uiState,
                            produit = produit
                        )
                    }
                }

                // Then display items with no position at the bottom
                if (sortedNoPositionItems.isNotEmpty()) {
                    item(span = { GridItemSpan(4) }) {
                        Text(
                            text = "Products without Position",
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surface)
                                .padding(16.dp),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                    items(sortedNoPositionItems) { produit ->
                        Main_Item_Second_Type_D_Affiche(
                            uiState = uiState,
                            produit = produit
                        )
                    }
                }
            }
        }

        UiState.Affichage_Et_Click_Modes.MODE_Affiche_Achteurs,
        UiState.Affichage_Et_Click_Modes.MODE_Affiche_Produits -> {
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
                items(
                    visibleItems.sortedWith(
                        compareBy<UiState.Produit_DataBase> { produit ->
                            produit.grossist_Choisi_Pour_Acheter_CeProduit
                                .maxByOrNull { it.date }
                                ?.position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit
                                ?: Int.MAX_VALUE
                        }.thenBy { it.nom }
                    )
                ) { produit ->
                    Main_Item(
                        uiState = uiState,
                        produit = produit
                    )
                }
            }
        }
    }
}
