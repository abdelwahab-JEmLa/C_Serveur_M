package com.example.Packages._1.Fragment.UI

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.Apps_Head._1.Model.AppsHeadModel
import com.example.Apps_Head._1.Model.AppsHeadModel.Companion.updateProduitsFireBase

@Composable
fun ListMainFragment1(
    visibleItems: SnapshotStateList<AppsHeadModel.ProduitModel>,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues,
) {
    fun updateProductPosition(product: AppsHeadModel.ProduitModel, newPosition: Int) {
        product.apply {
            bonCommendDeCetteCota = bonCommendDeCetteCota ?: AppsHeadModel.ProduitModel.GrossistBonCommandes()
            bonCommendDeCetteCota?.position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit = newPosition
            besoin_To_Be_Updated = true
        }

        // Normalize positions
        visibleItems
            .filter { (it.bonCommendDeCetteCota?.position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit ?: 0) > 0 }
            .sortedBy { it.bonCommendDeCetteCota?.position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit }
            .forEachIndexed { index, item ->
                item.apply {
                    bonCommendDeCetteCota?.position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit = index + 1
                    besoin_To_Be_Updated = true
                }
            }

        visibleItems.updateProduitsFireBase()
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
        val (positioned, unpositioned) = visibleItems.partition {
            (it.bonCommendDeCetteCota?.position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit ?: 0) > 0
        }

        if (positioned.isNotEmpty()) {
            item(span = { GridItemSpan(5) }) {
                Text(
                    "Produits avec position (${positioned.size})",
                    modifier = Modifier.padding(8.dp),
                    style = MaterialTheme.typography.titleMedium
                )
            }

            items(positioned.sortedBy { it.bonCommendDeCetteCota?.position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit }, key = { it.id }) { product ->
                ItemMain(
                    itemMain = product,
                    onCLickOnMain = {
                        val maxPosition = positioned.maxOfOrNull {
                            it.bonCommendDeCetteCota?.position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit ?: 0
                        } ?: 0
                        updateProductPosition(product, maxPosition + 1)
                    },
                    onClickDelete = { updateProductPosition(product, 0) }
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

            items(unpositioned.sortedBy { it.nom }, key = { it.id }) { product ->
                ItemMain(
                    itemMain = product,
                    onCLickOnMain = {
                        val maxPosition = positioned.maxOfOrNull {
                            it.bonCommendDeCetteCota?.position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit ?: 0
                        } ?: 0
                        updateProductPosition(product, maxPosition + 1)
                    },
                    onClickDelete = { updateProductPosition(product, 0) }
                )
            }
        }
    }
}
