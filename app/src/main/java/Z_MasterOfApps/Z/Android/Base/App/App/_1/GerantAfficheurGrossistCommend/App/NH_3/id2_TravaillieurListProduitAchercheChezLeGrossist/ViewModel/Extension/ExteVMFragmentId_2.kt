package Z_MasterOfApps.Z.Android.Base.App.App._1.GerantAfficheurGrossistCommend.App.NH_3.id2_TravaillieurListProduitAchercheChezLeGrossist.ViewModel.Extension

import Z_MasterOfApps.Kotlin.Model.C_GrossistsDataBase.Companion.updateGrossistDataBase
import Z_MasterOfApps.Kotlin.Model._ModelAppsFather.Companion.updateProduit
import Z_MasterOfApps.Kotlin.Model._ModelAppsFather.ProduitModel
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope

class ExteVMFragmentId_2(
    val viewModelInitApp: ViewModelInitApp,
    val produitsMainDataBase: MutableList<ProduitModel>,
    val viewModelScope: CoroutineScope,
) {
    private val grossistsDataBase = viewModelInitApp._modelAppsFather.grossistsDataBase

    var afficheProduitsPourRegleConflites by mutableStateOf(false)
    var auFilter by mutableStateOf<Long?>(0)

    fun upButton(index: Int) {
        // Ensure index is valid and there's a previous element
        if (index <= 0 || index >= grossistsDataBase.size) {
            return
        }

        val currentElement = grossistsDataBase[index]
        val prev = grossistsDataBase[index - 1]

        // Swap their positions
        val currentPosition = currentElement.statueDeBase.itPositionInList
        val prevPosition = prev.statueDeBase.itPositionInList

        // Update positions
        currentElement.statueDeBase.itPositionInList = prevPosition
        prev.statueDeBase.itPositionInList = currentPosition

        // Update the list order
        grossistsDataBase[index] = prev
        grossistsDataBase[index - 1] = currentElement

        // Update both clients in the database
        currentElement.updateGrossistDataBase(viewModelInitApp)
        prev.updateGrossistDataBase(viewModelInitApp)
    }

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
        updateProduit(updatedProduit,viewModelInitApp)
    }

}


