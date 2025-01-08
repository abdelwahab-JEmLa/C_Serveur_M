package com.example.Main.MainScreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Tab
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.Packages.A_GrosssitsCommendHandler.A1_Fragment.A_ScreenMainFragment_1
import com.example.Packages.A_GrosssitsCommendHandler.A2_Fragment.A_ScreenMainFragment_2
import com.example.c_serveur.AppViewModels
import kotlinx.serialization.Serializable

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

            composable(FragmentMainScreenDestination1().route) {
               A_ScreenMainFragment_1(initViewModel=appViewModels.initViewModel)
            }
            composable(FragmentMainScreenDestination2().route) {
                A_ScreenMainFragment_2(initViewModel=appViewModels.initViewModel)
            }

        }
    }
}
/**
 * Object used for a type safe destination to a Home screen
 */
@Serializable
data class FragmentMainScreenDestination1(val route: String = "FragmentMainScreenDestination1") : java.io.Serializable
@Serializable
data class FragmentMainScreenDestination2(val route: String = "A_ScreenMainFragment_2") : java.io.Serializable

sealed class Screen(
    val route: String,
    val icon: ImageVector,
    val title: String,
    val color: Color
) {
    data object FragmentMainScreen1 : Screen(
        route = "FragmentMainScreenDestination1",
        icon = Icons.Default.Tab,
        title = "FragmentMainScreenDestination1",
        color = Color(0xFFFF5722)
    )
    data object FragmentMainScreen2 : Screen(
        route = "A_ScreenMainFragment_2",
        icon = Icons.Default.People,
        title = "A_ScreenMainFragment_2",
        color = Color(0xFF9C27B0)
    )

}

// Update NavigationItems to include the new screen
object NavigationItems {
    fun getItems() = listOf(
        Screen.FragmentMainScreen1,
        Screen.FragmentMainScreen2,
    )
}




