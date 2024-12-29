package com.example.Packages._1.Fragment.UI._2.ListMain.Extensions._2

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
import com.example.Apps_Head._1.Model.AppInitializeModel
import com.example.Packages._1.Fragment.ViewModel.Models.UiState
import java.util.UUID

@Composable
fun DisplayListMode(
    app_Initialize_Model: AppInitializeModel,
    visibleItems: List<AppInitializeModel.ProduitModel>,
    modifier: Modifier,
    contentPadding: PaddingValues,
    ui_State: UiState
) {
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
                key = { "${it.nom}_${it.bonCommendDeCetteCota?.position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit}_${UUID.randomUUID()}" }
            ) { produit ->
                Item(
                    app_Initialize_Model=app_Initialize_Model,
                    produit = produit
                )
            }
        } else {
            item {
                EmptyStateMessage()
            }
        }
    }
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
