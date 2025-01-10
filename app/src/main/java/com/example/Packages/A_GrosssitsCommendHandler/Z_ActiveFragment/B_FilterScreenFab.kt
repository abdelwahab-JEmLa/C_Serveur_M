package com.example.Packages.A_GrosssitsCommendHandler.Z_ActiveFragment

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material3.MaterialTheme
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
import com.example.Y_AppsFather.Kotlin.ModelAppsFather.Companion.updateAllProduitsUiEtFireBases
import com.example.Y_AppsFather.Kotlin.ModelAppsFather.Companion.updatePoduitsUiEtFireBases
import com.example.Y_AppsFather.Kotlin.ViewModelProduits
import kotlin.math.roundToInt

@Composable
fun FilterScreenFab(
    modifier: Modifier = Modifier,
    viewModelProduits: ViewModelProduits
) {
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }
    var showButtons by remember { mutableStateOf(false) }

    // Access groupedProducts through the viewModel
    val groupedProducts = viewModelProduits._modelAppsFather.groupedProducts
    val produitsMainDataBase = viewModelProduits._modelAppsFather.produitsMainDataBase

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomEnd
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        offsetX += dragAmount.x
                        offsetY += dragAmount.y
                    }
                },
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FloatingActionButton(
                onClick = { showButtons = !showButtons },
                modifier = Modifier.size(48.dp),
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ) {
                Icon(
                    imageVector = if (showButtons) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (showButtons) "Hide" else "Show"
                )
            }

            AnimatedVisibility(
                visible = showButtons,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    groupedProducts.forEachIndexed { index, (grossist, produits) ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (index > 0) {
                                FloatingActionButton(
                                    onClick = {
                                        val previousGrossist = groupedProducts[index - 1].first

                                        // Update positions
                                        produitsMainDataBase.forEach { product ->
                                            product.bonCommendDeCetteCota?.grossistInformations?.let { currentGrossist ->
                                                when (currentGrossist.id) {
                                                    grossist.id -> {
                                                        currentGrossist.positionInGrossistsList--
                                                    }
                                                    previousGrossist.id -> {
                                                        currentGrossist.positionInGrossistsList++
                                                    }
                                                }
                                            }
                                        }
                                        updateAllProduitsUiEtFireBases(viewModelProduits, viewModelProduits.produitsMainDataBase)
                                    },
                                    modifier = Modifier.size(36.dp),
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ExpandLess,
                                        contentDescription = "Move Up"
                                    )
                                }
                            }

                            Text(
                                text = "${grossist.nom} (${produits.size})",
                                modifier = Modifier
                                    .padding(end = 8.dp)
                                    .background(
                                        if (grossist.auFilterFAB) Color.Blue else Color.Transparent
                                    )
                                    .padding(4.dp),
                                style = MaterialTheme.typography.bodyMedium
                            )

                            FloatingActionButton(
                                onClick = {
                                    produitsMainDataBase.forEach { product ->
                                        product.isVisible = product.bonCommendDeCetteCota?.let { bon ->
                                            bon.grossistInformations?.id == grossist.id
                                        } ?: false
                                    }
                                    produitsMainDataBase.updatePoduitsUiEtFireBases(viewModelProduits)
                                },
                                modifier = Modifier.size(48.dp),
                                containerColor = Color(android.graphics.Color.parseColor(grossist.couleur))
                            ) {
                                Text(
                                    text = produits.size.toString(),
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
