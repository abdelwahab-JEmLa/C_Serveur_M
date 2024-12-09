package com.example.Packages._3.Fragment.UI

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.Packages._3.Fragment.Models.Ui_Mutable_State
import com.example.Packages._3.Fragment.UI._5.Objects.DisplayeImageById

@Composable
internal fun Expanded_Item_Displaye(
    produit: Ui_Mutable_State.Produits_Commend_DataBase,
    initialHeightCard: Dp = 300.dp,
    onEpandToggle: () -> Unit
) {
    val filteredColorFlavors = produit.colours_Et_Gouts_Commende
        ?.filter { it.quantity_Achete > 0 }
        ?: emptyList()

    Box(
        modifier = Modifier
            .size(initialHeightCard)
            .animateContentSize()
            .clickable { onEpandToggle() }
    ) {
        LazyRow(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(filteredColorFlavors) { colorFlavor ->
                ElevatedCard(
                    modifier = Modifier
                        .padding(4.dp)
                        .fillMaxWidth()
                ) {
                    Box {
                        DisplayeImageById(
                            produit_Id = produit.id.toLong(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(initialHeightCard),
                            index = filteredColorFlavors.indexOf(colorFlavor),
                            reloadKey = 0,
                            height_Defini = initialHeightCard,
                            width_Defini = initialHeightCard // Let it fill width naturally
                        )

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.align(Alignment.Center)
                        ) {
                            Row {
                                Text(
                                    text = colorFlavor.nom,
                                    fontSize = 30.sp,
                                    color = Color.White
                                )
                                Text(
                                    text = colorFlavor.imogi,
                                    fontSize = 30.sp,
                                    color = Color.White
                                )
                            }
                            Text(
                                text = colorFlavor.quantity_Achete.toString(),
                                fontSize = 30.sp,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}
