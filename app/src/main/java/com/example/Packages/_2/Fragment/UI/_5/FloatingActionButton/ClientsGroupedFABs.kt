package com.example.Packages._2.Fragment.UI._5.FloatingActionButton

import android.util.Log
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.runtime.mutableFloatStateOf
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
    onClickFAB: (SnapshotStateList<AppsHeadModel.ProduitModel>) -> Unit,
    produitsMainDataBase: SnapshotStateList<AppsHeadModel.ProduitModel>,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()

    // Group products by client information and count products per client
    val groupedProduitsParClient = remember(produitsMainDataBase) {
        produitsMainDataBase.flatMap { produit ->
            produit.bonsVentDeCetteCota.mapNotNull { bonVent ->
                bonVent.clientInformations?.let { client ->
                    // Only include clients who have actually purchased products
                    if (bonVent.colours_Achete.any { it.quantity_Achete > 0 }) {
                        client to produit
                    } else null
                }
            }
        }.groupBy(
            { it.first }, // Group by client
            { it.second }  // Keep the products
        ).also { grouped ->
            Log.d(
                "Grouping",
                """
                -------- Client Grouping Details --------
                Total products: ${produitsMainDataBase.size}
                Active clients: ${grouped.size}
                Client breakdown:
                ${
                    grouped.entries.joinToString("\n") { (client, products) ->
                        """
                        Client: ${client.nom}
                        Color: ${client.couleur}
                        Products count: ${products.size}
                        Products: ${products.joinToString(", ") { it.nom }}
                        """.trimIndent()
                    }
                }
                """.trimIndent()
            )
        }
    }

    var showLabels by remember { mutableStateOf(true) }
    var showFloatingButtons by remember { mutableStateOf(false) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    Column(
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(16.dp)
            .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    offsetX += dragAmount.x
                    offsetY += dragAmount.y
                }
            }
            .zIndex(1f)
    ) {
        AnimatedVisibility(
            visible = showFloatingButtons,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Surface(
                modifier = Modifier.wrapContentHeight(),
                color = Color.Transparent
            ) {
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    groupedProduitsParClient.forEach { (client, products) ->
                        FabButton(
                            productsSize = products.size,
                            label = client.nom,
                            color = Color(android.graphics.Color.parseColor(client.couleur)),
                            showLabel = showLabels,
                            isFiltered = client.auFilterFAB,
                            onClick = {
                                scope.launch {
                                    try {
                                        // Reset all filters first
                                        groupedProduitsParClient.keys.forEach { c ->
                                            c.auFilterFAB = c.id == client.id
                                        }

                                        // Update product visibility based on client selection
                                        produitsMainDataBase.forEach { product ->
                                            product.isVisible = product.bonsVentDeCetteCota
                                                .any { bonVent ->
                                                    bonVent.clientInformations?.id == client.id &&
                                                            bonVent.colours_Achete.any { it.quantity_Achete > 0 }
                                                }
                                        }

                                        onClickFAB(produitsMainDataBase)
                                        produitsMainDataBase.updateProduitsFireBase()

                                    } catch (e: Exception) {
                                        Log.e("FilterError", "Error filtering products", e)
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }

        // Label toggle FAB
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

        // Expand/collapse FAB
        FloatingActionButton(
            onClick = { showFloatingButtons = !showFloatingButtons },
            modifier = Modifier.size(48.dp),
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ) {
            Icon(
                imageVector = if (showFloatingButtons) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = if (showFloatingButtons) "Collapse" else "Expand"
            )
        }
    }
}

@Composable
private fun FabButton(
    label: String,
    color: Color,
    showLabel: Boolean,
    isFiltered: Boolean = false,
    onClick: () -> Unit,
    productsSize: Int
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End
    ) {
        AnimatedVisibility(
            visible = showLabel,
            enter = fadeIn() + expandHorizontally(),
            exit = fadeOut() + shrinkHorizontally()
        ) {
            Surface(
                modifier = Modifier.padding(end = 8.dp),
                shape = MaterialTheme.shapes.medium,
                color = if (isFiltered)
                    MaterialTheme.colorScheme.error.copy(alpha = 0.6f)
                else
                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
            ) {
                Text(
                    text = "$label ($productsSize)",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = if (isFiltered) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        FloatingActionButton(
            onClick = onClick,
            modifier = Modifier.size(48.dp),
            containerColor = color
        ) {
            Text(
                text = productsSize.toString(),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White
            )
        }
    }
}
