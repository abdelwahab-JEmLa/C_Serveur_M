package com.example.Packages._3.Fragment.V.FABs.Modules

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.example.Packages._3.Fragment.Models.UiState
import com.example.Packages._3.Fragment.ViewModel.F3_ViewModel
import com.example.c_serveur.ViewModel.Model.App_Initialize_Model
import kotlin.math.roundToInt
import kotlin.random.Random

private const val FAB_TAG = "FAB_DEBUG"

@Composable
internal fun Grossissts_FloatingActionButtons_Grouped(
    modifier: Modifier = Modifier,
    ui_State: UiState,
    produits_Main_DataBase: SnapshotStateList<App_Initialize_Model.Produit_Main_DataBase>,
) {
    val grouped_Produits_Par_Id_Grossist = remember(produits_Main_DataBase) {
        val groupedProducts = produits_Main_DataBase.groupBy { produit ->
            produit.grossist_Choisi_Pour_Acheter_CeProduit
                .maxByOrNull { it.date }?.vid ?: -1L
        }

        // Log the grouping results
        Log.d(FAB_TAG, "Grouped products by supplier:")
        groupedProducts.forEach { (supplierId, products) ->
            Log.d(FAB_TAG, "Supplier ID: $supplierId, Product Count: ${products.size}")
        }

        groupedProducts
    }
    
    Log.d(FAB_TAG, "Total grouped entries: ${grouped_Produits_Par_Id_Grossist.size}")
    grouped_Produits_Par_Id_Grossist.forEach { (supplierId, products) ->
        Log.d(FAB_TAG, "Group for supplier $supplierId has ${products.size} products")
    }
    var showLabels by remember { mutableStateOf(true) }
    var showFloatingButtons by remember { mutableStateOf(false) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    // Log state changes
    LaunchedEffect(showLabels, showFloatingButtons) {
        Log.d(FAB_TAG, "showLabels: $showLabels")
        Log.d(FAB_TAG, "showFloatingButtons: $showFloatingButtons")
    }

    // Remember supplier colors to keep them consistent
    val supplierColors = remember {
        val colors = grouped_Produits_Par_Id_Grossist.keys.associateWith {
            Color(
                red = Random.nextFloat(),
                green = Random.nextFloat(),
                blue = Random.nextFloat(),
                alpha = 1f
            )
        }

        // Log supplier colors
        colors.forEach { (supplierId, color) ->
            Log.d(FAB_TAG, "Supplier $supplierId color: $color")
        }

        colors
    }

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
        // Extensive logging for the AnimatedVisibility of floating buttons
        LaunchedEffect(showFloatingButtons) {
            Log.d(FAB_TAG, "AnimatedVisibility showFloatingButtons: $showFloatingButtons")
        }

        AnimatedVisibility(
            visible = showFloatingButtons,
            enter = fadeIn() + expandHorizontally(),
            exit = fadeOut() + shrinkHorizontally()
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
                    val filteredSuppliers = grouped_Produits_Par_Id_Grossist
                        .filter { it.key != -1L } // Filter out products without a supplier

                    Log.d(FAB_TAG, "Filtered suppliers count: ${filteredSuppliers.size}")

                    filteredSuppliers.forEach { (supplierId, products) ->
                        val supplier = products.firstOrNull()
                            ?.grossist_Choisi_Pour_Acheter_CeProduit
                            ?.maxByOrNull { it.date }

                        if (supplier != null) {
                            Log.d(FAB_TAG, "Creating FAB for supplier: ${supplier.nom}, ID: $supplierId, Products: ${products.size}")

                            FabButton(
                                supplierProductssize = products.size,
                                label = supplier.nom,
                                color = supplierColors[supplierId] ?: MaterialTheme.colorScheme.primary,
                                showLabel = showLabels,
                                isFiltered = ui_State.selectedSupplierId == supplierId,
                                onClick = {
                                    Log.d(FAB_TAG, "FAB clicked for supplier $supplierId")
                                    ui_State.selectedSupplierId =
                                        if (ui_State.selectedSupplierId == supplierId) 0L else supplierId
                                }
                            )
                        } else {
                            Log.w(FAB_TAG, "No supplier found for ID: $supplierId")
                        }
                    }
                }
            }
        }

        // Toggle labels button with logging
        FloatingActionButton(
            onClick = {
                Log.d(FAB_TAG, "Toggle labels button clicked. Current state: $showLabels")
                showLabels = !showLabels
            },
            modifier = Modifier.size(48.dp),
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ) {
            Icon(
                imageVector = if (showLabels) Icons.Default.Close else Icons.AutoMirrored.Filled.Label,
                contentDescription = if (showLabels) "Hide Labels" else "Show Labels"
            )
        }

        FloatingActionButton(
            onClick = {
                Log.d(FAB_TAG, "Expand/collapse button clicked. Current state: $showFloatingButtons")
                showFloatingButtons = !showFloatingButtons
            },
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
