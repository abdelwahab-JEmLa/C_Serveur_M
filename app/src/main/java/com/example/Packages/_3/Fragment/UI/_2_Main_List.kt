package com.example.Packages._3.Fragment.UI

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.Packages._3.Fragment.Models.UiState
import com.example.App_Produits_Main._1.Model.AppInitializeModel

@Composable
fun Produits_Main_List(
    modifier: Modifier = Modifier,
    app_Initialize_Model: AppInitializeModel,
    ui_State: UiState,
    contentPadding: PaddingValues = PaddingValues(horizontal = 8.dp, vertical = 12.dp),
) {
    val visibleItems = app_Initialize_Model.produits_Main_DataBase
        .filter {
            it.grossist_Pour_Acheter_Ce_Produit_Dons_Cette_Cota != null
        }

    when (ui_State.currentMode) {
        UiState.Affichage_Et_Click_Modes.MODE_Click_Change_Position -> {
            // Partition items into those with and without positions
            val (itemsWithPosition, itemsWithoutPosition) =
                visibleItems.partition { produit ->
                    val position = produit
                        .grossist_Pour_Acheter_Ce_Produit_Dons_Cette_Cota
                    ?.position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit

                position != null && position > 0
            }

            val sortedPositionItems = itemsWithPosition.sortedBy { produit ->
                produit
                    .grossist_Pour_Acheter_Ce_Produit_Dons_Cette_Cota
                    ?.position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit
                    ?: Int.MAX_VALUE
            }

            val sortedNoPositionItems = itemsWithoutPosition.sortedBy { it.nom }

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
                // First display items with positions > 0
                if (sortedPositionItems.isNotEmpty()) {
                    item(span = { GridItemSpan(5) }) {
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
                        Host_Affiche_Produit_Item(
                            app_Initialize_Model=app_Initialize_Model,
                            uiState = ui_State,
                            produit = produit,
                        )
                    }
                }

                // Then display items with no position at the bottom
                if (sortedNoPositionItems.isNotEmpty()) {
                    item(span = { GridItemSpan(5) }) {
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
                        Host_Affiche_Produit_Item(
                            app_Initialize_Model = app_Initialize_Model,
                            uiState = ui_State,
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
                        compareBy<AppInitializeModel.Produit_Model> { produit ->
                            produit
                                .grossist_Pour_Acheter_Ce_Produit_Dons_Cette_Cota
                                ?.position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit
                                ?: Int.MAX_VALUE
                        }.thenBy { it.nom }
                    )
                ) { produit ->
                    Produit_Item(
                        uiState = ui_State,
                        produit = produit
                    )
                }
            }
        }
    }
}
