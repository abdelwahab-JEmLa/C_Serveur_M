package com.example.Packages.A_GrosssitsCommendHandler.Z_ActiveFragment

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.Y_AppsFather.Kotlin.ModelAppsFather.Companion.updateProduct
import com.example.Y_AppsFather.Kotlin.ViewModelProduits

@Composable
fun B_ListMainFragment_1(
    viewModelProduits: ViewModelProduits,
    paddingValues: PaddingValues,
    modifier: Modifier = Modifier
) {
    // Constants
    val GRID_COLUMNS = 5
    val GRID_SPACING = 8.dp

    var showSearchDialog by remember { mutableStateOf(false) }

    // Memoized product filtering
    val products by remember(viewModelProduits.produitsMainDataBase) {
        mutableStateOf(viewModelProduits.produitsMainDataBase.filter { it.isVisible })
    }

    val (positionedProducts, unpositionedProducts) = remember(products) {
        products.partition { it.bonCommendDeCetteCota?.cPositionCheyCeGrossit == true }
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(GRID_COLUMNS),
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.1f),
                shape = RoundedCornerShape(8.dp)
            ),
        contentPadding = paddingValues,
        horizontalArrangement = Arrangement.spacedBy(GRID_SPACING)
    ) {
        // Positioned Products Section
        if (positionedProducts.isNotEmpty()) {
            ProductsSection(
                title = "Produits avec position (${positionedProducts.size})",
                products = positionedProducts.sortedBy {
                    it.bonCommendDeCetteCota?.positionProduitDonGrossistChoisiPourAcheterCeProduit
                },
                onProductClick = { product ->
                    product.bonCommendDeCetteCota?.cPositionCheyCeGrossit = false
                    viewModelProduits.updateProduct(product)
                }
            )
        }

        // Unpositioned Products Section
        if (unpositionedProducts.isNotEmpty()) {
            UnpositionedProductsSection(
                products = unpositionedProducts,
                positionedProducts = positionedProducts,
                viewModelProduits = viewModelProduits,
                onSearchClick = { showSearchDialog = true }
            )
        }
    }

    // Search Dialog
    if (showSearchDialog) {
        SearchDialog(
            viewModel_Head = viewModelProduits.viewModel_Head,
            onDismiss = { showSearchDialog = false },
            onItemSelected = { product ->
                val newPosition = (positionedProducts.maxOfOrNull {
                    it.bonCommendDeCetteCota?.positionProduitDonGrossistChoisiPourAcheterCeProduit ?: 0
                } ?: 0) + 1

                product.bonCommendDeCetteCota?.apply {
                    cPositionCheyCeGrossit = true
                    positionProduitDonGrossistChoisiPourAcheterCeProduit = newPosition
                }

                if (product.itsTempProduit) {
                    product.statuesBase.prePourCameraCapture = true
                }

                viewModelProduits.updateProduct(product)
            }
        )
    }
}
