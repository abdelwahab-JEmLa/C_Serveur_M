package com.example.Packages.A1_Fragment

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.Moving
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.Apps_Head._1.Model.AppsHeadModel
import com.example.Apps_Head._1.Model.AppsHeadModel.Companion.update_produitsViewModelEtFireBases
import com.example.Apps_Head._1.Model.AppsHeadModel.ProduitModel.GrossistBonCommandes.GrossistInformations.Companion.produitGroupeurParGrossistInfos
import com.example.Apps_Head._2.ViewModel.InitViewModel

@Composable
fun B_ListMainFragment_1(
    visibleItems: SnapshotStateList<AppsHeadModel.ProduitModel>,
    initViewModel: InitViewModel,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    var showSearchDialog by remember { mutableStateOf(false) }

    val (positioned, unpositioned) =
        visibleItems
            .partition {
                it.bonCommendDeCetteCota
                    ?.cPositionCheyCeGrossit == true
            }

    fun updatePosition(
        positioned: List<AppsHeadModel.ProduitModel>,
        selectedProduct: AppsHeadModel.ProduitModel,
    ) {
        val newPositione =
            (positioned.maxOfOrNull {
                it.bonCommendDeCetteCota?.positionProduitDonGrossistChoisiPourAcheterCeProduit
                    ?: 0
            } ?: 0) + 1

        visibleItems[visibleItems.indexOfFirst { it.id == selectedProduct.id }] =
            selectedProduct.apply {
                if (selectedProduct.itsTempProduit) {
                    statuesBase.prePourCameraCapture = true
                }
                bonCommendDeCetteCota?.positionProduitDonGrossistChoisiPourAcheterCeProduit =
                    newPositione
                bonCommendDeCetteCota?.cPositionCheyCeGrossit = true
            }

        visibleItems.toMutableStateList()
            .update_produitsViewModelEtFireBases(initViewModel)
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(5),
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xE3C85858).copy(alpha = 0.1f), RoundedCornerShape(8.dp)),
        contentPadding = contentPadding,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        if (positioned.isNotEmpty()) {
            item(span = { GridItemSpan(5) }) {
                Text(
                    "Produits avec position (${positioned.size})",
                    modifier = Modifier.padding(8.dp),
                    style = MaterialTheme.typography.titleMedium
                )
            }

            items(
                items = positioned.sortedBy { it.bonCommendDeCetteCota?.positionProduitDonGrossistChoisiPourAcheterCeProduit },
                key = { it.id }
            ) { product ->
                C_ItemMainFragment_1(
                    initViewModel = initViewModel,
                    itemMain = product,
                    onCLickOnMain = {
                        visibleItems[visibleItems.indexOfFirst { it.id == product.id }] =
                            product.apply {
                                bonCommendDeCetteCota?.cPositionCheyCeGrossit = false
                            }

                        visibleItems.toMutableStateList()
                            .update_produitsViewModelEtFireBases(initViewModel)
                    }
                )
            }
        }

        if (unpositioned.isNotEmpty()) {
            item(span = { GridItemSpan(5) }) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.align(Alignment.CenterStart)
                    ) {
                        IconButton(
                            onClick = {
                                val groupedProducts = produitGroupeurParGrossistInfos(initViewModel.appsHeadModel.produitsMainDataBase)
                                unpositioned.forEach { product ->
                                    val currentPosition = product.bonCommendDeCetteCota
                                        ?.grossistInformations?.positionInGrossistsList ?: return@forEach

                                    // Find the matching GrossistInformations for the current position
                                    groupedProducts.entries
                                        .find { (key, _) ->
                                            key.positionInGrossistsList == currentPosition + 1 }
                                        ?.key?.let { matchingGrossist ->
                                            // Update the product's GrossistInformations
                                            visibleItems[visibleItems.indexOfFirst { it.id == product.id }] =
                                                product.apply {
                                                    bonCommendDeCetteCota?.grossistInformations = matchingGrossist
                                                    isVisible=false
                                                }
                                        }
                                }

                                // Update Firebase after processing all items
                                visibleItems.toMutableStateList()
                                    .update_produitsViewModelEtFireBases(initViewModel)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Moving,
                                contentDescription = "Moving",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        IconButton(onClick = { showSearchDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        Text(
                                "Produits sans position (${unpositioned.size})",
                        style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }

            items(
                items = unpositioned.sortedBy { it.bonCommendDeCetteCota?.positionProduitDonGrossistChoisiPourAcheterCeProduit },
                key = { it.id }
            ) { product ->
                C_ItemMainFragment_1(
                    initViewModel = initViewModel,
                    itemMain = product,
                    onCLickOnMain = {
                        updatePosition(positioned, product)
                    }
                )
            }
        }
    }
    if (showSearchDialog) {
        SearchDialog(
            unpositionedItems = unpositioned,
            onDismiss = {
                showSearchDialog = false
            },
            onItemSelected = { selectedProduct ->
                updatePosition(positioned, selectedProduct)
                showSearchDialog = false
            },
            initViewModel = initViewModel
        )
    }
}
