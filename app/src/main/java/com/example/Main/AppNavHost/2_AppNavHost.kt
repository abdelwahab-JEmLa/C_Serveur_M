package com.example.Main.AppNavHost

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Tab
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.Packages.F1_ServeurGrossistCommendFragment.A_ScreenMainFragment_1
import com.example.Packages.F2_PhoneClientGrossistCommend.MainScreen_F2
import com.example.Packages.Z__F3_PhoneClientClient.MainScreen_F3
import com.example.c_serveur.AppViewModels

sealed class Screen(
    val route: String,
    val icon: ImageVector,
    val title: String,
    val color: Color
) {
    data object FragmentMainScreen1 : Screen(
        route = "fragment_main_screen_1",
        icon = Icons.Default.Tab,
        title = "Serveur Grossist",
        color = Color(0xFFFF5722)
    )

    data object MainScreen_F2 : Screen(
        route = "main_screen_f2",
        icon = Icons.Default.Tab,
        title = "Phone Client Grossist",
        color = Color(0xFFFF5722)
    )

    data object MainScreen_F3 : Screen(
        route = "main_screen_f3",
        icon = Icons.Default.Tab,
        title = "Phone Client Client",
        color = Color(0xFFFF5722)
    )
}

object NavigationItems {
    val items = listOf(
        Screen.FragmentMainScreen1,
        Screen.MainScreen_F2,
        Screen.MainScreen_F3,
    )
}

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    appViewModels: AppViewModels,
    navController: NavHostController,
) {
    Box(modifier = modifier.fillMaxSize()) {
        NavHost(
            navController = navController,
            startDestination = Screen.FragmentMainScreen1.route,
            modifier = Modifier.fillMaxSize()
        ) {
            composable(Screen.FragmentMainScreen1.route) {
                A_ScreenMainFragment_1(viewModelInitApp = appViewModels.initViewModel)
            }

            composable(Screen.MainScreen_F2.route) {
                MainScreen_F2(viewModelInitApp = appViewModels.initViewModel)
            }

            composable(Screen.MainScreen_F3.route) {
                MainScreen_F3(viewModelInitApp = appViewModels.initViewModel)
            }
        }
    }
}

@Preview
@Composable
private fun PreviewScreenMainFragment_1() {
    A_ScreenMainFragment_1(modifier = Modifier.fillMaxSize())
}

@Preview
@Composable
private fun PreviewMainScreen_F2() {
    MainScreen_F2(modifier = Modifier.fillMaxSize())
}

@Preview
@Composable
private fun PreviewMainScreen_F3() {
    MainScreen_F3(modifier = Modifier.fillMaxSize())
}
