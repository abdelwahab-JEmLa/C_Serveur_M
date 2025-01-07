package com.example.Packages.A1_Fragment.D_FloatingActionButton

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.Apps_Head._1.Model.AppsHeadModel
import com.example.Apps_Head._1.Model.AppsHeadModel.Companion.update_produitsViewModelEtFireBases
import com.example.Apps_Head._1.Model.AppsHeadModel.ProduitModel.GrossistBonCommandes.GrossistInformations.Companion.produitGroupeurParGrossistInfos
import com.example.Apps_Head._2.ViewModel.InitViewModel
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun GrossisstsGroupedFABsFragment_1(
    produitsMainDataBase: List<AppsHeadModel.ProduitModel>,
    initViewModel: InitViewModel,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }
    var showButtons by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    offsetX += dragAmount.x
                    offsetY += dragAmount.y
                }
            }
            .zIndex(1f),
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AnimatedVisibility(
            visible = showButtons,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val groupedProducts = produitGroupeurParGrossistInfos(produitsMainDataBase)
                groupedProducts.forEach { (grossistInformations, products) ->
                    key(grossistInformations.id) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Up button to move grossist position
                            if (grossistInformations.positionInGrossistsList > 0) {
                                FloatingActionButton(
                                    onClick = {
                                        scope.launch {
                                            val updatedList = produitsMainDataBase.toMutableList()

                                            // Create a set of all unique GrossistInformations
                                            val grossistInfosSet = groupedProducts.keys.toMutableSet()

                                            // Find the grossist that should be swapped with current one
                                            val previousGrossist = grossistInfosSet.find {
                                                it.positionInGrossistsList == grossistInformations.positionInGrossistsList - 1
                                            }

                                            if (previousGrossist != null) {
                                                // Update positions
                                                updatedList.forEach { product ->
                                                    product.bonCommendDeCetteCota?.grossistInformations?.let { grossist ->
                                                        when (grossist.id) {
                                                            grossistInformations.id -> {
                                                                grossist.positionInGrossistsList--
                                                            }
                                                            previousGrossist.id -> {
                                                                grossist.positionInGrossistsList++
                                                            }
                                                        }
                                                    }
                                                }

                                                // Update Firebase and ViewModel
                                                updatedList
                                                    .toMutableStateList()
                                                    .update_produitsViewModelEtFireBases(initViewModel)
                                            }
                                        }
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
                                text = "${grossistInformations.nom} (${products.size})",
                                modifier = Modifier.padding(end = 8.dp),
                                style = MaterialTheme.typography.bodyMedium
                            )

                            FloatingActionButton(
                                onClick = {
                                    scope.launch {
                                        val updatedList = produitsMainDataBase.toMutableList()
                                        updatedList.forEach { product ->
                                            product.isVisible = product.bonCommendDeCetteCota?.let { bon ->
                                                bon.grossistInformations?.id == grossistInformations.id
                                            } ?: false
                                            product.bonCommendDeCetteCota
                                                ?.grossistInformations?.auFilterFAB = true
                                        }
                                        updatedList
                                            .toMutableStateList()
                                            .update_produitsViewModelEtFireBases(initViewModel)
                                    }
                                },
                                modifier = Modifier.size(48.dp),
                                containerColor = Color(android.graphics.Color.parseColor(grossistInformations.couleur))
                            ) {
                                Text(
                                    text = products.size.toString(),
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }

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
    }
}
