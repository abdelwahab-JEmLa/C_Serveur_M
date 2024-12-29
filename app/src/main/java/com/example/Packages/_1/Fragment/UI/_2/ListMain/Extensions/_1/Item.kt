package com.example.Packages._1.Fragment.UI._2.ListMain.Extensions._1

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
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
import com.example.Apps_Head._1.Model.AppInitializeModel
import com.example.Apps_Head._3.Modules.Images_Handler.Glide_Display_Image_By_Id
import com.example.Packages._1.Fragment.ViewModel.Models.UiState

@Composable
internal fun Item(
    uiState: UiState,
    produit: AppInitializeModel.ProduitModel,
) {
    var isExpanded by remember { mutableStateOf(false) }

    // Calculate height based on mode
    val heightCard = when {
        uiState.currentMode == UiState.Affichage_Et_Click_Modes.MODE_Affiche_Produits -> if (isExpanded) 300.dp else 100.dp
        else -> 100.dp
    }

    // Calculate total quantity
    val totalQuantity = produit.historiqueBonsCommend
        .find { it.vid == 1L }
        ?.coloursEtGoutsCommendee
        ?.sumOf { it.quantityAchete } ?: 0

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (uiState.currentMode == UiState.Affichage_Et_Click_Modes.MODE_Affiche_Achteurs)
                    Modifier.wrapContentHeight()
                else
                    Modifier.height(heightCard)
            )
    ) {
        // Dans le composant ItemDisplayListMode, modifier l'appel de Glide_Display_Image_By_Id :
        Glide_Display_Image_By_Id(
            produit_Id = produit.id,
            produit_Image_Need_Update = produit.it_Image_besoin_To_Be_Updated,
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            reloadKey = 0
        )
        // Overlay
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(color = Color.Black.copy(alpha = 0.4f))
        )

        // Non trouvé highlight
        if (produit.non_Trouve) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(color = Color(0xFFFFD700).copy(alpha = 0.7f))
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
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

                // Position display and buttons
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Total: $totalQuantity",
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            // Contenu spécifique au mode
            if (uiState.currentMode == UiState.Affichage_Et_Click_Modes.MODE_Affiche_Achteurs) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(top = 8.dp)
                ) {
                    produit.bonsVentDeCetteCota
                        .sortedBy { it.nom_Acheteur }
                        .forEach { acheteur ->
                            acheteur.colours_Achete
                                .sortedBy { it.quantity_Achete }
                                .forEach { couleur ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = acheteur.nom_Acheteur,
                                            fontSize = 14.sp,
                                            color = Color.Red,
                                            modifier = Modifier
                                                .weight(1f)
                                                .background(Color.Gray)
                                        )
                                        Text(
                                            text = "${couleur.quantity_Achete}=${couleur.imogi}${couleur.nom}",
                                            fontSize = 14.sp,
                                            color = Color.White,
                                            modifier = Modifier.padding(start = 8.dp)
                                        )
                                    }
                                }
                        }
                }
            } else {
                // Mode Produits - Grille de couleurs
                Box(
                    modifier = Modifier
                        .width(200.dp)
                        .height(if (isExpanded) 280.dp else 80.dp)
                        .clickable { isExpanded = !isExpanded }
                ) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        val colorsList = produit.bonCommendDeCetteCota
                            ?.coloursEtGoutsCommendee
                            ?.sortedBy { it.quantityAchete }
                            ?.filter { it.quantityAchete > 0 }
                            ?: emptyList()

                        items(colorsList.size) { index ->
                            val colorFlavor = colorsList[index]
                            val displayText = when {
                                colorFlavor.statues?.imogi?.isNotEmpty() == true ->
                                    colorFlavor.statues?.imogi
                                else -> colorFlavor.statues?.nom?.take(3) ?: ""
                            }

                            Text(
                                text = "(${colorFlavor.quantityAchete})$displayText",
                                fontSize = 24.sp,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}
