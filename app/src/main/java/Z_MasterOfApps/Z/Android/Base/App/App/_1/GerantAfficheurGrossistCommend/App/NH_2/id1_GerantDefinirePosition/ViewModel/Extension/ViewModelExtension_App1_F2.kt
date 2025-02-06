package Z_MasterOfApps.Z.Android.Base.App.App._1.GerantAfficheurGrossistCommend.App.NH_2.id1_GerantDefinirePosition.ViewModel.Extension

import Z_MasterOfApps.Kotlin.Model.A_ProduitModel
import Z_MasterOfApps.Kotlin.Model.C_GrossistsDataBase.Companion.updateGrossistDataBase
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class Frag2_A1_ExtVM(
    val viewModel: ViewModelInitApp,
    val produitsMainDataBase: MutableList<A_ProduitModel>,
) {
    val produitsAChoisireLeurClient = viewModel
        ._paramatersAppsViewModelModel.produitsAChoisireLeurClient

    private val grossistsDataBase = viewModel._modelAppsFather.grossistsDataBase

    var idAuFilter by mutableStateOf<Long?>(0)

    fun upButton(index: Int) {
        // Ensure index is valid and there's a previous element
        if (index <= 0 || index >= grossistsDataBase.size) {
            return
        }

        val currentElement = grossistsDataBase[index]
        val prev = grossistsDataBase[index - 1]

        // Swap their positions
        val currentPosition = currentElement.statueDeBase.itIndexInParentList
        val prevPosition = prev.statueDeBase.itIndexInParentList

        // Update positions
        currentElement.statueDeBase.itIndexInParentList = prevPosition
        prev.statueDeBase.itIndexInParentList = currentPosition

        // Update the list order
        grossistsDataBase[index] = prev
        grossistsDataBase[index - 1] = currentElement

        // Update all wholesalers to ensure indices are sequential and consistent
        updateAllWholesalerIndices()
    }

    private fun updateAllWholesalerIndices() {
        // Reindex all items sequentially
        grossistsDataBase.forEachIndexed { index, grossist ->
            if (grossist.statueDeBase.itIndexInParentList != index) {
                grossist.statueDeBase.itIndexInParentList = index
                // Update in Firebase
                grossist.updateGrossistDataBase(viewModel)
            }
        }
    }
}
