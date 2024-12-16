package com.example.Packages._4.Fragment.UI._Y.Modules

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
import com.example.Packages._4.Fragment._1.Main.Model.Ui_State_4_Fragment
import com.example.c_serveur.ViewModel.Model.App_Initialize_Model
import kotlin.math.roundToInt
import kotlin.random.Random

private const val FAB_TAG = "FAB_DEBUG"

@Composable
internal fun Second_Grouped_FloatingActionButtons(
    modifier: Modifier = Modifier,
    uiState: Ui_State_4_Fragment,
    produit_Main_DataBase: SnapshotStateList<App_Initialize_Model.Produit_Main_DataBase>,
) {

    val grouped_Produits_Par_Id_Acheteur = remember(produit_Main_DataBase) {
        val groupedProducts = produit_Main_DataBase.groupBy { produit ->
            produit.demmende_Achate_De_Cette_Produit
                .maxByOrNull { it.time_String }
                ?.id_Acheteur ?: -1L
        }

        // Log the grouping results
        Log.d(FAB_TAG, "Grouped products by buyer:")
        groupedProducts.forEach { (buyerId, products) ->
            Log.d(FAB_TAG, "Buyer ID: $buyerId, Product Count: ${products.size}")
        }

        groupedProducts
    }

    Log.d(FAB_TAG, "Total grouped entries: ${grouped_Produits_Par_Id_Acheteur.size}")
    grouped_Produits_Par_Id_Acheteur.forEach { (Id, products) ->
        Log.d(FAB_TAG, "Group for  $Id has ${products.size} products")
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

    // Remember  colors to keep them consistent
    val achteurColors = remember {
        val colors = grouped_Produits_Par_Id_Acheteur.keys.associateWith {
            Color(
                red = Random.nextFloat(),
                green = Random.nextFloat(),
                blue = Random.nextFloat(),
                alpha = 1f
            )
        }

        // Log  colors
        colors.forEach { (Id, color) ->
            Log.d(FAB_TAG, " $Id color: $color")
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
                    val filteredacheteur = grouped_Produits_Par_Id_Acheteur
                        .filter { it.key != -1L } // Filter out products without a acheteur

                    Log.d(FAB_TAG, "Filtered acheteur count: ${filteredacheteur.size}")

                    filteredacheteur.forEach { (acheteurId, products) ->
                        val acheteur = products.firstOrNull()
                            ?.demmende_Achate_De_Cette_Produit
                            ?.maxByOrNull { it.time_String }

                        if (acheteur != null) {
                            Log.d(FAB_TAG, "Creating FAB for acheteur: ${acheteur.nom_Acheteur}, ID: $acheteurId, Products: ${products.size}")

                            FabButton(
                                acheteurProductssize = products.size,
                                label = acheteur.nom_Acheteur,
                                color = achteurColors[acheteurId] ?: MaterialTheme.colorScheme.primary,
                                showLabel = showLabels,
                                isFiltered = uiState.selected_Client_Id == acheteurId,
                                onClick = {
                                    Log.d(FAB_TAG, "FAB clicked for Acheteur $acheteurId")
                                    uiState.selected_Client_Id =
                                        if (uiState.selected_Client_Id == acheteurId) 0L else acheteurId
                                }
                            )
                        } else {
                            Log.w(FAB_TAG, "No Acheteur found for ID: $acheteurId")
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
    acheteurProductssize: Int
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
                    text = "$label ($acheteurProductssize)",
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
                text = acheteurProductssize.toString(),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White
            )
        }
    }
}
