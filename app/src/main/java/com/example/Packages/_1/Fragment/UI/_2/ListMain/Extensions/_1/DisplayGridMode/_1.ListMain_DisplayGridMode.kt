package com.example.Packages._1.Fragment.UI._2.ListMain.Extensions._1.DisplayGridMode

import android.util.Log
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.Apps_Head._1.Model.AppInitializeModel
import com.example.Apps_Head._2.ViewModel.AppInitialize_ViewModel
import com.example.Packages._1.Fragment.UI._2.ListMain.Extensions._2.DisplayListMode.EmptyStateMessage
import com.example.Packages._1.Fragment.ViewModel.Models.UiState

private const val TAG = "ListMain_DisplayGridMode"

@Composable
internal fun ListMain_DisplayGridMode(
    appInitializeViewModel: AppInitialize_ViewModel,
    appInitializeModel: AppInitializeModel,
    visibleItems: List<AppInitializeModel.ProduitModel>,
    modifier: Modifier,
    contentPadding: PaddingValues,
    ui_State: UiState
) {
    val partitionedItems by remember(visibleItems) {
        derivedStateOf {
            Log.d(TAG, "Recalculating partitions for ${visibleItems.size} items")

            visibleItems.partition { produit ->
                val position = produit.bonCommendDeCetteCota?.position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit
                val hasPosition = position != null && position > 0
                Log.d(TAG, "Product ${produit.id} (${produit.nom}) has position: $hasPosition ($position)")
                hasPosition
            }
        }
    }

    val (itemsWithPosition, itemsWithoutPosition) = partitionedItems

    val sortedPositionItems by remember(itemsWithPosition) {
        derivedStateOf {
            itemsWithPosition.sortedBy { produit ->
                produit.bonCommendDeCetteCota?.position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit
                    ?: Int.MAX_VALUE
            }.also { sorted ->
                Log.d(TAG, "Sorted items with position: ${sorted.map { "${it.nom}:${it.bonCommendDeCetteCota?.position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit}" }}")
            }
        }
    }

    val sortedNoPositionItems by remember(itemsWithoutPosition) {
        derivedStateOf {
            itemsWithoutPosition.sortedBy { it.nom }
                .also { sorted ->
                    Log.d(TAG, "Sorted items without position: ${sorted.map { it.nom }}")
                }
        }
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
        if (sortedPositionItems.isNotEmpty()) {
            item(span = { GridItemSpan(5) }) {
                SectionHeader(
                    text = "Products with Position (${sortedPositionItems.size})"
                )
            }

            items(
                items = sortedPositionItems,
                key = { "${it.id}_${it.bonCommendDeCetteCota?.position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit}" }
            ) { produit ->
                ItemMain_Grid(
                    appInitializeViewModel = appInitializeViewModel,
                    appInitializeModel = appInitializeModel,
                    produit = produit,
                )
            }
        }

        if (sortedNoPositionItems.isNotEmpty()) {
            item(span = { GridItemSpan(5) }) {
                SectionHeader(
                    text = "Products without Position (${sortedNoPositionItems.size})"
                )
            }

            items(
                items = sortedNoPositionItems,
                key = { "np_${it.id}" }
            ) { produit ->
                ItemMain_Grid(
                    appInitializeModel = appInitializeModel,
                    produit = produit,
                    appInitializeViewModel = appInitializeViewModel
                )
            }
        }

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
