package com.example.Main

import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun MainScreen(
    modifier: Modifier,
    permissionsGranted: Boolean,
    viewModelInitApp: ViewModelInitApp = viewModel(),
    xmlResources: List<Pair<String, Int>>,
) {
    if (permissionsGranted) {
        Surface(
            modifier = modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            AppNavigationHost(viewModelInitApp,modifier, xmlResources)
        }
    }
}
