package com.example.Packages.Z_F4._New

import Z_MasterOfApps.Kotlin.Model.Extension.groupedProductsPatGrossist
import Z_MasterOfApps.Kotlin.Model._ModelAppsFather.Companion.updateProduit
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun MainScreenFilterFAB_F4(
    modifier: Modifier = Modifier,
    viewModelInitApp: ViewModelInitApp,
) {
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }
    var showButtons by remember { mutableStateOf(false) }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        offsetX += dragAmount.x
                        offsetY += dragAmount.y
                    }
                },
            horizontalAlignment = Alignment.End
        ) {
            FloatingActionButton(
                onClick = { showButtons = !showButtons },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = if (showButtons) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null
                )
            }

            AnimatedVisibility(visible = showButtons) {
                Column(horizontalAlignment = Alignment.End) {
                    viewModelInitApp._modelAppsFather.groupedProductsPatGrossist
                        .forEachIndexed { index, (grossist, produits) ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                if (index > 0) {
                                    FloatingActionButton(
                                        onClick = {
                                            viewModelInitApp.viewModelScope.launch {
                                                val groupedProductsPatGrossist = viewModelInitApp
                                                    ._modelAppsFather
                                                    .groupedProductsPatGrossist

                                                val nonDefini = groupedProductsPatGrossist
                                                    .find { it.first.nom == "Non Defini"}

                                                // Safely handle the nullable pair and its contents
                                                nonDefini?.let { (_, products) ->
                                                    if (products.isNotEmpty()) {
                                                        val product = products.first()
                                                        viewModelInitApp._modelAppsFather.produitsMainDataBase
                                                            .find { it.id == product.id }?.let { foundProduct ->
                                                                // Create updated product with new grossist information
                                                                foundProduct.bonCommendDeCetteCota?.let { bonCommande ->
                                                                    bonCommande.grossistInformations = grossist
                                                                    // Update the product in the database
                                                                    updateProduit(
                                                                        product = foundProduct,
                                                                        viewModelProduits = viewModelInitApp
                                                                    )
                                                                }
                                                            }
                                                    }
                                                }
                                            }
                                        },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(Icons.Default.ExpandLess, null)
                                    }
                                }

                                Text(
                                    "${grossist.nom} (${produits.size})",
                                    modifier = Modifier
                                        .background(
                                            if (viewModelInitApp._paramatersAppsViewModelModel
                                                    .telephoneClientParamaters.selectedGrossistForServeur == grossist.id
                                            ) Color(0xFF2196F3) else Color.Transparent
                                        )
                                        .padding(4.dp)
                                )

                                FloatingActionButton(
                                    onClick = {
                                        viewModelInitApp._paramatersAppsViewModelModel
                                            .telephoneClientParamaters.selectedGrossistForServeur = grossist.id
                                    },
                                    modifier = Modifier.size(48.dp),
                                    containerColor = try {
                                        Color(android.graphics.Color.parseColor(
                                            if (grossist.couleur.startsWith("#")) grossist.couleur
                                            else "#${grossist.couleur}"
                                        ))
                                    } catch (e: Exception) {
                                        Color(0xFFFF0000)
                                    }
                                ) {
                                    Text(produits.size.toString())
                                }
                            }
                        }
                }
            }
        }
    }
}
