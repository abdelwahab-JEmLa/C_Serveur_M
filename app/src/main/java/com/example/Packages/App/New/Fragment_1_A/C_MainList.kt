package com.example.Packages.App.New.Fragment_1_A

import Z_MasterOfApps.Kotlin.Model.Extension.grossistsDisponible
import Z_MasterOfApps.Kotlin.Model._ModelAppsFather
import Z_MasterOfApps.Kotlin.Model._ModelAppsFather.Companion.updateProduit
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
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

@Composable
private fun MoveProductsDialog(
    selectedProducts: List<_ModelAppsFather.ProduitModel>,
    currentGrossist: _ModelAppsFather.ProduitModel.GrossistBonCommandes.GrossistInformations?,
    viewModelProduits: ViewModelInitApp,
    onDismiss: () -> Unit,
    onProductsMoved: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
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
                    "Move ${selectedProducts.size} products to another grossist",
                    style = MaterialTheme.typography.titleMedium
                )

                viewModelProduits._modelAppsFather.grossistsDisponible
                    .filter { it.id != currentGrossist?.id }
                    .forEach { grossist ->
                        Button(
                            onClick = {
                                selectedProducts.forEach { product ->
                                    product.bonCommendDeCetteCota?.let { bonCommande ->
                                        bonCommande.grossistInformations = grossist
                                        updateProduit(product, viewModelProduits)
                                    }
                                }
                                onDismiss()
                                onProductsMoved()
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

@Composable
private fun GrossistHeader(
    grossist: _ModelAppsFather.ProduitModel.GrossistBonCommandes.GrossistInformations?,
    selectedProductsCount: Int,
    onMoveClick: () -> Unit
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

        IconButton(onClick = onMoveClick) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    Icons.Default.DragIndicator,
                    contentDescription = "Move products",
                    tint = Color.White
                )
                if (selectedProductsCount > 0) {
                    Text(
                        "$selectedProductsCount",
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun MainList_F4(
    visibleProducts: List<_ModelAppsFather.ProduitModel>,
    viewModelProduits: ViewModelInitApp,
    paddingValues: PaddingValues,
    modifier: Modifier = Modifier
) {
    var selectedProducts by remember { mutableStateOf<List<_ModelAppsFather.ProduitModel>>(emptyList()) }
    var currentGrossist by remember { mutableStateOf<_ModelAppsFather.ProduitModel.GrossistBonCommandes.GrossistInformations?>(null) }
    var showMoveDialog by remember { mutableStateOf(false) }

    val groupedProducts = visibleProducts
        .groupBy { it.bonCommendDeCetteCota?.grossistInformations }
        .filterKeys { it != null }
        .toSortedMap(compareBy { it?.positionInGrossistsList })

    if (showMoveDialog && currentGrossist != null) {
        MoveProductsDialog(
            selectedProducts = selectedProducts,
            currentGrossist = currentGrossist,
            viewModelProduits = viewModelProduits,
            onDismiss = { showMoveDialog = false },
            onProductsMoved = { selectedProducts = emptyList() }
        )
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = modifier.background(
            Color(0xE3C85858).copy(alpha = 0.1f),
            RoundedCornerShape(8.dp)
        ),
        contentPadding = paddingValues,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        groupedProducts.forEach { (grossist, products) ->
            item(span = { GridItemSpan(3) }) {
                GrossistHeader(
                    grossist = grossist,
                    selectedProductsCount = selectedProducts.size,
                    onMoveClick = {
                        currentGrossist = grossist
                        showMoveDialog = true
                    }
                )
            }

            items(
                items = products.sortedBy {
                    it.bonCommendDeCetteCota?.mutableBasesStates
                        ?.positionProduitDonGrossistChoisiPourAcheterCeProduit
                        ?: Int.MAX_VALUE
                }
            ) { product ->
                Box(
                    modifier = Modifier
                        .animateItem()
                        .padding(4.dp)
                ) {
                    MainItem_F4(
                        mainItem = product,
                        onCLickOnMain = {
                            selectedProducts = if (selectedProducts.contains(product)) {
                                selectedProducts - product
                            } else {
                                selectedProducts + product
                            }
                        },
                        position = selectedProducts.indexOf(product).let {
                            if (it >= 0) it + 1 else null
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = if (selectedProducts.contains(product))
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
