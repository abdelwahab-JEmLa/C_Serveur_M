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
import com.example.Packages._3.Fragment.Models.Ui_Mutable_State
import com.example.Packages._3.Fragment.UI._5.Objects.DisplayeImageById

@Composable
internal fun Produit_Item(
    uiState: Ui_Mutable_State,
    produit: Ui_Mutable_State.Produits_Commend_DataBase,
    groupedProduits_Par_Id_Grossist: Map<Long, List<Ui_Mutable_State.Produits_Commend_DataBase>>,
) {
    var isExpanded by remember { mutableStateOf(false) }
    val heightCard = if (isExpanded) 300.dp else 100.dp

    // Calculate total quantity
    val totalQuantity = produit.colours_Et_Gouts_Commende?.sumOf { it.quantity_Achete } ?: 0

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
            DisplayeImageById(
                produit_Id = produit.id.toLong(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(heightCard),
                reloadKey = 0
            )

            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(color = Color.Black.copy(alpha = 0.4f))
            )
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
                        .clickable {
                            handleProductPositionUpdate(
                                produit,
                                groupedProduits_Par_Id_Grossist,
                                uiState
                            )
                        }
                ) {
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
                            items(
                                produit.colours_Et_Gouts_Commende?.filter { it.quantity_Achete > 0 }?.size
                                    ?: 0
                            ) { index ->
                                val colorFlavor =
                                    produit.colours_Et_Gouts_Commende?.filter { it.quantity_Achete > 0 }
                                        ?.get(index)
                                colorFlavor?.let {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        val displayText = when {
                                            it.imogi.isNotEmpty() -> it.imogi
                                            else -> it.nom.take(2)
                                        }
                                        Text(
                                            text = "$displayText>(${it.quantity_Achete})",
                                            fontSize = 24.sp,
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.width(70.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    val position =
                        produit.grossist_Choisi_Pour_Acheter_CeProduit?.position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit
                            ?: 0
                    Text(
                        text = "${if (position == 0) "" else position}",
                        fontSize = 80.sp,
                        color = Color.White
                    )
                    IconButton(
                        onClick = {
                            val updatedProduit = produit.copy(non_Trouve = !produit.non_Trouve)
                            uiState.update_Ui_Mutable_State_C_produits_Commend_DataBase(updatedProduit)
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

// Extracted function to handle product position update
private fun handleProductPositionUpdate(
    produit: Ui_Mutable_State.Produits_Commend_DataBase,
    groupedProduits_Par_Id_Grossist: Map<Long, List<Ui_Mutable_State.Produits_Commend_DataBase>>,
    uiState: Ui_Mutable_State
) {
    val supplierGroup = groupedProduits_Par_Id_Grossist[produit.grossist_Choisi_Pour_Acheter_CeProduit?.id ?: 0]
        ?: emptyList()
    val maxPosition = supplierGroup.maxOfOrNull {
        it.grossist_Choisi_Pour_Acheter_CeProduit?.position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit
            ?: 0
    } ?: 0
    val updatedProduit = produit.copy(
        grossist_Choisi_Pour_Acheter_CeProduit = produit.grossist_Choisi_Pour_Acheter_CeProduit?.copy(
            position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit = maxPosition + 1
        )
    )
    updatedProduit.updateSelf(uiState)
}
