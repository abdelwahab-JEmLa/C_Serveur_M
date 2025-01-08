package com.example.Packages.A_GrosssitsCommendHandler.A1_Fragment

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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.Apps_Head._1.Model.AppsHeadModel
import com.example.Packages.A_GrosssitsCommendHandler.A1_Fragment.A_Head.ViewModel_Head

@Composable
fun B_ListMainFragment_1(
    viewModel_Head: ViewModel_Head,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    var showSearchDialog by remember { mutableStateOf(false) }
    val mapGroToMapPositionToProduits = viewModel_Head._mapsSansModels.mapGroToMapPositionToProduits
    val currentGrossist = mapGroToMapPositionToProduits.entries.firstOrNull()

    val handleProductMove = { produitModel: AppsHeadModel.ProduitModel, toPositioned: Boolean ->
        currentGrossist?.let { grossistEntry ->
            grossistEntry.value[true]?.let { positionedList ->
                grossistEntry.value[false]?.let { unpositionedList ->
                    if (toPositioned) {
                        // Move to positioned list
                        if (unpositionedList.remove(produitModel)) {
                            positionedList.add(produitModel)
                        }
                    } else {
                        // Move to unpositioned list
                        if (positionedList.remove(produitModel)) {
                            unpositionedList.add(produitModel)
                        }
                    }
                }
            }
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
            viewModel_Head = viewModel_Head,
            products = currentGrossist?.value?.get(true) ?: mutableStateListOf(),
            onClickOnMAin = { handleProductMove(it, false) }
        )

        UnPositionedProduits(
            viewModel_Head = viewModel_Head,
            products = currentGrossist?.value?.get(false) ?: mutableStateListOf(),
            onShowSearchDialog = { showSearchDialog = true },
            onClickOnMAin = { handleProductMove(it, true) }
        )
    }
}

private fun LazyGridScope.PositionedProduits(
    viewModel_Head: ViewModel_Head,
    products: SnapshotStateList<AppsHeadModel.ProduitModel>,
    onClickOnMAin: (AppsHeadModel.ProduitModel) -> Unit,
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
        ) { produitModel ->
            val index = products.indexOf(produitModel)
            C_ItemMainFragment_1(
                viewModel_Head =viewModel_Head,
                itemMainId = produitModel,
                onCLickOnMain = { onClickOnMAin(produitModel) },
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
    viewModel_Head: ViewModel_Head,
    products: SnapshotStateList<AppsHeadModel.ProduitModel>,
    onShowSearchDialog: () -> Unit,
    onClickOnMAin: (AppsHeadModel.ProduitModel) -> Unit
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
        ) { produitModel ->
            C_ItemMainFragment_1(
                viewModel_Head = viewModel_Head,
                itemMainId = produitModel,
                modifier = Modifier.animateItem(
                    fadeInSpec = null,
                    fadeOutSpec = null
                ),
                onCLickOnMain = { onClickOnMAin(produitModel) }  // Add animation for item placement
            )
        }
    }
}

