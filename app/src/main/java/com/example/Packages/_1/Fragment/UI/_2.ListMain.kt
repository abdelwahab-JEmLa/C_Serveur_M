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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun ListMain(
    visibleItems: SnapshotStateList<AppsHeadModel.ProduitModel>,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues,
    viewModelScope: CoroutineScope
) {
    // Transformer la fonction en valeur lambda
    val updateProduct: (AppsHeadModel.ProduitModel, Int, SnapshotStateList<AppsHeadModel.ProduitModel>, CoroutineScope) -> Unit = { product, newPosition, items, scope ->
        if (product.bonCommendDeCetteCota == null) {
            product.bonCommendDeCetteCota = AppsHeadModel.ProduitModel.GrossistBonCommandes()
        }

        product.bonCommendDeCetteCota?.position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit = newPosition
        product.besoin_To_Be_Updated = true

        scope.launch {
            items.updateProduitsFireBase()
        }
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(5),
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = Color(0xE3C85858).copy(alpha = 0.1f),
                shape = RoundedCornerShape(8.dp)
            ),
        contentPadding = contentPadding,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Show message if no items
        if (visibleItems.isEmpty()) {
            item(span = { GridItemSpan(5) }) {
                Text(
                    text = "Aucun produit disponible",
                    modifier = Modifier.padding(32.dp),
                    style = MaterialTheme.typography.bodyLarge
                )
                return@item
            }
        }

        // Split items into positioned and unpositioned
        val (positioned, unpositioned) = visibleItems.partition { product ->
            product.bonCommendDeCetteCota?.position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit?.let { it > 0 } ?: false
        }

        // Display positioned items
        if (positioned.isNotEmpty()) {
            item(span = { GridItemSpan(5) }) {
                Text(
                    text = "Produits avec position (${positioned.size})",
                    modifier = Modifier.padding(8.dp),
                    style = MaterialTheme.typography.titleMedium
                )
            }

            items(items = positioned.sortedBy {
                it.bonCommendDeCetteCota?.position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit
            }, key = { it.id }) { product ->
                ItemMain(
                    itemMain = product,
                    onCLickOnMain = {
                        val maxPosition = positioned.maxOfOrNull {
                            it.bonCommendDeCetteCota?.position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit ?: 0
                        } ?: 0
                        updateProduct(product, maxPosition + 1, visibleItems, viewModelScope)
                    },
                    onClickDelete = {
                        updateProduct(product, 0, visibleItems, viewModelScope)
                    }
                )
            }
        }

        // Display unpositioned items
        if (unpositioned.isNotEmpty()) {
            item(span = { GridItemSpan(5) }) {
                Text(
                    text = "Produits sans position (${unpositioned.size})",
                    modifier = Modifier.padding(8.dp),
                    style = MaterialTheme.typography.titleMedium
                )
            }

            items(items = unpositioned.sortedBy { it.nom }, key = { it.id }) { product ->
                ItemMain(
                    itemMain = product,
                    onCLickOnMain = {
                        val maxPosition = positioned.maxOfOrNull {
                            it.bonCommendDeCetteCota?.position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit ?: 0
                        } ?: 0
                        updateProduct(product, maxPosition + 1, visibleItems, viewModelScope)
                    },
                    onClickDelete = {
                        updateProduct(product, 0, visibleItems, viewModelScope)
                    }
                )
            }
        }
    }
}
