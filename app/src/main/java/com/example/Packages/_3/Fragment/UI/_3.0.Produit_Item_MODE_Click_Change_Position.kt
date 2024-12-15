package com.example.Packages._3.Fragment.UI

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
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

@Composable
fun Produit_Item_MODE_Click_Change_Position(
    uiState: UiState,
    produit: UiState.Produit_DataBase,
) {
    val currentPosition = produit.grossist_Choisi_Pour_Acheter_CeProduit
        .find { it.supplier_id == uiState.selectedSupplierId }
        ?.position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .background(
                color = if (currentPosition != null)
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                else
                    MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(4.dp)
            )
            .clickable {
                val currentSupplier = produit.grossist_Choisi_Pour_Acheter_CeProduit
                    .find { it.supplier_id == uiState.selectedSupplierId }

                if (currentSupplier != null) {
                    val maxPosition = uiState.produit_DataBase
                        .mapNotNull { otherProduit ->
                            otherProduit.grossist_Choisi_Pour_Acheter_CeProduit
                                .find { it.supplier_id == uiState.selectedSupplierId }
                                ?.position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit
                        }
                        .maxOrNull() ?: 0

                    currentSupplier.position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit = maxPosition + 1
                    currentSupplier.position_Grossist_Don_Parent_Grossists_List = maxPosition + 1
                }
            },
        contentAlignment = Alignment.Center
    ) {
        // Display product image
        DisplayeImageById(
            produit_Id = produit.id,
            modifier = Modifier.fillMaxWidth(),
            reloadKey = 0
        )

        // Display first letter of product name
        Text(
            text = produit.nom.firstOrNull()?.toString() ?: "",
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(4.dp)
                .background(
                    color = Color.LightGray.copy(alpha = 0.7f),
                    shape = RoundedCornerShape(4.dp)
                )
                .padding(4.dp),
            style = MaterialTheme.typography.bodyLarge
        )

        // Display position if available
        currentPosition?.let { position ->
            Text(
                text = position.toString(),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(4.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(4.dp)
                    )
                    .padding(4.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
