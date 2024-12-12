package com.example.Packages._3.Fragment.UI

import androidx.compose.foundation.ExperimentalFoundationApi
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
import com.example.Packages._3.Fragment.ViewModel.P3_ViewModel
import com.example.Packages._3.Fragment.Models.UiState

@Composable
internal fun Produits_Main_List(
    modifier: Modifier = Modifier,
    ui_Mutable_State: UiState,
    viewModel: P3_ViewModel,
    contentPadding: PaddingValues = PaddingValues(horizontal = 8.dp, vertical = 12.dp)
) {

    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = Color(0xE3C85858).copy(alpha = 0.1f), // Explicit Pink color
                shape = RoundedCornerShape(8.dp)
            ),
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

                items(
                    items = ui_Mutable_State.produit_DataBase,
                ) { produit ->
                    val totalQuantity = produit.grossist_Choisi_Pour_Acheter_CeProduit
                        .find { it.vid == 1L }
                        ?.colours_Et_Gouts_Commende
                        ?.sumOf { it.quantity_Achete } ?: 0
                    if(totalQuantity>0) {
                        Produit_Item(
                            uiState = ui_Mutable_State,
                            produit = produit,
                        )
                    }
                }
            }
}
