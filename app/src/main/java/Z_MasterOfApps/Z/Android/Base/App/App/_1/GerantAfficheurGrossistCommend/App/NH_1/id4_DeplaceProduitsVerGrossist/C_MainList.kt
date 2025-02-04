package Z_MasterOfApps.Z.Android.Base.App.App._1.GerantAfficheurGrossistCommend.App.NH_1.id4_DeplaceProduitsVerGrossist

import Z_MasterOfApps.Kotlin.Model._ModelAppsFather
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import Z_MasterOfApps.Z.Android.Base.App.App._1.GerantAfficheurGrossistCommend.App.NH_1.id4_DeplaceProduitsVerGrossist.Modules.Dialogs.MoveProductsDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun MainList_F4(
    viewModel: ViewModelInitApp,
    paddingValues: PaddingValues,
    modifier: Modifier = Modifier,
) {
    var selectedProducts by remember { mutableStateOf<List<_ModelAppsFather.ProduitModel>>(emptyList()) }
    var deplaceProduitsAuGrosssist by remember { mutableStateOf<Long?>(null) }
    var showMoveDialog by remember { mutableStateOf(false) }

    val groupedProductsParGrossist = remember(
        viewModel._modelAppsFather.produitsMainDataBase,
        viewModel.frag_4A1_ExtVM.updateTrigger
    ) {
        viewModel._modelAppsFather.grossistsDataBase.map { grossist ->
            val matchingProducts = viewModel._modelAppsFather.produitsMainDataBase.filter { product ->
                product.bonCommendDeCetteCota?.idGrossistChoisi == grossist.id
            }
            grossist to matchingProducts
        }.sortedBy { (grossist, _) ->
            grossist.statueDeBase.itPositionInParentList
        }
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = modifier.background(
            Color(0xE3C85858).copy(alpha = 0.1f),
            RoundedCornerShape(8.dp)
        ),
        contentPadding = paddingValues,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        groupedProductsParGrossist.forEach { (grossist, products) ->
            item(
                span = { GridItemSpan(3) }
            ) {
                GrossistHeader(
                    grossist = grossist,
                    selectedProductsCount = selectedProducts.size,
                    onMoveClick = {
                        deplaceProduitsAuGrosssist = grossist.id
                        showMoveDialog = true
                    }
                )
            }

            items(
                items = products.sortedBy {
                    it.bonCommendDeCetteCota?.mutableBasesStates
                        ?.positionProduitDonGrossistChoisiPourAcheterCeProduit
                        ?: Int.MAX_VALUE
                },
                key = { it.id }
            ) { product ->
                Box(
                    modifier = Modifier
                        .animateItem(fadeInSpec = null, fadeOutSpec = null)
                        .padding(4.dp)
                        .background(
                            color = if (selectedProducts.contains(product))
                                Color.Yellow.copy(alpha = 0.3f)
                            else Color.Transparent,
                            shape = RoundedCornerShape(4.dp)
                        )
                ) {
                    MainItem_F4(
                        mainItem = product,
                        onCLickOnMain = {
                            selectedProducts = if (selectedProducts.contains(product)) {
                                selectedProducts - product
                            } else {
                                selectedProducts + product
                            }
                        },
                        position = selectedProducts.indexOf(product).let {
                            if (it >= 0) it + 1 else null
                        },
                        modifier = Modifier.animateItem(fadeInSpec = null, fadeOutSpec = null)
                    )
                }
            }
        }
    }

    if (showMoveDialog && deplaceProduitsAuGrosssist != null) {
        MoveProductsDialog(
            selectedProducts = selectedProducts,
            viewModel = viewModel,
            onDismiss = { showMoveDialog = false },
            onProductsMoved = {
                selectedProducts = emptyList()
                viewModel.frag_4A1_ExtVM.updateTriggerFun()
            }
        )
    }
}
