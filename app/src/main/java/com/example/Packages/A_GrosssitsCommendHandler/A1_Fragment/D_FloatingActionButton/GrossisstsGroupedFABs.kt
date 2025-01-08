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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.Apps_Head._1.Model.AppsHeadModel
import com.example.Packages.A_GrosssitsCommendHandler.A1_Fragment.A_Head.ViewModel_Head
import kotlin.math.roundToInt

@Composable
fun GrossisstsGroupedFABsFragment_1(
    produitsMainDataBase: List<AppsHeadModel.ProduitModel>,
    viewModel_Head: ViewModel_Head = viewModel(),

    modifier: Modifier = Modifier,
    onClick: (Pair<AppsHeadModel.ProduitModel.GrossistBonCommandes.GrossistInformations, List<AppsHeadModel.ProduitModel>>?) -> Unit
) {
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }
    var showButtons by remember { mutableStateOf(false) }
    val grossistList = viewModel_Head._mapsModel.maps.grossistList
    

    // LaunchedEffect to handle Firebase operations
    
    var visibleGrossistAssociatedProduits by remember {
        mutableStateOf<Pair<AppsHeadModel.ProduitModel.GrossistBonCommandes.GrossistInformations, List<AppsHeadModel.ProduitModel>>
        ?>(null)
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
                },
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Main FAB
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

            // Animated content
            AnimatedVisibility(
                visible = showButtons,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    grossistList.forEachIndexed { index, entry ->
                        val (grossist, produits) = entry
                        Row(    //->
                            //TODO(FIXME):Fix erreur 
                            @Composable invocations can only happen from the context of a @Composable function
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (index > 0) {
                                FloatingActionButton(
                                    onClick = {
                                        grossistList = grossistList.toMutableList().apply {      //->
                                            //TODO(FIXME):Fix erreur Val cannot be reassigned
                                            val temp = this[index]
                                            this[index] = this[index - 1]
                                            this[index - 1] = temp
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
                                text = grossist.nom,
                                modifier = Modifier
                                    .padding(end = 8.dp)
                                    .background(
                                        if (visibleGrossistAssociatedProduits?.first == grossist) Color.Blue
                                        else Color.Transparent
                                    )
                                    .padding(4.dp),
                                style = MaterialTheme.typography.bodyMedium
                            )

                            FloatingActionButton(
                                onClick = {
                                    visibleGrossistAssociatedProduits = entry
                                    onClick(visibleGrossistAssociatedProduits)
                                },
                                modifier = Modifier.size(48.dp),
                                containerColor = Color(android.graphics.Color.parseColor(grossist.couleur))
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


