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
fun Host_Affiche_Produit_Item(
    app_Initialize_Model: App_Initialize_Model,
    uiState: UiState,
    produit: App_Initialize_Model.Produit_Main_DataBase,
) {
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .background(
                color = if (produit.grossist_Pour_Acheter_Ce_Produit_Dons_Cette_Cota?.position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit != null)
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                else
                    MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(4.dp)
            )
            .clickable {
                coroutineScope.launch {
                    // Find the maximum position across all products
                    val maxPosition = app_Initialize_Model.produits_Main_DataBase
                        .mapNotNull { it.grossist_Pour_Acheter_Ce_Produit_Dons_Cette_Cota }
                        .maxOfOrNull { it.position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit }
                        ?: 0

                    // Create new position as max + 1
                    val newPosition = maxPosition + 1

                    // Update the current product's position
                    produit.grossist_Pour_Acheter_Ce_Produit_Dons_Cette_Cota?.position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit = newPosition

                    // Update Firebase
                    app_Initialize_Model.update_Produits_FireBase()
                }
            },
        contentAlignment = Alignment.Center
    ) {
        // Rest of the composable implementation remains the same...
        Glide_Display_Image_By_Id(
            produit_Id = produit.id,
            produit_Image_Need_Update = produit.it_Image_besoin_To_Be_Updated,
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            reloadKey = 0
        )

        IconButton(
            onClick = {
                coroutineScope.launch {
                    produit.grossist_Pour_Acheter_Ce_Produit_Dons_Cette_Cota?.let { supplier ->
                        supplier.position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit = 0
                    }
                    app_Initialize_Model.update_Produits_FireBase()
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

        produit.grossist_Pour_Acheter_Ce_Produit_Dons_Cette_Cota?.position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit?.let { position ->
            if (position != 0) {
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
}
