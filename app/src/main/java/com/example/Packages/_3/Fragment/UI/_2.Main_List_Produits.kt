package com.example.Packages._3.Fragment.UI


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.Packages._3.Fragment.Models.UiState
import com.example.Packages._3.Fragment.ViewModel.P3_ViewModel

@Composable
internal fun Produits_Main_List(
    modifier: Modifier = Modifier,
    ui_Mutable_State: UiState,
    viewModel: P3_ViewModel,
    contentPadding: PaddingValues = PaddingValues(horizontal = 8.dp, vertical = 12.dp)
) {
    // First filter the items with quantity > 0
    val visibleItems = ui_Mutable_State.produit_DataBase.filter { produit ->
        val totalQuantity = produit.grossist_Choisi_Pour_Acheter_CeProduit
            .find { it.vid == 1L }
            ?.colours_Et_Gouts_Commende
            ?.sumOf { it.quantity_Achete } ?: 0
        totalQuantity > 0
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
        items(
            items = visibleItems,
        ) { produit ->
            Produit_Item(
                uiState = ui_Mutable_State,
                produit = produit,
            )
        }
    }
}
