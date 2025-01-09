package com.example.c_serveur.Archives.A3_DiviseProduitsAuCamionFragment.D_FloatingActionButton

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.runtime.key
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
import androidx.compose.ui.zIndex
import com.example.Z_AppsFather.Kotlin._1.Model.ModelAppsFather
import com.example.Z_AppsFather.Kotlin._1.Model.ModelAppsFather.ProduitModel.ClientBonVentModel.ClientInformations.Companion.groupedProductsByClientBonVentModelClientInformations
import com.example.Z_AppsFather.Kotlin._2.ViewModel.ViewModelProduits
import kotlin.math.roundToInt

@Composable
fun ClientsGroupedFABs_Fragment_3(
    produitsMainDataBase: List<ModelAppsFather.ProduitModel>,
    initViewModel: ViewModelProduits,
    modifier: Modifier = Modifier
) {
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }
    var showButtons by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    offsetX += dragAmount.x
                    offsetY += dragAmount.y
                }
            }
            .zIndex(1f),
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AnimatedVisibility(
            visible = showButtons,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                groupedProductsByClientBonVentModelClientInformations(
                    produitsMainDataBase
                ).forEach { (grpFabClientInfo, itProductsAcheter) ->
                    key(grpFabClientInfo.id) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "${grpFabClientInfo.nom} (${itProductsAcheter.size})",
                                modifier = Modifier.padding(end = 8.dp),
                                style = MaterialTheme.typography.bodyMedium
                            )

                            FloatingActionButton(
                                onClick = {
                                    initViewModel
                                        .onClickOn_Fragment_3
                                        .ClientsFloatingActionButton(grpFabClientInfo.id)
                                },
                                modifier = Modifier.size(48.dp),
                                containerColor = Color(android.graphics.Color.parseColor(grpFabClientInfo.couleur))
                            ) {
                                Text(
                                    text = itProductsAcheter.size.toString(),
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }

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
    }
}
