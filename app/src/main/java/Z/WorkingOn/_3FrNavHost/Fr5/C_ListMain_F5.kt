package Z.WorkingOn._3FrNavHost.Fr5

import Z.WorkingOn._3FrNavHost.Fr5.Modules.SearchDialog_F1
import Z.WorkingOn._3FrNavHost.Fr5.ViewModel.Extension.ViewModelExtension_App1_F5
import Z_MasterOfApps.Kotlin.Model._ModelAppsFather.Companion.updateProduit
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun C_MainList_F5(
    extensionVM: ViewModelExtension_App1_F5,
    viewModel: ViewModelInitApp,
    paddingValues: PaddingValues,
    modifier: Modifier = Modifier,
) {
    var showSearchDialog by remember { mutableStateOf(false) }

    Column {
        Column() {
            Text(
                "Produits Excluded (${extensionVM.produitsExcluded.size})",
                modifier = Modifier.padding(8.dp),
                style = MaterialTheme.typography.titleMedium
            )
            LazyVerticalGrid(
                columns = GridCells.Fixed(5),
                modifier = modifier
                    .fillMaxWidth()
                    .background(Color(0xE3C85858).copy(alpha = 0.1f), RoundedCornerShape(8.dp)),
                contentPadding = paddingValues,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    items = extensionVM.produitsExcluded,
                ) { product ->
                    D_MainItem_F5(
                        mainItem = product,
                        onCLickOnMain = {},
                        modifier = Modifier.animateItem(fadeInSpec = null, fadeOutSpec = null)
                    )
                }
            }
        }
        LazyVerticalGrid(
            columns = GridCells.Fixed(5),
            modifier = modifier
                .fillMaxWidth()
                .background(Color(0xE3C85858).copy(alpha = 0.1f), RoundedCornerShape(8.dp)),
            contentPadding = paddingValues,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item(span = { GridItemSpan(5) }) {
                Row(
                    modifier = Modifier.padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(onClick = { showSearchDialog = true }) {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = "Rechercher",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    Text(
                        "Produits Verifie (${extensionVM.verifieProduits.size})",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }

            items(
                items = unpositionedProducts.sortedWith(compareBy(
                    { product ->
                        val position =
                            product.bonCommendDeCetteCota?.mutableBasesStates?.positionProduitDonGrossistChoisiPourAcheterCeProduit
                        if (position == null || position == 0) null else position
                    },
                    // Ensuite trier par nom
                    { it.nom.lowercase() }
                )),
            ) { product ->
                D_MainItem_F5(
                    mainItem = product,
                    onCLickOnMain = {
                        val newPosition = (positionedProducts.maxOfOrNull {
                            it.bonCommendDeCetteCota?.mutableBasesStates?.positionProduitDonGrossistChoisiPourAcheterCeProduit
                                ?: 0
                        } ?: 0) + 1

                        product.bonCommendDeCetteCota?.apply {
                            mutableBasesStates?.cPositionCheyCeGrossit = true
                            mutableBasesStates?.positionProduitDonGrossistChoisiPourAcheterCeProduit =
                                newPosition
                        }
                        if (product.itsTempProduit) {
                            product.statuesBase.prePourCameraCapture = true
                        }
                        updateProduit(product, viewModel)
                    },
                    modifier = Modifier.animateItem(fadeInSpec = null, fadeOutSpec = null)
                )
            }
        }
    }



    SearchDialog_F1(
        unpositionedItems = unpositionedProducts,
        viewModelProduits = viewModel,
        showDialog = showSearchDialog,
        onDismiss = { showSearchDialog = false }
    )
}
