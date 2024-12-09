package com.example.Packages._3.Fragment.UI

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.Packages._3.Fragment.Models.Ui_Mutable_State
import com.example.Packages._3.Fragment.ViewModel.P3_ViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun Produits_Main_List(
    modifier: Modifier = Modifier,
    ui_Mutable_State: Ui_Mutable_State,
    viewModel: P3_ViewModel,
    grouped_Produits_Par_Id_Grossist: Map<Long, List<Ui_Mutable_State.Produits_Commend_DataBase>>,
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
        grouped_Produits_Par_Id_Grossist.forEach { (grossistId, produitsGroup) ->
            val filteredProducts = if (ui_Mutable_State.mode_Trie_Produit_Non_Trouve) {
                produitsGroup.filter { it.non_Trouve }
            } else {
                produitsGroup
            }

            // Split products based on position
            val productsWithPositivePosition = filteredProducts.filter {
                (it.grossist_Choisi_Pour_Acheter_CeProduit?.position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit ?: 0) > 0
            }

            val productsWithZeroOrNegativePosition = filteredProducts.filter {
                (it.grossist_Choisi_Pour_Acheter_CeProduit?.position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit ?: 0) <= 0
            }

            // Display products with positive position
            if (productsWithPositivePosition.isNotEmpty()) {
                // Only show sticky header for non-zero grossistId
                if (grossistId != 0L) {
                    stickyHeader {
                        produitsGroup.firstOrNull()?.grossist_Choisi_Pour_Acheter_CeProduit?.let {
                            Sticky_Header(lastChosenSupplier = it)
                        }
                    }
                }

                items(
                    items = productsWithPositivePosition,
                    key = { item -> "positive_${item.id}" }
                ) { produit ->
                    Produit_Item(
                        uiState = ui_Mutable_State,
                        produit = produit,
                        groupedProduits_Par_Id_Grossist = grouped_Produits_Par_Id_Grossist
                    )
                }
            }

            // Display products with zero or negative position
            if (productsWithZeroOrNegativePosition.isNotEmpty()) {
                stickyHeader {
                    Sticky_Header(
                        lastChosenSupplier = Ui_Mutable_State.Produits_Commend_DataBase.Grossist_Choisi_Pour_Acheter_CeProduit(
                            nom = "Unassigned Products",
                            couleur = "#808080"
                        )
                    )
                }

                items(
                    items = productsWithZeroOrNegativePosition,
                    key = { item -> "zero_negative_${item.id}" }
                ) { produit ->

                    ElevatedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp),
                        elevation = CardDefaults.elevatedCardElevation(
                            defaultElevation = 4.dp,
                            pressedElevation = 2.dp
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Produit_Item(
                            uiState = ui_Mutable_State,
                            produit = produit,
                            groupedProduits_Par_Id_Grossist = grouped_Produits_Par_Id_Grossist
                        )
                    }
                }
            }
        }
    }
}
