// GrossisstsGroupedFABs.kt
package com.example.Packages.A_GrosssitsCommendHandler.A1_Fragment.D_FloatingActionButton

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
import com.example.Packages.A_GrosssitsCommendHandler.A1_Fragment.A_Head.Model.Maps
import com.example.Packages.A_GrosssitsCommendHandler.A1_Fragment.A_Head.Model.Modules.ArticleLoggingUtil
import com.example.Packages.A_GrosssitsCommendHandler.A1_Fragment.A_Head.Model.TypePosition
import com.example.Packages.A_GrosssitsCommendHandler.A1_Fragment.A_Head.Model.ViewModel.ViewModel_Head
import kotlin.math.roundToInt

@Composable
fun GrossisstsGroupedFABs(viewModel_Head: ViewModel_Head) {
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }
    var showButtons by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxSize(),
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
                },
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FloatingActionButton(
                onClick = { showButtons = !showButtons },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = if (showButtons) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (showButtons) "Masquer" else "Afficher"
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
                    viewModel_Head.maps.mapGroToMapPositionToProduits.forEachIndexed { index, entry ->
                        val grossist = entry.key
                        val positionMap = entry.value

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            if (index > 0) {
                                FloatingActionButton(
                                    onClick = {
                                        val currentList = viewModel_Head.maps.mapGroToMapPositionToProduits
                                        currentList.add(index - 1, currentList.removeAt(index))
                                        Maps.updateMapFromPositionedLists(index - 1, viewModel_Head, true)
                                    },
                                    modifier = Modifier.size(32.dp),
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ExpandLess,
                                        contentDescription = "Monter"
                                    )
                                }
                            }

                            Text(
                                text = grossist.nom,
                                modifier = Modifier
                                    .background(
                                        if (viewModel_Head.selectedGrossistIndex == index)
                                            MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                        else Color.Transparent
                                    )
                                    .padding(4.dp)
                            )

                            FloatingActionButton(
                                onClick = {
                                    viewModel_Head.selectedGrossistIndex = index
                                    viewModel_Head._maps.positionedArticles.clear()
                                    viewModel_Head._maps.positionedArticles.addAll(
                                        positionMap[TypePosition.POSITIONE] ?: mutableListOf()
                                    )
                                    viewModel_Head._maps.nonPositionedArticles.clear()
                                    viewModel_Head._maps.nonPositionedArticles.addAll(
                                        positionMap[TypePosition.NON_POSITIONE] ?: mutableListOf()
                                    )
                                    ArticleLoggingUtil.logArticleListChange(
                                        grossistName = grossist.nom,
                                        positionedArticles = viewModel_Head._maps.positionedArticles,
                                        nonPositionedArticles = viewModel_Head._maps.nonPositionedArticles
                                    )
                                },
                                modifier = Modifier.size(40.dp)
                            ) {
                                Text(text = "")
                            }
                        }
                    }
                }
            }
        }
    }
}
