package com.example.Packages.Z_F4._DeplaceProduitsVerGrossist

import Z_MasterOfApps.Kotlin.Model.Extension.grossistsDisponible
import Z_MasterOfApps.Kotlin.Model._ModelAppsFather
import Z_MasterOfApps.Kotlin.Model._ModelAppsFather.Companion.updateProduit
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DragIndicator
import androidx.compose.material3.Button
import androidx.compose.material3.Card
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainList_F4(
    visibleProducts: List<_ModelAppsFather.ProduitModel>,
    viewModelProduits: ViewModelInitApp,
    paddingValues: PaddingValues,
    modifier: Modifier = Modifier
) {
    var produitPourDeplace by remember { mutableStateOf<List<_ModelAppsFather.ProduitModel>>(emptyList()) }
    var showGrossistDialog by remember { mutableStateOf(false) }
    var selectedGrossist by remember { mutableStateOf<_ModelAppsFather.ProduitModel.GrossistBonCommandes.GrossistInformations?>(null) }

    val groupedProducts = visibleProducts
        .groupBy { product ->
            product.bonCommendDeCetteCota?.grossistInformations
        }
        .filterKeys { it != null }
        .toSortedMap(compareBy { it?.positionInGrossistsList })

    if (showGrossistDialog && selectedGrossist != null) {
        Dialog(onDismissRequest = { showGrossistDialog = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        "Move ${produitPourDeplace.size} products to another grossist",
                        style = MaterialTheme.typography.titleMedium
                    )

                    viewModelProduits._modelAppsFather.grossistsDisponible.forEach { grossist ->
                        if (grossist.id != selectedGrossist?.id) {
                            Button(
                                onClick = {
                                    produitPourDeplace.forEach { product ->
                                        product.bonCommendDeCetteCota?.let { bonCommande ->
                                            bonCommande.grossistInformations = grossist
                                            updateProduit(
                                                product = product,
                                                viewModelProduits = viewModelProduits
                                            )
                                        }
                                    }
                                    showGrossistDialog = false
                                    produitPourDeplace = emptyList()
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(grossist.nom)
                            }
                        }
                    }
                }
            }
        }
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = modifier
            .background(Color(0xE3C85858).copy(alpha = 0.1f), RoundedCornerShape(8.dp)),
        contentPadding = paddingValues,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        groupedProducts.forEach { (grossist, products) ->
            item(
                span = { GridItemSpan(3) }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF2C2C2C))
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = grossist?.nom ?: "",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    IconButton(
                        onClick = {
                            selectedGrossist = grossist
                            showGrossistDialog = true
                        }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                Icons.Default.DragIndicator,
                                contentDescription = "Move products",
                                tint = Color.White
                            )
                            if (produitPourDeplace.isNotEmpty()) {
                                Text(
                                    "${produitPourDeplace.size}",
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }

            items(
                items = products.sortedBy { product ->
                    product.bonCommendDeCetteCota
                        ?.mutableBasesStates
                        ?.positionProduitDonGrossistChoisiPourAcheterCeProduit
                        ?: Int.MAX_VALUE
                },
            ) { product ->
                Box(
                    modifier = Modifier
                        .animateItemPlacement()
                        .padding(4.dp)
                ) {
                    MainItem_F4(
                        mainItem = product,
                        onCLickOnMain = {
                            if (produitPourDeplace.contains(product)) {
                                produitPourDeplace = produitPourDeplace - product
                            } else {
                                produitPourDeplace = produitPourDeplace + product
                            }
                        },
                        position = if (produitPourDeplace.contains(product))
                            produitPourDeplace.indexOf(product) + 1
                        else null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = if (produitPourDeplace.contains(product))
                                    Color.Yellow.copy(alpha = 0.3f)
                                else Color.Transparent,
                                shape = RoundedCornerShape(4.dp)
                            )
                            .animateItem()
                    )
                }
            }
        }
    }
}
