package com.example.Main

import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.Main.Utils.NavigationBarWithFab

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun MainScreen(
    modifier: Modifier,
    permissionsGranted: Boolean,
    viewModelInitApp: ViewModelInitApp = viewModel(),
) {
    val navController = rememberNavController()
    val items = NavigationItems.items
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    if (permissionsGranted) {
        Surface(
            modifier = modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Column(modifier = Modifier.fillMaxSize()) {
                    Box(modifier = Modifier.weight(1f)) {
                        ParentAppNavHost(
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
                    NavigationBarWithFab(
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
