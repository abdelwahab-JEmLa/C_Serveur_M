package com.example.Packages.A1_Fragment

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.Apps_Head._1.Model.AppsHeadModel
import com.example.Apps_Head._1.Model.AppsHeadModel.Companion.update_produitsViewModelEtFireBases
import com.example.Apps_Head._2.ViewModel.InitViewModel

@Composable
fun B_ListMainFragment_1(
    visibleItems: SnapshotStateList<AppsHeadModel.ProduitModel>,
    initViewModel: InitViewModel,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    val (positioned, unpositioned) =
        visibleItems
            .partition {
                (it.bonCommendDeCetteCota
                    ?.positionProduitDonGrossistChoisiPourAcheterCeProduit ?: 0) > 0
            }

    LazyVerticalGrid(
        columns = GridCells.Fixed(5),
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xE3C85858).copy(alpha = 0.1f), RoundedCornerShape(8.dp)),
        contentPadding = contentPadding,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
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
                                statuesBase.prePourCameraCapture = true
                                bonCommendDeCetteCota
                                    ?.positionProduitDonGrossistChoisiPourAcheterCeProduit =
                                    0
                            }

                        visibleItems.toMutableStateList()
                            .update_produitsViewModelEtFireBases(initViewModel)
                    }
                )
            }
        }

        if (unpositioned.isNotEmpty()) {
            item(span = { GridItemSpan(5) }) {
                Text(
                    "Produits sans position (${unpositioned.size})",
                    modifier = Modifier.padding(8.dp),
                    style = MaterialTheme.typography.titleMedium
                )
            }

            items(
                items = unpositioned.sortedBy { it.nom },
                key = { it.id }
            ) { product ->
                C_ItemMainFragment_1(
                    initViewModel = initViewModel,
                    itemMain = product,
                    onCLickOnMain = {
                        val maxPosition = positioned.maxOfOrNull {
                            it.bonCommendDeCetteCota?.positionProduitDonGrossistChoisiPourAcheterCeProduit
                                ?: 0
                        } ?: 0

                        visibleItems[visibleItems.indexOfFirst { it.id == product.id }] =
                            product.apply {
                                statuesBase.prePourCameraCapture = true
                                bonCommendDeCetteCota?.positionProduitDonGrossistChoisiPourAcheterCeProduit =
                                    maxPosition + 1
                            }

                        visibleItems.toMutableStateList()
                            .update_produitsViewModelEtFireBases(initViewModel)
                    }
                )
            }
        }
    }
}
