package Z_MasterOfApps.Z.Android.Base.App.App._1.GerantAfficheurGrossistCommend.App.NH_1.id4_DeplaceProduitsVerGrossist

import Z_MasterOfApps.Kotlin.Model._ModelAppsFather.Companion.updateProduit
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
fun MainScreenFilterFAB_F4(
    modifier: Modifier = Modifier,
    viewModel: ViewModelInitApp,
) {
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }
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
            FloatingActionButton(
                onClick = { showButtons = !showButtons },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = if (showButtons) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null
                )
            }

            AnimatedVisibility(visible = showButtons) {
                Column(horizontalAlignment = Alignment.End) {
                    viewModel._modelAppsFather.grossistsDataBase.forEachIndexed { index, grossist ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (index > 0) {
                                FloatingActionButton(
                                    onClick = {
                                        viewModel.viewModelScope.launch {
                                            viewModel.frag1_A1_ExtVM.upButton(index)
                                        }
                                    },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(Icons.Default.ExpandLess, null)
                                }
                            }

                            Text(
                                grossist.nom,
                                modifier = Modifier
                                    .background(
                                        if (viewModel.frag1_A1_ExtVM.idAuFilter == grossist.id
                                        ) Color(0xFF2196F3) else Color.Transparent
                                    )
                                    .padding(4.dp)
                            )

                            FloatingActionButton(
                                onClick = {
                                    viewModel.viewModelScope.launch {
                                        val nonDefiniProduct = viewModel.produitsMainDataBase
                                            .firstOrNull { product ->
                                                product.bonCommendDeCetteCota
                                                    ?.grossistInformations?.nom == "Non Defini"
                                            }

                                        nonDefiniProduct?.let { product ->
                                            product.bonCommendDeCetteCota?.let { bonCommande ->
                                                bonCommande.grossistInformations?.id = grossist.id
                                                updateProduit(
                                                    product = product,
                                                    viewModelProduits = viewModel
                                                )
                                            }
                                        }
                                    }
                                },
                                modifier = Modifier.size(48.dp),
                                containerColor = try {
                                    Color(android.graphics.Color.parseColor(
                                        if (grossist.statueDeBase.couleur.startsWith("#"))
                                            grossist.statueDeBase.couleur
                                        else "#${grossist.statueDeBase.couleur}"
                                    ))
                                } catch (e: Exception) {
                                    Color(0xFFFF0000)
                                }
                            ) {
                                val productCount = viewModel.produitsMainDataBase.count { product ->
                                    product.bonCommendDeCetteCota
                                        ?.grossistInformations?.id == grossist.id
                                }
                                Text(productCount.toString())
                            }
                        }
                    }
                }
            }
        }
    }
}
