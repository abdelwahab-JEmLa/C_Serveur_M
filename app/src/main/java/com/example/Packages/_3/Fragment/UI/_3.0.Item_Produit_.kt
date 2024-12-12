package com.example.Packages._3.Fragment.UI

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.NotListedLocation
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.Packages._3.Fragment.Models.UiState
import com.example.Packages._3.Fragment.UI._5.Objects.DisplayeImageById

@Composable
internal fun Produit_Item(
    uiState: UiState,
    produit: UiState.Produit_DataBase,
) {
    // State to control card expansion
    var isExpanded by remember { mutableStateOf(false) }
    val heightCard = if (isExpanded) 300.dp else 100.dp

    // Calculate total quantity across all colors
    val totalQuantity = produit.grossist_Choisi_Pour_Acheter_CeProduit
        .find { it.vid == 1L }
        ?.colours_Et_Gouts_Commende
        ?.sumOf { it.quantity_Achete } ?: 0

    if (isExpanded) {
        Expanded_Item_Displaye(
            produit = produit,
            initialHeightCard = heightCard,
            onEpandToggle = {
                isExpanded = !isExpanded
            }
        )
    } else {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(heightCard)
        ) {
            // Product Image
            DisplayeImageById(
                produit_Id = produit.id.toLong(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(heightCard),
                reloadKey = 0
            )

            // Overlay for visual effect
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(color = Color.Black.copy(alpha = 0.4f))
            )

            // Highlight for non-found products
            if (produit.non_Trouve) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(color = Color(0xFFFFD700).copy(alpha = 0.7f))
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(heightCard)
            ) {
                Column(
                    modifier = Modifier
                        .width(270.dp)
                        .padding(8.dp)
                ) {
                    // Product name and total quantity row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = produit.nom,
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "Total: $totalQuantity",
                            color = Color.White,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    // Expandable grid of colors and quantities
                    Box(
                        modifier = Modifier
                            .width(200.dp)
                            .height(if (isExpanded) 280.dp else 80.dp)
                            .clickable { isExpanded = !isExpanded }
                    ) {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.spacedBy(0.dp),
                            verticalArrangement = Arrangement.spacedBy(0.dp)
                        ) {
                            // Filter and display colors with purchased quantities
                            val colorsList = produit.grossist_Choisi_Pour_Acheter_CeProduit
                                .find { it.vid == 1L }
                                ?.colours_Et_Gouts_Commende
                                ?.filter { it.quantity_Achete > 0 } ?: emptyList()

                            items(colorsList.size) { index ->
                                val colorFlavor = colorsList[index]
                                Row() {
                                    val displayText = when {
                                        colorFlavor.imogi.isNotEmpty() -> colorFlavor.imogi
                                        else -> colorFlavor.nom.take(3)
                                    }

                                    Text(
                                        text = "(${colorFlavor.quantity_Achete})$displayText",
                                        fontSize = 24.sp,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }

                // Toggle visibility button
                Row(
                    modifier = Modifier.width(70.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(
                        onClick = {
                            // Toggle the non_Trouve status
                            produit.non_Trouve = !produit.non_Trouve
                        },
                        modifier = Modifier.size(heightCard)
                    ) {
                        Icon(
                            imageVector = if (produit.non_Trouve)
                                Icons.AutoMirrored.Filled.NotListedLocation
                            else
                                Icons.Default.Visibility,
                            contentDescription = "Basculer le statut du produit",
                            tint = if (produit.non_Trouve) Color(0xFFFFD700) else Color.Green,
                        )
                    }
                }
            }
        }
    }
}
