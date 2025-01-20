package com.example.Packages.Views

import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.example.Packages.Views._1_GerantAfficheurGrossistCommend.App.Screens
import com.example.Packages.Views._1_GerantAfficheurGrossistCommend.App._1GerantAfficheurGrossistCommendApp
import com.example.Packages.Views._2LocationGpsClients.App.ScreensApp2
import com.example.Packages.Views._2LocationGpsClients.App._2App

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModelInitApp: ViewModelInitApp,
) {
    Box(modifier = modifier.fillMaxSize()) {
        NavHost(
            navController = navController,
            startDestination = Screens.MainScreen_F4.route,
            modifier = Modifier.fillMaxSize()
        ) {
            _1GerantAfficheurGrossistCommendApp(viewModelInitApp)
            _2App(viewModelInitApp)
        }
    }
}

abstract class Screen(
    val route: String,
    val icon: ImageVector,
    val title: String,
    val color: Color
)

object NavigationItems {
    val items = listOf(
        ScreensApp2,
        Screens,
    )
}
