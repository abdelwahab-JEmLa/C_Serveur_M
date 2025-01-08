package com.example.Packages.A_GrosssitsCommendHandler.A1_Fragment

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
import com.example.Packages.A_GrosssitsCommendHandler.A1_Fragment.A_Head.Model_CodingWithMaps.MapsSansModels.TypePosition
import com.example.Packages.A_GrosssitsCommendHandler.A1_Fragment.A_Head.ViewModel_Head

@Composable
fun B_ListMainFragment_1(viewModel: ViewModel_Head, contentPadding: PaddingValues) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(5),
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xE3C85858).copy(alpha = 0.1f), RoundedCornerShape(8.dp)),
        contentPadding = contentPadding,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        viewModel.mapsSansModels.mapGroToMapPositionToProduits.entries.firstOrNull()?.let { grossist ->
            grossist.value.forEach { (isPosition, products) ->
                if (products.isNotEmpty()) {
                    // Header
                    item(span = { GridItemSpan(5) }) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (isPosition != TypePosition.POSITIONE) {
                                IconButton(onClick = {}) { Icon(Icons.Default.Search, null) }
                            }
                            Text(
                                "${if (isPosition == TypePosition.POSITIONE) "Avec" else "Sans"} position (${products.size})",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }

                    // Items
                    items(products.keys.toList()) { product ->
                        C_ItemMainFragment_1(
                            viewModel_Head = viewModel,
                            itemMainId = product,
                            position = if (isPosition == TypePosition.POSITIONE) products.keys.indexOf(product) + 1 else null,
                            onCLickOnMain = {
                                viewModel.apply {
                                    val newMap = _mapsSansModels.mapGroToMapPositionToProduits.toMutableMap()
                                    val positionMap = grossist.value.toMutableMap()
                                    val currentProducts = positionMap[isPosition]?.toMutableMap() ?: mutableMapOf()
                                    val otherProducts = positionMap[if (isPosition == TypePosition.POSITIONE) TypePosition.NON_POSITIONE else TypePosition.POSITIONE]?.toMutableMap() ?: mutableMapOf()

                                    currentProducts.remove(product)?.let { colors ->
                                        otherProducts[product] = colors
                                    }

                                    positionMap[isPosition] = currentProducts
                                    positionMap[if (isPosition == TypePosition.POSITIONE) TypePosition.NON_POSITIONE else TypePosition.POSITIONE] = otherProducts
                                    newMap[grossist.key] = positionMap

                                    _mapsSansModels = _mapsSansModels.apply {
                                        mapGroToMapPositionToProduits = newMap
                                    }
                                }
                            },
                            modifier = Modifier.animateItem(null, null)
                        )
                    }
                }
            }
        }
    }
}
