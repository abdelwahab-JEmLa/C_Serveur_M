package com.example.Packages._1.Fragment.A.UI.Main

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
import com.example.Packages._1.Fragment.C.ViewModel.Models.UiState
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun Grossissts_FloatingActionButtons_Grouped(
    modifier: Modifier = Modifier,
    ui_State: UiState,
    app_Initialize_Model: AppsHeadModel,
) {
    val scope = rememberCoroutineScope()

    // Optimized grouping logic with null safety
    val grouped_Produits_Par_grossistInformations = remember(app_Initialize_Model.produitsMainDataBase) {
        app_Initialize_Model.produitsMainDataBase
            .mapNotNull { produit ->
                produit.bonCommendDeCetteCota?.grossistInformations?.let { grossist ->
                    grossist to produit
                }
            }
            .groupBy({ it.first }, { it.second })
            .also { grouped ->
                Log.d("GrossistGrouping", """
                    -------- Grouping Details --------
                    Total products: ${app_Initialize_Model.produitsMainDataBase.size}
                    Products with grossists: ${grouped.values.sumOf { it.size }}
                    Number of groups: ${grouped.size}
                    
                    Groups breakdown:
                    ${grouped.entries.joinToString("\n") { (grossist, products) ->
                    """
                        Grossist: ${grossist.nom}
                        Color: ${grossist.couleur}
                        Products count: ${products.size}
                        Product names: ${products.joinToString(", ") { it.nom }}
                    """.trimIndent()
                }}
                """.trimIndent())
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
                    grouped_Produits_Par_grossistInformations.forEach { (grossistModel, products) ->
                        FabButton(
                            supplierProductssize = products.size,
                            label = grossistModel.nom,
                            color = Color(android.graphics.Color.parseColor(grossistModel.couleur)),
                            showLabel = showLabels,
                            isFiltered = grossistModel.auFilterFAB,
                            onClick = {
                                scope.launch {
                                    try {
                                        // Reset all filters first
                                        app_Initialize_Model.produitsMainDataBase.forEach { product ->
                                            product.isVisible = false
                                            product.bonCommendDeCetteCota?.grossistInformations?.auFilterFAB = false
                                        }

                                        // Set selected grossist filter
                                        grossistModel.auFilterFAB = true

                                        // Filter products for selected grossist
                                        products.forEach { product ->
                                            product.bonCommendDeCetteCota?.let { bonCommande ->
                                                val totalQuantity = bonCommande.coloursEtGoutsCommendee.sumOf { it.quantityAchete }
                                                if (totalQuantity > 0) {
                                                    product.isVisible = true
                                                }
                                            }
                                        }

                                        // Log filtered products
                                        val filteredProducts = app_Initialize_Model.produitsMainDataBase
                                            .filter { it.isVisible }

                                        Log.d("FilteredProducts", """
                                            -------- Filtered Products Details --------
                                            Selected Grossist: ${grossistModel.nom}
                                            Total filtered products: ${filteredProducts.size}
                                            
                                            Filtered products list:
                                            ${filteredProducts.joinToString("\n") { product ->
                                            """
                                                Product: ${product.nom}
                                                Total Quantity: ${product.bonCommendDeCetteCota?.coloursEtGoutsCommendee?.sumOf { it.quantityAchete } ?: 0}
                                            """.trimIndent()
                                        }}
                                        """.trimIndent())

                                        // Update Firebase
                                        app_Initialize_Model.produitsMainDataBase.updateProduitsFireBase()
                                    } catch (e: Exception) {
                                        Log.e("FilterError", "Error while filtering products", e)
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }

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
    supplierProductssize: Int
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
                    text = "$label ($supplierProductssize)",
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
                text = supplierProductssize.toString(),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White
            )
        }
    }
}
