package com.example.Main.C_EcranDeDepart.Startup

import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import Z_MasterOfApps.Z_AppsFather.Kotlin._4.Modules.GlideDisplayImageBykeyId
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.Main.C_EcranDeDepart.Startup.B.Dialogs.A_OptionsControlsButtons

private const val TAG = "A_id1_GerantDefinirePosition"

@Composable
internal fun A_PremierScreenApp(
    viewModelInitApp: ViewModelInitApp = viewModel(),
    modifier: Modifier = Modifier,
) {

    Box(modifier = modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize()
        ) { paddingValues ->
            Box(modifier = Modifier.fillMaxSize()) {
                GlideDisplayImageBykeyId(
                    imageGlidReloadTigger = 0,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    size = 100.dp,
                )
            }
            if (viewModelInitApp
                    ._paramatersAppsViewModelModel
                    .fabsVisibility
            ) {
                A_OptionsControlsButtons(
                    viewModelInitApp=viewModelInitApp,
                    paddingValues=paddingValues,
                )
            }
        }
    }
}

