package com.example.Packages.App._1_GerantAfficheurGrossistCommend.F1_0.Fragment_2

import Z_MasterOfApps.Kotlin.Model._ModelAppsFather
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import Z_MasterOfApps.Z_AppsFather.Kotlin._4.Modules.GlideDisplayImageById2
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.sp

@Composable
fun MainList_F2(
    visibleProducts: List<_ModelAppsFather.ProduitModel>,
    viewModelInitApp: ViewModelInitApp,
    paddingValues: PaddingValues,
    modifier: Modifier = Modifier
) {
    var expandedItemId by remember { mutableStateOf<Long?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xE3C85858).copy(alpha = 0.1f), RoundedCornerShape(8.dp)),
        contentPadding = paddingValues,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            items = visibleProducts.sortedBy { product ->
                product.bonCommendDeCetteCota
                    ?.mutableBasesStates
                    ?.positionProduitDonGrossistChoisiPourAcheterCeProduit
                    ?: Int.MAX_VALUE
            },
            key = { product ->
                "${product.id}_${product.bonCommendDeCetteCota
                    ?.mutableBasesStates
                    ?.positionProduitDonGrossistChoisiPourAcheterCeProduit}"
            }
        ) { product ->
            if (expandedItemId == product.id) {
                ExpandedMainItem_F4(
                    mainItem = product,
                    onCLickOnMain = { expandedItemId = null }
                )
            } else {
                MainItem_F2(
                    mainItem = product,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                        .animateItem(fadeInSpec = null, fadeOutSpec = null),
                    onCLickOnMain = { expandedItemId = product.id }
                )
            }
        }
    }
}

@Composable
fun ExpandedMainItem_F4(
    mainItem: _ModelAppsFather.ProduitModel,
    modifier: Modifier = Modifier,
    onCLickOnMain: () -> Unit = {},
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(350.dp)
            .background(
                MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(4.dp)
            )
            .clickable { onCLickOnMain() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            // Header with image and basic info
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
            ) {
                GlideDisplayImageById2(
                    mainItem.id,
                    imageGlidReloadTigger = 0,
                    modifier = Modifier
                        .width(100.dp)
                        .height(100.dp),
                    size = 100.dp
                )

                Column(
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .weight(1f)
                ) {
                    Text(
                        text = mainItem.nom,
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "ID: ${mainItem.id}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // List of buyers and their purchases
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(mainItem.bonsVentDeCetteCota) { bonVent ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                MaterialTheme.colorScheme.surfaceVariant,
                                RoundedCornerShape(4.dp)
                            )
                            .padding(8.dp)
                    ) {
                        // Buyer info
                        Text(
                            text = bonVent.clientInformations?.nom ?: "Unknown Client",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        // Colors grid for this buyer
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(4),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            items(bonVent.colours_Achete.filter { it.quantity_Achete > 0 }) { color ->
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .background(
                                            MaterialTheme.colorScheme.surface,
                                            RoundedCornerShape(4.dp)
                                        )
                                        .padding(4.dp)
                                ) {
                                    Text(
                                        text = color.imogi.ifEmpty { color.nom.take(2) },
                                        fontSize = 20.sp
                                    )
                                    Text(
                                        text = "${color.quantity_Achete}",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
