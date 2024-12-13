package com.example.Packages._3.Fragment.UI

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.Packages._3.Fragment.Models.UiState
import com.example.Packages._3.Fragment.UI._5.Objects.DisplayeImageById

@Composable
internal fun Produit_Item_MODE_Click_Change_Position(
    uiState: UiState,
    produit: UiState.Produit_DataBase,
) {
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
                    // Get the maximum position among all products for this supplier
                    val maxPosition = uiState.produit_DataBase
                        .mapNotNull { otherProduit ->
                            otherProduit.grossist_Choisi_Pour_Acheter_CeProduit
                                .find { it.supplier_id == uiState.selectedSupplierId }
                                ?.position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit
                        }
                        .maxOrNull() ?: 0

                    // Update the position for this product
                    currentSupplier.position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit = maxPosition + 1
                }
            }
    ) {
        DisplayeImageById(
            produit_Id = produit.id,
            modifier = Modifier.fillMaxWidth(),
            reloadKey = 0
        )
    }
}
