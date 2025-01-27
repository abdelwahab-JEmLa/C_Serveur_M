package Z.WorkingOn._2NavHost.Fragment_2

import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel

private const val TAG = "A_GerantDefinirePosition_F1"

@Composable
internal fun A_TravaillieurListProduitAchercheChezLeGrossist_F2(
    modifier: Modifier = Modifier,
    viewModelInitApp: ViewModelInitApp = viewModel(),
    ) {
    // Log state changes using LaunchedEffect
    LaunchedEffect(viewModelInitApp.isLoading, viewModelInitApp.loadingProgress) {
        logLoadingState(viewModelInitApp.isLoading, viewModelInitApp.loadingProgress)
    }

    if (viewModelInitApp.isLoading) {
        Box(modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator(
                progress = {
                    viewModelInitApp.loadingProgress
                },
                modifier = Modifier.align(Alignment.Center),
                trackColor = ProgressIndicatorDefaults.circularIndeterminateTrackColor,
            )
        }
        return
    }

    val databaseSize = viewModelInitApp._modelAppsFather.produitsMainDataBase.size

    val visibleProducts = viewModelInitApp._modelAppsFather.produitsMainDataBase
        .filter { product ->
            product.bonCommendDeCetteCota.let {
                it?.grossistInformations?.id ==
                        viewModelInitApp
                            ._paramatersAppsViewModelModel
                            .telephoneClientParamaters
                            .selectedGrossistForClientF2
                        && it
                    ?.mutableBasesStates
                    ?.positionProduitDonGrossistChoisiPourAcheterCeProduit!! > 0
            }
        }

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            if (databaseSize > 0) {
                MainList_F2(
                    initVisibleProducts = visibleProducts,
                    viewModelInitApp = viewModelInitApp,
                    paddingValues = paddingValues
                )
            }
        }
        if (viewModelInitApp
                ._paramatersAppsViewModelModel
                .fabsVisibility
        ) {
            MainScreenFilterFAB_F2(
                viewModelInitApp = viewModelInitApp,
            )
        }
    }
}

private fun logLoadingState(isLoading: Boolean, progress: Float) {
    Log.d(TAG, "Loading State: isLoading=$isLoading, progress=${progress * 100}%")
}
