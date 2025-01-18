package com.example.Main.AppNavHost

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
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.Packages.App._1_GerantAfficheurGrossistCommend.App.AppNavHost
import com.example.Packages.App._1_GerantAfficheurGrossistCommend.App.NavigationItems
import com.example.c_serveur.AppViewModels

@Composable
fun MainScreen(
    modifier: Modifier,
    appViewModels: AppViewModels,
) {
    val navController = rememberNavController()
    val items = NavigationItems.items  // Updated to use the items property instead of getItems()
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize()) {
                Box(modifier = Modifier.weight(1f)) {
                    AppNavHost(
                        modifier = Modifier.fillMaxSize(),
                        appViewModels = appViewModels,
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
                    initViewModel = appViewModels.initViewModel,
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
