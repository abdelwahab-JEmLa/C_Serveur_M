package com.example.Packages._3.Fragment.UI._4.Components
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.Packages._3.Fragment.Models.UiState
import com.example.Packages._3.Fragment.UI._5.Objects.DisplayeImageById

@Composable
fun ProductPositionDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    produit: UiState.Produit_DataBase,
    onPositionUpdate: (Int) -> Unit,
    uiState: UiState
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Sélectionner la position") },
            text = {
                // Filter products where supplier (supp) = 1
                val supplierProducts = produit.grossist_Choisi_Pour_Acheter_CeProduit
                    .filter { it.supplier_id == 1L }

                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(supplierProducts.size) { index ->
                        val supplierProduct = supplierProducts[index]
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f)
                                .clickable {
                                    onPositionUpdate(supplierProduct.produit_Position_Ou_Celuila_Va_Etre_Apre_Pour_Ce_Supp)
                                    onDismiss()
                                }
                        ) {
                            DisplayeImageById(
                                produit_Id = produit.id,
                                modifier = Modifier.fillMaxSize(),
                                reloadKey = 0
                            )
                            // Position overlay
                            Text(
                                text = "${supplierProduct.produit_Position_Ou_Celuila_Va_Etre_Apre_Pour_Ce_Supp}",
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .background(MaterialTheme.colorScheme.primary)
                                    .padding(4.dp),
                                color = Color.White
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = onDismiss) {
                    Text("Fermer")
                }
            }
        )
    }
}
