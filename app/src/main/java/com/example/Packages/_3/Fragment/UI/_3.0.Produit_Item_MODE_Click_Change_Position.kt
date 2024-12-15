package com.example.Packages._3.Fragment.UI

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.Packages._3.Fragment.Models.UiState
import com.example.Packages._3.Fragment.UI._5.Objects.DisplayeImageById
import com.example.Packages._3.Fragment.ViewModel.P3_ViewModel

@Composable
internal fun Produit_Item_MODE_Click_Change_Position(
    uiState: UiState,
    produit: UiState.Produit_DataBase,
    viewModel: P3_ViewModel,
) {
    // Calculate total quantity
    val totalQuantity = produit.grossist_Choisi_Pour_Acheter_CeProduit
        .find { it.vid == 1L }
        ?.colours_Et_Gouts_Commende
        ?.sumOf { it.quantity_Achete } ?: 0

    // Get the first letter of the product name (or empty string if name is empty)
    val firstLetter = produit.nom.firstOrNull()?.toString() ?: ""

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .clickable {
                // Find the current supplier's products
                val currentSupplier = produit.grossist_Choisi_Pour_Acheter_CeProduit
                    .maxByOrNull { it.date }
                    ?.takeIf { it.supplier_id == uiState.selectedSupplierId }

                if (currentSupplier != null) {
                    fun P3_ViewModel.internal_P3_ViewModel_Fun() {
                        val maxPosition = _uiState.produit_DataBase
                            .mapNotNull { otherProduit ->
                                otherProduit.grossist_Choisi_Pour_Acheter_CeProduit
                                    .find { it.supplier_id == uiState.selectedSupplierId }
                                    ?.position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit
                            }
                            .maxOrNull() ?: 0
                        _uiState.produit_DataBase.find { it.id==produit.id }.let {
                            if (it != null) {
                                it.grossist_Choisi_Pour_Acheter_CeProduit.last().position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit=
                                    maxPosition + 1
                            }
                        }
                    }
                    viewModel.internal_P3_ViewModel_Fun()
                }
            },
        contentAlignment = Alignment.Center
    ) {
        // Overlay the first letter at top start
        Text(
            text = firstLetter,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(4.dp)
                .background(Color.LightGray.copy(alpha = 0.5f))
                .padding(4.dp),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )

        // Overlay the total quantity at top end
        Text(
            text = totalQuantity.toString(),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(4.dp)
                .background(Color.LightGray.copy(alpha = 0.5f))
                .padding(4.dp),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )

        // Original image display
        DisplayeImageById(
            produit_Id = produit.id,
            modifier = Modifier.fillMaxWidth(),
            reloadKey = 0
        )
    }
}
