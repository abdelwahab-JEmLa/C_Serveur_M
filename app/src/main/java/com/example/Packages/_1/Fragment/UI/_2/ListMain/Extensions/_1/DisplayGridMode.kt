package com.example.Packages._1.Fragment.UI._2.ListMain.Extensions._1

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.Apps_Head._1.Model.AppInitializeModel
import com.example.Packages._1.Fragment.UI._2.ListMain.Extensions._2.EmptyStateMessage
import com.example.Packages._1.Fragment.UI._2.ListMain.Extensions._2.ItemDisplayListMode
import com.example.Packages._1.Fragment.ViewModel.Models.UiState
import java.util.UUID

@Composable
fun DisplayGridMode(
    visibleItems: List<AppInitializeModel.ProduitModel>,
    modifier: Modifier,
    contentPadding: PaddingValues,
    app_Initialize_Model: AppInitializeModel,
    ui_State: UiState
) {
    val (itemsWithPosition, itemsWithoutPosition) = remember(visibleItems) {
        visibleItems.partition { produit ->
            produit.bonCommendDeCetteCota?.let { bon ->
                bon.position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit > 0
            } ?: false
        }
    }

    val sortedPositionItems = remember(itemsWithPosition) {
        itemsWithPosition.sortedBy { produit ->
            produit.bonCommendDeCetteCota?.position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit
                ?: Int.MAX_VALUE
        }
    }

    val sortedNoPositionItems = remember(itemsWithoutPosition) {
        itemsWithoutPosition.sortedBy { it.nom }
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(5),
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
        // Section for items with position
        if (sortedPositionItems.isNotEmpty()) {
            item(span = { GridItemSpan(5) }) {
                SectionHeader(
                    text = "Products with Position (${sortedPositionItems.size})"
                )
            }

            items(
                items = sortedPositionItems,
                key = { "${it.nom}_${it.bonCommendDeCetteCota?.position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit}_${UUID.randomUUID()}" }
            ) { produit ->
                ItemDisplayListMode(
                    app_Initialize_Model = app_Initialize_Model,
                    uiState = ui_State,
                    produit = produit,
                )
            }
        }

        // Section for items without position
        if (sortedNoPositionItems.isNotEmpty()) {
            item(span = { GridItemSpan(5) }) {
                SectionHeader(
                    text = "Products without Position (${sortedNoPositionItems.size})"
                )
            }

            items(
                items = sortedNoPositionItems,
                key = { "${it.nom}_no_position_${UUID.randomUUID()}" }
            ) { produit ->
                ItemDisplayListMode(
                    app_Initialize_Model = app_Initialize_Model,
                    uiState = ui_State,
                    produit = produit
                )
            }
        }

        // Empty state message
        if (sortedPositionItems.isEmpty() && sortedNoPositionItems.isEmpty()) {
            item(span = { GridItemSpan(5) }) {
                EmptyStateMessage()
            }
        }
    }
}

@Composable
private fun SectionHeader(text: String) {
    Text(
        text = text,
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp),
        style = MaterialTheme.typography.titleMedium
    )
}
