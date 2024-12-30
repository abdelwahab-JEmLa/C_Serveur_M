package com.example.Packages._1.Fragment.UI

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
import com.example.Apps_Head._1.Model.AppsHeadModel
import com.example.Packages._1.Fragment.ViewModel.Models.UiState
import com.google.firebase.Firebase
import com.google.firebase.database.database

private const val TAG = "ListMain_DisplayGridMode"

@Composable
internal fun ListMain(
    visibleItems: List<AppsHeadModel.ProduitModel>,
    ui_State: UiState,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues,
) {
    // Partition items based on position
    val partitionedItems by remember(visibleItems) {
        derivedStateOf {
            Log.d(TAG, "Partitioning ${visibleItems.size} items")

            visibleItems.partition { produit ->
                produit.bonCommendDeCetteCota?.position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit?.let { position ->
                    position > 0
                } ?: false
            }
        }
    }

    val (itemsWithPosition, itemsWithoutPosition) = partitionedItems

    // Sort items with position
    val sortedPositionItems by remember(itemsWithPosition) {
        derivedStateOf {
            itemsWithPosition.sortedBy { produit ->
                produit.bonCommendDeCetteCota?.position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit ?: Int.MAX_VALUE
            }
        }
    }

    // Sort items without position
    val sortedNoPositionItems by remember(itemsWithoutPosition) {
        derivedStateOf {
            itemsWithoutPosition.sortedBy { it.nom }
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
        // Display items with position
        if (sortedPositionItems.isNotEmpty()) {
            item(
                span = { GridItemSpan(5) },
            ) {
                SectionHeader(
                    text = "Products with Position (${sortedPositionItems.size})"
                )
            }
            items(items = sortedPositionItems,)
            {item->
                ItemMain(
                    item,
                    onClickDelete= { visibleItems.find { it.id == item.id }?.let { item ->
                            item.bonCommendDeCetteCota?.position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit = 0
                        }
                        Firebase.database
                            .getReference("0_UiState_3_Host_Package_3_Prototype11Dec/produit_DataBase")
                            .setValue(item)
                    },
                   onCLickOnMain =  {
                        // Find the maximum position among existing products
                        val maxPosition = visibleItems.mapNotNull {visibleItem->
                            visibleItem.bonCommendDeCetteCota?.position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit
                        }.filter { it >= 0 }.maxOrNull() ?: -1

                        Log.d("onClickMainCard", "Current max position: $maxPosition")

                        // Create or update bonCommendDeCetteCota if necessary
                        if (item.bonCommendDeCetteCota == null) {
                            item.bonCommendDeCetteCota = AppsHeadModel.ProduitModel.GrossistBonCommandes()
                        }

                        item.bonCommendDeCetteCota
                            ?.position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit =
                            maxPosition + 1

                        Firebase.database
                            .getReference("0_UiState_3_Host_Package_3_Prototype11Dec/produit_DataBase")
                            .setValue(item)
                    }
                )
            }
        }

        // Display items without position
        if (sortedNoPositionItems.isNotEmpty()) {
            item(
                span = { GridItemSpan(5) },
            ) {
                SectionHeader(
                    text = "Products without Position (${sortedNoPositionItems.size})"
                )
            }

            items(
                items = sortedNoPositionItems,
                ) { item ->
                ItemMain(
                    itemMain = item,
                )
            }
        }

        // Display empty state if no items
        if (visibleItems.isEmpty()) {
            item(
                span = { GridItemSpan(5) },
            ) {
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
@Composable
fun EmptyStateMessage() {
    Text(
        text = "No products available for selected filter",
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}
