package Z.WorkingOn._3FrNavHost.Fr5.ViewModel.Extension

import Z_MasterOfApps.Kotlin.Model._ModelAppsFather.ProduitModel
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class ViewModelExtension_App1_F5(
    val viewModel: ViewModelInitApp,
    val produitsMainDataBase: MutableList<ProduitModel>,
) {

    var selectedGrossistId by mutableStateOf<Long?>(0)

}


