package Z.WorkingOn.Fragment_2.ViewModel.Extension

import Z_MasterOfApps.Kotlin.Model._ModelAppsFather.Companion.updateProduit
import Z_MasterOfApps.Kotlin.Model._ModelAppsFather.ProduitModel
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import androidx.compose.runtime.toMutableStateList
import kotlinx.coroutines.CoroutineScope

class ViewModelExtension_App1_F2(
    val viewModel: ViewModelInitApp,
    val produitsMainDataBase: MutableList<ProduitModel>,
    val viewModelScope: CoroutineScope,
) {
    var produitsAChoisireLeurClient: MutableList<ProduitModel> =
        emptyList<ProduitModel>().toMutableStateList()

    fun changeColours_AcheteQuantity_Achete(
        selectedBonVent: ProduitModel.ClientBonVentModel?,
        produit: ProduitModel,
        color: ProduitModel.ClientBonVentModel.ColorAchatModel,
        newQuantity: Int
    ) {
        val updatedProduit = produit.apply {
            bonsVentDeCetteCota.find { it==selectedBonVent }
                ?.let { bonVent ->
                bonVent.colours_Achete.find { it == color }
                    ?.quantity_Achete = newQuantity
            }
        }
        updateProduit(updatedProduit,viewModel)
    }

}


