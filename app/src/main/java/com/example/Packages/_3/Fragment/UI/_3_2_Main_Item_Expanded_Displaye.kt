package com.example.Packages._3.Fragment.UI

import androidx.compose.animation.animateContentSize
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.Apps_Produits_Main_DataBase._1.Images_Handler.Display_Image_By_Id
import com.example.Apps_Produits_Main_DataBase._2.ViewModel.Model.App_Initialize_Model

@Composable
internal fun Expanded_Item_Displaye(
    produit: App_Initialize_Model.Produit_Main_DataBase,
    initialHeightCard: Dp = 300.dp,
    onEpandToggle: () -> Unit
) {
    val filteredColorFlavors = produit.grossist_Choisi_Pour_Acheter_CeProduit
        .find { it.vid == 1L }
        ?.colours_Et_Gouts_Commende?.filter { it.quantity_Achete > 0 }
        ?: emptyList()

    Box(
        modifier = Modifier
            .height(initialHeightCard)
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
                        Display_Image_By_Id(
                            produit_Id = produit.id + 2000,
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
                            ElevatedCard(
                                modifier = Modifier
                                    .padding(4.dp)
                                    .fillMaxWidth()
                            ) {
                                Row( modifier = Modifier
                                    .background(Color.Gray)
                                ) {
                                    Text(
                                        text = colorFlavor.quantity_Achete.toString(),
                                        fontSize = 30.sp,
                                        color = Color.Red
                                    )
                                    Text(
                                        text = colorFlavor.imogi,
                                        fontSize = 30.sp,
                                        color = Color.White
                                    )
                                    Text(
                                        text = colorFlavor.nom,
                                        fontSize = 30.sp,
                                        color = Color.White
                                    )

                                }
                            }

                            produit.demmende_Achate_De_Cette_Produit.forEach { achterur ->
                                achterur.colours_Et_Gouts_Acheter_Depuit_Client.forEach { couleur ->
                                    Row {
                                        if (couleur.vidPosition == colorFlavor.position_Du_Couleur_Au_Produit) {
                                            Text(
                                                text = couleur.quantity_Achete.toString(),
                                                fontSize = 30.sp,
                                                color = Color.Red
                                            )
                                            Text(
                                                text = achterur.nom_Acheteur,
                                                fontSize = 24.sp,
                                                color = Color.Black,
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
