package com.example.Packages.F2_AfficheurOrdreDeProduitsACherche

import Z_MasterOfApps.Kotlin.Model._ModelAppsFather
import Z_MasterOfApps.Kotlin.Model._ModelAppsFather.Companion.updateProduit
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun MainList_F2(
    visibleProducts: List<_ModelAppsFather.ProduitModel>,
    viewModelProduits: ViewModelInitApp,
    paddingValues: PaddingValues,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xE3C85858).copy(alpha = 0.1f), RoundedCornerShape(8.dp)),
        contentPadding = paddingValues,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            items = visibleProducts.sortedBy { product ->
                // Handle null cases by providing a default max value
                product.bonCommendDeCetteCota
                    ?.mutableBasesStates
                    ?.positionProduitDonGrossistChoisiPourAcheterCeProduit
                    ?: Int.MAX_VALUE
            },
            // Ensure unique key by combining id with position if available
            key = { product ->
                "${product.id}_${product.bonCommendDeCetteCota
                    ?.mutableBasesStates
                    ?.positionProduitDonGrossistChoisiPourAcheterCeProduit}"
            }
        ) { product ->
            MainItem_F2(
                mainItem = product,
                onCLickOnMain = {
                    product.bonCommendDeCetteCota
                        ?.mutableBasesStates
                        ?.cPositionCheyCeGrossit = false
                    updateProduit(product, viewModelProduits)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .animateItem(fadeInSpec = null, fadeOutSpec = null)
            )
        }
    }
}
