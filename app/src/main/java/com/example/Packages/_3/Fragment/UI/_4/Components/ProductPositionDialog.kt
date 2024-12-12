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
import com.example.Packages._3.Fragment.ViewModel.P3_ViewModel

@Composable
fun ProductPositionDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    produit: UiState.Produit_DataBase,
    uiState: UiState
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Sélectionner la position") },
            text = {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(
                        count = uiState.produit_DataBase.size,
                        key = { index -> uiState.produit_DataBase[index].id }
                    ) { index ->
                        val currentProduit = uiState.produit_DataBase[index]
                        val position_Produit_Clicke = produit.grossist_Choisi_Pour_Acheter_CeProduit
                            .find { it.supplier_id == 1L }
                            ?.produit_Position_Ou_Celuila_Va_Etre_Apre_Pour_Ce_Supp ?: 1

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f)
                                .clickable {
                                    fun P3_ViewModel.func() {
                                        _uiState.produit_DataBase.find { it.id==currentProduit.id }.let { currentProduit ->
                                            currentProduit?.grossist_Choisi_Pour_Acheter_CeProduit?.find { it.vid==1L }
                                                ?.let { gro->
                                                  gro.produit_Position_Ou_Celuila_Va_Etre_Apre_Pour_Ce_Supp= position_Produit_Clicke
                                                }
                                        }
                                    }
                                    onDismiss()
                                }
                        ) {
                            DisplayeImageById(
                                produit_Id = currentProduit.id,
                                modifier = Modifier.fillMaxSize(),
                                reloadKey = 0
                            )

                            // Position overlay
                            Text(
                                text = "$position_Produit_Clicke",
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .background(MaterialTheme.colorScheme.primary)
                                    .padding(4.dp),
                                color = Color.White
                            )

                            // Product name overlay
                            Text(
                                text = currentProduit.nom,
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .background(Color.Black.copy(alpha = 0.7f))
                                    .padding(4.dp)
                                    .fillMaxWidth(),
                                color = Color.White,
                                style = MaterialTheme.typography.bodySmall
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
