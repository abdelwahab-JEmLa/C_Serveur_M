// ViewModelExtension_App1_F5.kt
package Z.WorkingOn._3FrNavHost.Fr5.ViewModel.Extension

import Z_MasterOfApps.Kotlin.Model._ModelAppsFather.ProduitModel
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList

class ViewModelExtension_App1_F5(
    val viewModel: ViewModelInitApp,
    val produitsMainDataBase: MutableList<ProduitModel>,
) {
    var excludedProduits: MutableList<ProduitModel> =
        emptyList<ProduitModel>().toMutableStateList()

    var produitsVerifie: MutableList<ProduitModel> =
        emptyList<ProduitModel>().toMutableStateList()

    var prochenClickIncludeProduit by mutableStateOf<ProduitModel?>(null)
}
