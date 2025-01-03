package com.example.Packages._2.Fragment.UI

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.Apps_Head._1.Model.AppsHeadModel

@Composable
fun ListMain(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues,
    visibleClientEtCesProduit: Map<AppsHeadModel.ProduitModel.ClientBonVentModel.ClientInformations, List<AppsHeadModel.ProduitModel>>
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
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
        if (visibleClientEtCesProduit.isEmpty()) {
            item(span = { GridItemSpan(2) }) {
                EmptyListMessage()
            }
            return@LazyVerticalGrid
        }

        visibleClientEtCesProduit.forEach { (client, products) ->
            val visibleProducts = products.filter { it.isVisible }
            if (visibleProducts.isNotEmpty()) {
                item(span = { GridItemSpan(2) }) {
                    ClientHeader(
                        client = client,
                        productCount = visibleProducts.size,
                        modifier = Modifier.animateItem(fadeInSpec = null, fadeOutSpec = null)
                    )
                }

                items(
                    items = visibleProducts,
                ) { produit ->
                    ItemMain(
                        itemMain = produit,
                        onCLickOnMain = { /* Handle click event */ }
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyListMessage() {
    Text(
        text = "Aucun produit disponible",
        modifier = Modifier.padding(32.dp),
        style = MaterialTheme.typography.bodyLarge
    )
}

@Composable
private fun ClientHeader(
    client: AppsHeadModel.ProduitModel.ClientBonVentModel.ClientInformations,
    productCount: Int,
    modifier: Modifier = Modifier
) {
    Text(
        text = "${client.nom} ($productCount)",
        style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.Bold
        ),
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    )
}
