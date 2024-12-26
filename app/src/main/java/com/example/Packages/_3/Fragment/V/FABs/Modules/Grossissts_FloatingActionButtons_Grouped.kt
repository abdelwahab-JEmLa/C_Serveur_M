package com.example.Packages._3.Fragment.V.FABs.Modules

import android.util.Log
import androidx.compose.animation.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.Packages._3.Fragment.Models.UiState
import com.example.App_Produits_Main._1.Model.AppInitializeModel
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun Grossissts_FloatingActionButtons_Grouped(
    modifier: Modifier = Modifier,
    ui_State: UiState,
    app_Initialize_Model: AppInitializeModel,
) {
    val scope = rememberCoroutineScope()

    // Grouper les produits par grossist en utilisant equals() personnalisé
    val grouped_Produits_Par_grossistInformations = remember(app_Initialize_Model.produits_Main_DataBase) {
        val grouped = app_Initialize_Model.produits_Main_DataBase
            .filter { it.bonCommendDeCetteCota?.grossistInformations != null }
            .groupBy { it.bonCommendDeCetteCota?.grossistInformations }

        // Log des informations de groupage
        Log.d("GrossistGrouping", """
            -------- Grouping Details --------
            Total products: ${app_Initialize_Model.produits_Main_DataBase.size}
            Products with grossists: ${grouped.values.sumOf { it.size }}
            Number of groups: ${grouped.size}
            
            Groups breakdown:
            ${grouped.entries.joinToString("\n") { (grossist, products) ->
            """
                Grossist: ${grossist?.nom ?: "Unknown"}
                Color: ${grossist?.couleur ?: "N/A"}
                Products count: ${products.size}
                Product names: ${products.joinToString(", ") { it.nom }}
            """.trimIndent()
        }}
        """.trimIndent())

        grouped
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
                        if (products.isNotEmpty() && grossistModel != null) {
                            FabButton(
                                supplierProductssize = products.size,
                                label = grossistModel.nom,
                                color = Color(android.graphics.Color.parseColor(grossistModel.couleur)),
                                showLabel = showLabels,
                                isFiltered = grossistModel.auFilterFAB,
                                onClick = {
                                    scope.launch {
                                        try {
                                            // Réinitialiser tous les filtres
                                            app_Initialize_Model.produits_Main_DataBase.forEach { product ->
                                                product.auFilterFAB = false
                                            }

                                            grossistModel.auFilterFAB =! grossistModel.auFilterFAB

                                            products.forEach { product ->
                                                product.bonCommendDeCetteCota?.let { bonCommande ->

                                                    val totalQuantity = bonCommande.coloursEtGoutsCommendee.sumOf { it.quantityAchete }
                                                    if (totalQuantity > 0) {
                                                        product.auFilterFAB = true
                                                    }
                                                }
                                            }

                                            // Log des produits filtrés
                                            val filteredProducts = app_Initialize_Model.produits_Main_DataBase
                                                .filter { it.bonCommendDeCetteCota?.grossistInformations?.auFilterFAB == true }

                                            Log.d("FilteredProducts", """
                                                -------- Filtered Products Details --------
                                                Selected Grossist: ${grossistModel.nom}
                                                Total filtered products: ${filteredProducts.size}
                                                
                                                Filtered products list:
                                                ${filteredProducts.joinToString("\n") { product ->
                                                """
                                                        Product: ${product.nom}
                                                    """.trimIndent()
                                            }}
                                            """.trimIndent())

                                            // Mettre à jour Firebase
                                            app_Initialize_Model.update_Produits_FireBase()
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
