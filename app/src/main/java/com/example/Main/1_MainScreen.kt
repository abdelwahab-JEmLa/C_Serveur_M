package com.example.Main

import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import Z_MasterOfApps.Z.Android.Base.NavigationBarWithFabDEV
import Z_MasterOfApps.Z.Android.Base.NavigationItems
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.Packages.Views._1_GerantAfficheurGrossistCommend.App.AppNavHost

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun MainScreen(
    modifier: Modifier,
    permissionsGranted: Boolean,
    viewModelInitApp: ViewModelInitApp = viewModel(),
) {
    val context = LocalContext.current
    val navController = rememberNavController()
    val items = NavigationItems.items
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    // Contenu principal uniquement affiché si les permissions sont accordées
    if (permissionsGranted) {
        Surface(
            modifier = modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Column(modifier = Modifier.fillMaxSize()) {
                    Box(modifier = Modifier.weight(1f)) {
                        AppNavHost(
                            modifier = Modifier.fillMaxSize(),
                            viewModelInitApp = viewModelInitApp,
                            navController = navController,
                        )
                    }
                }

                AnimatedVisibility(
                    visible = true,
                    modifier = Modifier.align(Alignment.BottomCenter)
                ) {
                    NavigationBarWithFabDEV(
                        items = items,
                        viewModelInitApp = viewModelInitApp,
                        currentRoute = currentRoute,
                        onNavigate = { route ->
                            navController.navigate(route) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
            }
        }
    }
}
