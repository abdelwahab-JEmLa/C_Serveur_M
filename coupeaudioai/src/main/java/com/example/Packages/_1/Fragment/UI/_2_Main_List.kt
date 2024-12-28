// _2_Main_List.kt
package com.example.Packages._1.Fragment.UI

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.App_Produits_Main._1.Model.AppInitializeModel
import com.example.Packages._1.Fragment.Models.UiState

@Composable
internal fun List_Main(
    modifier: Modifier = Modifier,
    app_Initialize_Model: AppInitializeModel,
    ui_State: UiState,
    contentPadding: PaddingValues = PaddingValues(horizontal = 8.dp, vertical = 12.dp),
) {

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
        }
    }
    when (ui_State.currentMode) {
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
                        ItemMain(
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
        UiState.Affichage_Et_Click_Modes.MODE_Click_Change_Position -> {}

    }
}
