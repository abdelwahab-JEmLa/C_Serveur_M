package com.example.Packages._2.Fragment.UI._5.FloatingActionButton

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.Apps_Head._1.Model.AppsHeadModel
import com.example.Apps_Head._1.Model.AppsHeadModel.Companion.updateProduitsFireBase
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun ClientsGroupedFABs(
    onClientSelected: (AppsHeadModel.ProduitModel.ClientBonVentModel.ClientInformations, List<AppsHeadModel.ProduitModel>) -> Unit,
    produitsMainDataBase: SnapshotStateList<AppsHeadModel.ProduitModel>,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    var showLabels by remember { mutableStateOf(true) }
    var showButtons by remember { mutableStateOf(true) }
    var offset by remember { mutableStateOf(IntOffset(0, 0)) }

    Column(
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(16.dp)
            .offset { offset }
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    offset += IntOffset(dragAmount.x.roundToInt(), dragAmount.y.roundToInt())
                }
            }
            .zIndex(1f)
    ) {
        // Client buttons section
        AnimatedVisibility(
            visible = showButtons,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            val groupedClients = remember(produitsMainDataBase) {
                produitsMainDataBase
                    .flatMap { produit ->
                        produit.bonsVentDeCetteCota.mapNotNull { bonVent ->
                            bonVent.clientInformations?.takeIf {
                                bonVent.colours_Achete.any { color -> color.quantity_Achete > 0 }
                            }?.to(produit)
                        }
                    }
                    .groupBy({ it.first }, { it.second })
            }

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                groupedClients.forEach { (client, products) ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.End
                    ) {
                        // Client label
                        AnimatedVisibility(
                            visible = showLabels,
                            enter = fadeIn() + expandHorizontally(),
                            exit = fadeOut() + shrinkHorizontally()
                        ) {
                            Surface(
                                modifier = Modifier.padding(end = 8.dp),
                                shape = MaterialTheme.shapes.medium,
                                color = if (client.auFilterFAB)
                                    MaterialTheme.colorScheme.error.copy(alpha = 0.6f)
                                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                            ) {
                                Text(
                                    text = "${client.nom} (${products.size})",
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    style = MaterialTheme.typography.bodyMedium,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    color = if (client.auFilterFAB) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        // Client FAB
                        FloatingActionButton(
                            onClick = {
                                scope.launch {
                                    groupedClients.keys.forEach { it.auFilterFAB = it.id == client.id }
                                    produitsMainDataBase.forEach { product ->
                                        product.isVisible = product.bonsVentDeCetteCota
                                            .any { it.clientInformations?.id == client.id }
                                    }
                                    produitsMainDataBase.updateProduitsFireBase()
                                    onClientSelected(client, products)
                                }
                            },
                            modifier = Modifier.size(48.dp),
                            containerColor = Color(android.graphics.Color.parseColor(client.couleur))
                        ) {
                            Text(
                                text = products.size.toString(),
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }

        // Control FABs
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FloatingActionButton(
                onClick = { showLabels = !showLabels },
                modifier = Modifier.size(48.dp),
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ) {
                Icon(
                    imageVector = if (showLabels) Icons.Default.Close else Icons.AutoMirrored.Filled.Label,
                    contentDescription = if (showLabels) "Hide Labels" else "Show Labels"
                )
            }

            FloatingActionButton(
                onClick = { showButtons = !showButtons },
                modifier = Modifier.size(48.dp),
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ) {
                Icon(
                    imageVector = if (showButtons) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (showButtons) "Collapse" else "Expand"
                )
            }
        }
    }
}
