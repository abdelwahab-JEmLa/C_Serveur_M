package com.example.Packages._3.Fragment.UI

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.Packages._3.Fragment.Models.UiState
import com.example.App_Produits_Main._3.Modules.Images_Handler.Glide_Display_Image_By_Id
import com.example.App_Produits_Main._1.Model.App_Initialize_Model
import kotlinx.coroutines.launch

@Composable
fun Produit_Item_MODE_Click_Change_Position(
    uiState: UiState,
    produit: App_Initialize_Model.Produit_Main_DataBase,
    produits_Main_DataBase: SnapshotStateList<App_Initialize_Model.Produit_Main_DataBase>,
) {
    val coroutineScope = rememberCoroutineScope()

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
                coroutineScope.launch {
                    val currentSupplier = produit.grossist_Choisi_Pour_Acheter_CeProduit
                        .find { it.supplier_id == uiState.selectedSupplierId }

                    currentSupplier?.let { supplier ->
                        val allPositions = produits_Main_DataBase.flatMap { prod ->
                            prod.grossist_Choisi_Pour_Acheter_CeProduit
                                .filter { it.supplier_id == uiState.selectedSupplierId }
                                .map { it.position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit }
                        }

                        val maxPosition = allPositions.maxOrNull() ?: 0
                        val newPosition = maxPosition + 1

                        supplier.position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit = newPosition
                        supplier.position_Grossist_Don_Parent_Grossists_List = newPosition
                    }
                }
            },
        contentAlignment = Alignment.Center
    ) {

        Glide_Display_Image_By_Id(
            produit_Id = produit.id,
            produit_Image_Need_Update = produit.it_Image_besoin_To_Be_Updated,
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            reloadKey = 0
        )
        // Delete button at top start
        IconButton(
            onClick = {
                coroutineScope.launch {
                    produit.grossist_Choisi_Pour_Acheter_CeProduit
                        .find { it.supplier_id == uiState.selectedSupplierId }
                        ?.let { supplier ->
                            supplier.position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit = 0
                            supplier.position_Grossist_Don_Parent_Grossists_List = 0
                        }
                }
            },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(4.dp)
                .size(24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Remove position",
                tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
            )
        }

        Text(
            text = "ID: ${produit.id}",
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(4.dp)
                .background(
                    color = Color.LightGray.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(4.dp)
                )
                .padding(4.dp),
            style = MaterialTheme.typography.bodySmall,
            fontSize = 8.sp
        )

        Text(
            text = produit.nom.firstOrNull()?.toString() ?: "",
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(4.dp)
                .background(
                    color = Color.LightGray.copy(alpha = 0.7f),
                    shape = RoundedCornerShape(4.dp)
                )
                .padding(4.dp),
            style = MaterialTheme.typography.bodyLarge
        )

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
