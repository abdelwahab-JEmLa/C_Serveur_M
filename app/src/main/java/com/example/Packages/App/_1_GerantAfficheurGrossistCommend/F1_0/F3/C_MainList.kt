package com.example.Packages.App._1_GerantAfficheurGrossistCommend.F1_0.F3

import Z_MasterOfApps.Kotlin.Model._ModelAppsFather
import Z_MasterOfApps.Kotlin.Model._ModelAppsFather.Companion.updateProduit
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainList_F3(
    visibleProducts: List<_ModelAppsFather.ProduitModel>,
    viewModelProduits: ViewModelInitApp,
    paddingValues: PaddingValues,
    modifier: Modifier = Modifier
) {
    val groupedProducts = visibleProducts
        .filter { product ->
            product.bonCommendDeCetteCota
                ?.mutableBasesStates
                ?.cPositionCheyCeGrossit == true
        }
        .groupBy { product ->
            product.bonCommendDeCetteCota
                ?.grossistInformations
        }
        .filterKeys { it != null }
        .toSortedMap(compareBy { it?.positionInGrossistsList })

    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xE3C85858).copy(alpha = 0.1f), RoundedCornerShape(8.dp)),
        contentPadding = paddingValues,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        groupedProducts.forEach { (grossist, products) ->
            stickyHeader {
                val backgroundColor = Color(android.graphics.Color.parseColor(grossist?.couleur ?: "#FFFFFF"))
                val textColor = if (grossist?.couleur?.equals("#FFFFFF", ignoreCase = true) == true) {
                    Color.Black
                } else {
                    Color.White
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(backgroundColor)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = grossist?.nom ?: "",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                }
            }

            items(
                items = products.sortedBy { product ->
                    product.bonCommendDeCetteCota
                        ?.mutableBasesStates
                        ?.positionProduitDonGrossistChoisiPourAcheterCeProduit
                        ?: Int.MAX_VALUE
                },
            ) { product ->
                MainItem_F3(
                    mainItem = product,
                    viewModelProduits=viewModelProduits,
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
}
