package com.example.Packages.A1_Fragment.D_FloatingActionButton

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.Apps_Head._1.Model.AppsHeadModel
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

    // Use derivedStateOf to ensure proper recomposition when the list changes
    val grossistInfosList by remember(produitsMainDataBase) {
        derivedStateOf {
            produitGroupeurParGrossistInfos(produitsMainDataBase)
        }
    }

    // Keep track of the order separately from the content
    var grossistOrder by remember(grossistInfosList) {
        mutableStateOf(grossistInfosList.keys.toList())
    }

    // Create the ordered map based on the current order
    val orderedGrossistInfos by remember(grossistInfosList, grossistOrder) {
        derivedStateOf {
            grossistOrder.associateWith { key ->
                grossistInfosList[key] ?: emptyList()
            }
        }
    }

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
                }
                .zIndex(1f),
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
                    orderedGrossistInfos.entries.toList().forEachIndexed { index, entry ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (index != 0) {
                                FloatingActionButton(
                                    onClick = {
                                        scope.launch {
                                            // Log the current state
                                            println("Before swap - Current order: $grossistOrder")

                                            // Create new order by swapping elements
                                            val newOrder = grossistOrder.toMutableList()
                                            val temp = newOrder[index]
                                            newOrder[index] = newOrder[index - 1]
                                            newOrder[index - 1] = temp

                                            // Update the order state
                                            grossistOrder = newOrder

                                            // Log the new state
                                            println("After swap - New order: ${grossistOrder}")
                                            println("Updated entry at ${index-1}: ${newOrder[index-1]}")
                                            println("Updated entry at $index: ${newOrder[index]}")
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
                                text = "${entry.key.nom} (${entry.value.size})",
                                modifier = Modifier.padding(end = 8.dp),
                                style = MaterialTheme.typography.bodyMedium
                            )

                            FloatingActionButton(
                                onClick = { /* Handle click if needed */ },
                                modifier = Modifier.size(48.dp),
                                containerColor = Color(android.graphics.Color.parseColor(entry.key.couleur))
                            ) {
                                Text(
                                    text = entry.value.size.toString(),
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
