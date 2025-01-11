package com.example.Packages.A_GrosssitsCommendHandler.F1_ServeurGrossistCommendFragment

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.Y_AppsFather.Kotlin.ModelAppsFather
import com.example.Y_AppsFather.Kotlin.ModelAppsFather.Companion.updateProduct_produitsAvecBonsGrossist
import com.example.Y_AppsFather.Kotlin.ViewModelProduits

@Composable
fun B_ListMainFragment_1(
    viewModelProduits: ViewModelProduits,
    paddingValues: PaddingValues,
    modifier: Modifier = Modifier,
    visibleProducts: List<ModelAppsFather.ProduitModel>
) {
    val (positionedProducts, unpositionedProducts) = visibleProducts.partition {
        it.bonCommendDeCetteCota
            ?.cPositionCheyCeGrossit == true
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(5),
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xE3C85858).copy(alpha = 0.1f), RoundedCornerShape(8.dp)),
        contentPadding = paddingValues,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Header for positioned products
        if (positionedProducts.isNotEmpty()) {
            item(span = { GridItemSpan(5) }) {
                Text(
                    "Produits avec position (${positionedProducts.size})",
                    modifier = Modifier.padding(8.dp),
                    style = MaterialTheme.typography.titleMedium
                )
            }

            items(
                items = positionedProducts.sortedBy {
                    it.bonCommendDeCetteCota
                        ?.positionProduitDonGrossistChoisiPourAcheterCeProduit
                },
                key = { it.id }
            ) { product ->
                C_ItemMainFragment_1(
                    mainItem = product,
                    onCLickOnMain = {
                        product.bonCommendDeCetteCota
                            ?.cPositionCheyCeGrossit = false

                        updateProduct_produitsAvecBonsGrossist(product,viewModelProduits)
                    },
                    modifier = Modifier.animateItem(fadeInSpec = null, fadeOutSpec = null)
                )
            }
        }

        // Header for unpositioned products
        if (unpositionedProducts.isNotEmpty()) {
            item(span = { GridItemSpan(5) }) {
                Row(
                    modifier = Modifier.padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(onClick = {}) {
                        Icon(
                            Icons.Default.Moving,
                            contentDescription = "Déplacer",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = {}) {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = "Rechercher",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    Text(
                        "Produits sans position (${unpositionedProducts.size})",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }

            items(
                items = unpositionedProducts.sortedBy {
                    it.bonCommendDeCetteCota
                        ?.positionProduitDonGrossistChoisiPourAcheterCeProduit
                },
                key = { it.id }
            ) { product ->
                C_ItemMainFragment_1(
                    mainItem = product,
                    onCLickOnMain = {
                        val newPosition = (positionedProducts.maxOfOrNull {
                            it.bonCommendDeCetteCota
                                ?.positionProduitDonGrossistChoisiPourAcheterCeProduit ?: 0
                        } ?: 0) + 1

                        product.bonCommendDeCetteCota?.apply {
                            cPositionCheyCeGrossit = true
                            positionProduitDonGrossistChoisiPourAcheterCeProduit = newPosition
                        }
                        if (product.itsTempProduit) {
                            product.statuesBase.prePourCameraCapture = true
                        }
                        updateProduct_produitsAvecBonsGrossist(product,viewModelProduits)
                    },
                    modifier = Modifier.animateItem(fadeInSpec = null, fadeOutSpec = null)
                )
            }
        }
    }

    SearchDialog(viewModelProduits)
}
