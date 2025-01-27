package Z.WorkingOn._3FrNavHost.Fr5

import Z.WorkingOn._1ItNavHost.F1_GerantDefinirePosition.Modules.ClientEditePositionDialog
import Z.WorkingOn._3FrNavHost.Fr5.ViewModel.Extension.ViewModelExtension_App1_F5
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel

private const val TAG = "A_GerantDefinirePosition_F1"

@Composable
internal fun A_VerificationProduitAcGrossist_F5(
    viewModelInitApp: ViewModelInitApp = viewModel(),
    modifier: Modifier = Modifier,
) {
    val extensionVM = ViewModelExtension_App1_F5(viewModelInitApp,viewModelInitApp.produitsMainDataBase,)

    val databaseSize = viewModelInitApp._modelAppsFather.produitsMainDataBase.size

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize()
        ) { paddingValues ->
            Box(modifier = Modifier.fillMaxSize()) {
                if (databaseSize > 0) {
                    C_MainList_F5(
                        extensionVM=extensionVM,
                        viewModel = viewModelInitApp,
                        paddingValues = paddingValues
                    )
                }

                if (viewModelInitApp._paramatersAppsViewModelModel.fabsVisibility) {
                    B_MainScreenFilterFAB_F5(
                        extensionVM=extensionVM,
                        viewModelProduits = viewModelInitApp,
                    )
                }
            }

            ClientEditePositionDialog(
                viewModelProduits = viewModelInitApp,
            )
        }
    }
}

