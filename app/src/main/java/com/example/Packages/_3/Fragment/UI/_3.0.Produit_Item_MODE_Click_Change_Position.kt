package com.example.Packages._3.Fragment.UI

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.Packages._3.Fragment.Models.UiState
import com.example.Packages._3.Fragment.UI._5.Objects.DisplayeImageById


// In _3.0.Produit_Item_MODE_Click_Change_Position.kt
@Composable
internal fun Produit_Item_MODE_Click_Change_Position(
    uiState: UiState,
    produit: UiState.Produit_DataBase,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                // Fixed item position update logic
                val supplierProducts = uiState.produit_DataBase.filter {
                    it.grossist_Choisi_Pour_Acheter_CeProduit.any { supplier ->
                        supplier.vid == 1L
                    }
                }

                val maxPosition = supplierProducts.maxOfOrNull { item ->
                    item.grossist_Choisi_Pour_Acheter_CeProduit
                        .find { it.vid == 1L }
                        ?.position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit
                } ?: 0

                // Find and update the supplier's position
                produit.grossist_Choisi_Pour_Acheter_CeProduit
                    .find { it.vid == 1L }
                    ?.let { supplier ->
                        supplier.position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit = maxPosition + 1
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
