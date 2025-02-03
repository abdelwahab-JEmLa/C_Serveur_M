package Z_MasterOfApps.Z.Android.Base.App.App._1.GerantAfficheurGrossistCommend.App.NH_1.id4_DeplaceProduitsVerGrossist.Modules.Dialogs

import Z_MasterOfApps.Kotlin.Model._ModelAppsFather
import Z_MasterOfApps.Kotlin.Model._ModelAppsFather.Companion.updateProduit
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun MoveProductsDialog(
    selectedProducts: List<_ModelAppsFather.ProduitModel>,
    viewModelProduits: ViewModelInitApp,
    onDismiss: () -> Unit,
    onProductsMoved: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    "Move ${selectedProducts.size} products to another grossist",
                    style = MaterialTheme.typography.titleMedium
                )

                viewModelProduits._modelAppsFather.grossistsDataBase
                    .filter { it.id != viewModelProduits.frag_4A1_ExtVM.auFilter}
                    .forEach { grossist ->
                        Button(
                            onClick = {
                                selectedProducts.forEach { product ->
                                    product.bonCommendDeCetteCota?.let { bonCommande ->
                                        bonCommande.IdGrossitChoisi = grossist.id
                                        updateProduit(product, viewModelProduits)
                                    }
                                }
                                onDismiss()
                                onProductsMoved()
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(grossist.nom)
                        }
                    }
            }
        }
    }
}
