package com.example.Packages.Z_F3._PhoneClientClient

import Z_MasterOfApps.Kotlin.Model.Extension.groupedProductsParClients
import Z_MasterOfApps.Kotlin.Model._ModelAppsFather.Companion.update_AllProduits
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
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
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun MainScreenFilterFAB_F3(
    modifier: Modifier = Modifier,
    viewModelProduits: ViewModelInitApp,
) {
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }
    var showButtons by remember { mutableStateOf(false) }

    val groupedProducts = viewModelProduits._modelAppsFather.groupedProductsParClients

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
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
                    groupedProducts.forEachIndexed { index, (clientInfo, produits) ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (index > 0) {
                                // In your FAB implementation
                                FloatingActionButton(
                                    onClick = {
                                        viewModelProduits.viewModelScope.launch {
                                            val previousClientInfo =
                                                groupedProducts[index - 1].first

                                            clientInfo.positionDonClientsList--
                                            previousClientInfo.positionDonClientsList++

                                            val updatedProducts =
                                                viewModelProduits._modelAppsFather.produitsMainDataBase .map { product ->
                                                    product.apply {
                                                        bonsVentDeCetteCota.forEach { bonVent ->
                                                            bonVent.clientInformations?.let { currentClientInfo ->
                                                                when (currentClientInfo.id) {
                                                                    clientInfo.id -> {
                                                                        val tempPosition =
                                                                            currentClientInfo.positionDonClientsList
                                                                        currentClientInfo.positionDonClientsList =
                                                                            previousClientInfo.positionDonClientsList
                                                                        previousClientInfo.positionDonClientsList =
                                                                            tempPosition
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }

                                            update_AllProduits(
                                                updatedProducts,
                                                viewModelProduits
                                            )
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
                                text = "${clientInfo.nom} (${produits.size})",
                                modifier = Modifier
                                    .weight(1f)
                                    .background(
                                        if (viewModelProduits
                                                ._paramatersAppsViewModelModel
                                                .telephoneClientParamaters
                                                .selectedAcheteurForClient == clientInfo.id
                                        ) MaterialTheme.colorScheme.primaryContainer else Color.Transparent
                                    )
                                    .padding(4.dp),
                                style = MaterialTheme.typography.bodyMedium
                            )

                            FloatingActionButton(
                                onClick = {
                                    viewModelProduits
                                        ._paramatersAppsViewModelModel
                                        .telephoneClientParamaters
                                        .selectedAcheteurForClient = clientInfo.id
                                },
                                modifier = Modifier.size(48.dp),
                                containerColor = Color(android.graphics.Color.parseColor(clientInfo.couleur))
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
