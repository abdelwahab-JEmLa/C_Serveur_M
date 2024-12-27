// _2_Main_List.kt
package com.example.Packages._1.Fragment.UI

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.Packages._1.Fragment.Models.UiState
import com.example.App_Produits_Main._1.Model.AppInitializeModel

@Composable
fun Produits_Main_List(
    modifier: Modifier = Modifier,
    app_Initialize_Model: AppInitializeModel,
    ui_State: UiState,
    contentPadding: PaddingValues = PaddingValues(horizontal = 8.dp, vertical = 12.dp),
) {
    // Add debug logging for initial state
    LaunchedEffect(app_Initialize_Model.produits_Main_DataBase) {
        Log.d("ProductsList", """
            Initial State:
            Total products: ${app_Initialize_Model.produits_Main_DataBase.size}
            Filtered products: ${app_Initialize_Model.produits_Main_DataBase.count { it.auFilterFAB }}
        """.trimIndent())
    }

    // Update the filter condition to properly handle all cases
    val visibleItems = remember(app_Initialize_Model.produits_Main_DataBase) {
        app_Initialize_Model.produits_Main_DataBase.filter { product ->
            // Check if any grossist is filtered
            val anyGrossistFiltered = app_Initialize_Model.produits_Main_DataBase
                .any { it.bonCommendDeCetteCota?.grossistInformations?.auFilterFAB == true }

            if (!anyGrossistFiltered) {
                // If no grossist is filtered, show all products
                true
            } else {
                // If a grossist is filtered, only show products that match the filter
                product.auFilterFAB && product.bonCommendDeCetteCota?.let { bon ->
                    // Verify the product has quantity and matches the filtered grossist
                    bon.grossistInformations?.auFilterFAB == true &&
                            bon.coloursEtGoutsCommendee.sumOf { it.quantityAchete } > 0
                } ?: false
            }
        }.also { filtered ->
            // Add debug logging for filtered results
            Log.d("ProductsList", """
                Filtered Results:
                Visible items: ${filtered.size}
                Items with quantities: ${filtered.count { product ->
                (product.bonCommendDeCetteCota?.coloursEtGoutsCommendee?.sumOf { it.quantityAchete } ?: 0) > 0
            }}
            """.trimIndent())
        }
    }
    when (ui_State.currentMode) {
        UiState.Affichage_Et_Click_Modes.MODE_Click_Change_Position -> {
            // Ensure proper partitioning of items based on position
            val (itemsWithPosition, itemsWithoutPosition) = remember(visibleItems) {
                visibleItems.partition { produit ->
                    produit.bonCommendDeCetteCota?.let { bon ->
                        bon.position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit > 0
                    } ?: false
                }
            }

            // Sort items with position by their position value
            val sortedPositionItems = remember(itemsWithPosition) {
                itemsWithPosition.sortedBy { produit ->
                    produit.bonCommendDeCetteCota?.position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit ?: Int.MAX_VALUE
                }
            }

            // Sort items without position alphabetically
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
                // Only show section if there are items
                if (sortedPositionItems.isNotEmpty()) {
                    item(span = { GridItemSpan(5) }) {
                        Text(
                            text = "Products with Position (${sortedPositionItems.size})",
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surface)
                                .padding(16.dp),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                    items(
                        items = sortedPositionItems,
                        key = { it.nom }  // Add key for better performance
                    ) { produit ->
                        Host_Affiche_Produit_Item(
                            app_Initialize_Model = app_Initialize_Model,
                            uiState = ui_State,
                            produit = produit,
                        )
                    }
                }

                if (sortedNoPositionItems.isNotEmpty()) {
                    item(span = { GridItemSpan(5) }) {
                        Text(
                            text = "Products without Position (${sortedNoPositionItems.size})",
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surface)
                                .padding(16.dp),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                    items(
                        items = sortedNoPositionItems,
                        key = { it.nom }  // Add key for better performance
                    ) { produit ->
                        Host_Affiche_Produit_Item(
                            app_Initialize_Model = app_Initialize_Model,
                            uiState = ui_State,
                            produit = produit
                        )
                    }
                }

                // Add empty state message if no items are visible
                if (sortedPositionItems.isEmpty() && sortedNoPositionItems.isEmpty()) {
                    item(span = { GridItemSpan(5) }) {
                        Text(
                            text = "No products available for selected filter",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        UiState.Affichage_Et_Click_Modes.MODE_Affiche_Achteurs,
        UiState.Affichage_Et_Click_Modes.MODE_Affiche_Produits -> {
            val sortedItems = remember(visibleItems) {
                visibleItems.sortedWith(
                    compareBy<AppInitializeModel.ProduitModel> { produit ->
                        produit.bonCommendDeCetteCota?.position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit
                            ?: Int.MAX_VALUE
                    }.thenBy { it.nom }
                )
            }

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
                if (sortedItems.isNotEmpty()) {
                    items(
                        items = sortedItems,
                        key = { it.nom }  // Add key for better performance
                    ) { produit ->
                        Produit_Item(
                            uiState = ui_State,
                            produit = produit
                        )
                    }
                } else {
                    item {
                        Text(
                            text = "No products available for selected filter",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
