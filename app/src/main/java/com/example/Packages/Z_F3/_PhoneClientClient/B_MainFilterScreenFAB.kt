package com.example.Packages.Z_F3._PhoneClientClient

import Z_MasterOfApps.Kotlin.Model.Extension.groupedProductsParClients
import Z_MasterOfApps.Kotlin.Model._ModelAppsFather.Companion.update_AllProduits
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
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/**
 * Safely parses a color string to a Color object
 * @param colorString The color string to parse (can be with or without # prefix)
 * @return Color object, falls back to red if parsing fails
 */
private fun parseColorSafely(colorString: String?): Color {
    return try {
        val normalizedColor = when {
            colorString.isNullOrEmpty() -> "#FF0000"
            colorString.startsWith("#") -> colorString
            else -> "#$colorString"
        }
        Color(android.graphics.Color.parseColor(normalizedColor))
    } catch (e: Exception) {
        Color(0xFFFF0000) // Fallback to red
    }
}

@Composable
fun MainScreenFilterFAB_F3(
    modifier: Modifier = Modifier,
    viewModelProduits: ViewModelInitApp,
) {
    // State for drag offset
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    // State for showing/hiding buttons
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
            // Main FAB to toggle visibility
            FloatingActionButton(
                onClick = { showButtons = !showButtons },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = if (showButtons) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (showButtons) "Hide options" else "Show options"
                )
            }

            // Animated content for client list
            AnimatedVisibility(visible = showButtons) {
                Column(
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    viewModelProduits._modelAppsFather.groupedProductsParClients
                        .forEachIndexed { index, (clientInfo, produits) ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.padding(vertical = 4.dp)
                            ) {
                                // Up button (except for first item)
                                if (index > 0) {
                                    FloatingActionButton(
                                        onClick = {
                                            viewModelProduits.viewModelScope.launch {
                                                val previousClientInfo = viewModelProduits._modelAppsFather
                                                    .groupedProductsParClients[index - 1].first

                                                val updatedProducts = viewModelProduits._modelAppsFather
                                                    .produitsMainDataBase.map { product ->
                                                        product.apply {
                                                            bonsVentDeCetteCota.forEach { bonVent ->
                                                                bonVent.clientInformations?.let { currentClientInfo ->
                                                                    if (currentClientInfo.id == clientInfo.id) {
                                                                        val tempPosition = currentClientInfo.positionDonClientsList
                                                                        currentClientInfo.positionDonClientsList = previousClientInfo.positionDonClientsList
                                                                        previousClientInfo.positionDonClientsList = tempPosition
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                update_AllProduits(updatedProducts, viewModelProduits)
                                            }
                                        },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.ExpandLess,
                                            contentDescription = "Move ${clientInfo.nom} up"
                                        )
                                    }
                                }

                                // Client name and count
                                Text(
                                    "${clientInfo.nom} (${produits.size})",
                                    modifier = Modifier
                                        .weight(1f)
                                        .background(
                                            if (viewModelProduits._paramatersAppsViewModelModel
                                                    .phoneClientSelectedAcheteur == clientInfo.id
                                            ) MaterialTheme.colorScheme.primaryContainer
                                            else Color.Transparent
                                        )
                                        .padding(4.dp)
                                )

                                // Client selection FAB
                                val containerColor = remember(clientInfo.couleur) {
                                    parseColorSafely(clientInfo.couleur)
                                }

                                FloatingActionButton(
                                    onClick = {
                                        viewModelProduits._paramatersAppsViewModelModel
                                            .phoneClientSelectedAcheteur = clientInfo.id
                                    },
                                    modifier = Modifier.size(48.dp),
                                    containerColor = containerColor
                                ) {
                                    Text(
                                        produits.size.toString(),
                                        color = if (containerColor.luminance() > 0.5f) Color.Black else Color.White
                                    )
                                }
                            }
                        }
                }
            }
        }
    }
}
