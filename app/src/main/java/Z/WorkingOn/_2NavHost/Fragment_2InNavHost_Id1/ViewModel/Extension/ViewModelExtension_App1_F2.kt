package Z.WorkingOn.ViewModel.Extension

import Z_MasterOfApps.Kotlin.Model.ClientsDataBase
import Z_MasterOfApps.Kotlin.Model._ModelAppsFather.ProduitModel
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import kotlinx.coroutines.CoroutineScope

class ViewModelExtension_App1_F1(
    val viewModel: ViewModelInitApp,
    val produitsMainDataBase: MutableList<ProduitModel>,
    val clientDataBaseSnapList: SnapshotStateList<ClientsDataBase>,
    val viewModelScope: CoroutineScope,
) {
    var produitsAChoisireLeurClient: MutableList<ProduitModel> =
        emptyList<ProduitModel>().toMutableStateList()
}


