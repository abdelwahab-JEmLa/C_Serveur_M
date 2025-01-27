package Z.WorkingOn._2NavHost.Fragment_2InNavHost_Id1.ViewModel.Extension

import Z_MasterOfApps.Kotlin.Model._ModelAppsFather.ProduitModel
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import kotlinx.coroutines.CoroutineScope

class ViewModelExtension_App1_F1(
    val viewModel: ViewModelInitApp,
    val produitsMainDataBase: MutableList<ProduitModel>,
    val viewModelScope: CoroutineScope,
) {
    val produitsAChoisireLeurClient = viewModel
        ._paramatersAppsViewModelModel.produitsAChoisireLeurClient
}


