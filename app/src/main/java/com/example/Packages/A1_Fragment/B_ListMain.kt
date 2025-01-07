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
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.Apps_Head._1.Model.AppsHeadModel.Companion.produitsFireBaseRef
import com.example.Apps_Head._1.Model.AppsHeadModel.ProduitModel

@Composable
fun B_ListMainFragment_1(
    visibleGrossistAssociatedProduits: List<ProduitModel>,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    var positionedProduits by remember(visibleGrossistAssociatedProduits) { mutableStateOf<List<ProduitModel>>(emptyList()) }
    var unPositionedProduits by remember(visibleGrossistAssociatedProduits) {
        mutableStateOf(visibleGrossistAssociatedProduits)
    }
    var showSearchDialog by remember { mutableStateOf(false) }

    val handleProductMove = { item: ProduitModel, toPositioned: Boolean ->
        if (toPositioned) {
            positionedProduits += item
            unPositionedProduits -= item
        } else {
            positionedProduits -= item
            unPositionedProduits += item
        }
        produitsFireBaseRef.apply {
            setValue(positionedProduits)
            setValue(unPositionedProduits)
        }
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(5),
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xE3C85858).copy(alpha = 0.1f), RoundedCornerShape(8.dp)),
        contentPadding = contentPadding,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        PositionedProduits(
            products = positionedProduits,
            onClickOnMAin = { handleProductMove(it, false) }
        )

        UnPositionedProduits(
            products = unPositionedProduits,
            onShowSearchDialog = { showSearchDialog = true },
            onClickOnMAin = { handleProductMove(it, true) }
        )
    }

    if (showSearchDialog) {
        SearchDialog(
            unpositionedItems = unPositionedProduits,
            onDismiss = { showSearchDialog = false },
            onItemSelected = {
                handleProductMove(it, true)
                showSearchDialog = false
            }
        )
    }
}

private fun LazyGridScope.PositionedProduits(
    products: List<ProduitModel>,
    onClickOnMAin: (ProduitModel) -> Unit,
) {
    if (products.isNotEmpty()) {
        item(span = { GridItemSpan(5) }) {
            Text(
                "Produits avec position (${products.size})",
                modifier = Modifier.padding(8.dp),
                style = MaterialTheme.typography.titleMedium
            )
        }

        items(
            items = products,
            key = { it.id }
        ) { product ->
            val index = products.indexOf(product)
            C_ItemMainFragment_1(
                itemMain = product,
                onCLickOnMain = { onClickOnMAin(product) },
                position = index + 1,
                modifier = Modifier.animateItem(
                    fadeInSpec = null,
                    fadeOutSpec = null
                )  // Add animation for item placement
            )
        }
    }
}

private fun LazyGridScope.UnPositionedProduits(
    products: List<ProduitModel>,
    onShowSearchDialog: () -> Unit,
    onClickOnMAin: (ProduitModel) -> Unit
) {
    if (products.isNotEmpty()) {
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
                    IconButton(onClick = onShowSearchDialog) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    Text(
                        "Produits sans position (${products.size})",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }

        items(
            items = products,
            key = { it.id }  // Add key for stable animations
        ) { product ->
            C_ItemMainFragment_1(
                itemMain = product,
                onCLickOnMain = { onClickOnMAin(product) },
                modifier = Modifier.animateItem(
                    fadeInSpec = null,
                    fadeOutSpec = null
                )  // Add animation for item placement
            )
        }
    }
}

