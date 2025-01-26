package com.example.Main

import Z.WorkingOn._2NavHost.Fragment_2InNavHost_Id1.A_GerantDefinirePosition_F1
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost

@Composable
fun ParentAppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModelInitApp: ViewModelInitApp,
) {
    // Show loading indicator while initializing
    if (viewModelInitApp.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(48.dp),
                color = MaterialTheme.colorScheme.primary
            )
        }
        return
    }

    Box(modifier = modifier.fillMaxSize()) {
        NavHost(
            navController = navController,
            startDestination = Screens.MainScreen_F1.route,
            modifier = Modifier.fillMaxSize()
        ) {
            _1GerantAfficheurGrossistCommendApp(viewModelInitApp)
            //  _2App(viewModelInitApp)
        }
    }
}

object NavigationItems {
    val items = listOf(
        Screens.MainScreen_F4,
        Screens.MainScreen_F1,
        Screens.MainScreen_F2,
        Screens.MainScreen_F3,
        ScreensApp2.Fragment1Screen
    )
}

abstract class Screen(
    val route: String,
    val icon: ImageVector,
    val title: String,
    val color: Color
)


@Preview
@Composable
private fun Preview_Fragment() {
    A_GerantDefinirePosition_F1(modifier = Modifier.fillMaxSize())
}
