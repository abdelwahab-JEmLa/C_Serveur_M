package com.example.Main.MainScreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreditScore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Tab
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.Main.StartFragment.StartFragment
import com.example.Main.StartFragment.StartFragmentDestination
import com.example.Packages.Z.Archives.P1.ClientProductsDisplayerStatsDestination
import com.example.Packages.Z.Archives.P1.ClientProductsDisplayerStatsFragment
import com.example.Packages._1.Fragment.UI.Fragment3_Main_Screen
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
            startDestination = Screen.Fragment3_Main_Screen.route,
            modifier = Modifier.fillMaxSize()
        ) {

            composable(StartFragmentDestination().route) {
                StartFragment(appViewModels.startFragmentViewModel)
            }
            composable(ClientProductsDisplayerStatsDestination().route) {
                ClientProductsDisplayerStatsFragment(appViewModels.clientProductsDisplayerStatsViewModel)
            }
            composable(Fragment3_Main_ScreenDestination().route) {
                Fragment3_Main_Screen(app_Initialize_ViewModel=appViewModels.app_Initialize_ViewModel)
            }
            composable(Fragment_4_Main_Screen_Destination().route) {
            }
        }
    }
}
/**
 * Object used for a type safe destination to a Home screen
 */
@Serializable
data class Fragment3_Main_ScreenDestination(val route: String = "Fragment3_Main_Screen") : java.io.Serializable

@Serializable
data class Fragment_4_Main_Screen_Destination(val route: String = "Fragment_4_Main_Screen") : java.io.Serializable

sealed class Screen(
    val route: String,
    val icon: ImageVector,
    val title: String,
    val color: Color
) {
    data object StartFragment : Screen(
        route = "startFragment",
        icon = Icons.Default.Home,
        title = "start Fragment",
        color = Color(0xFF2196F3)
    )
    data object ClientProductsDisplayerStatsFragment : Screen(
        route = "ClientProductsDisplayerStatsFragment",
        icon = Icons.Default.CreditScore,
        title = "ClientProductsDisplayerStatsFragment",
        color = Color(0xFF2196F3)
    )
    data object Fragment3_Main_Screen : Screen(
        route = "Fragment3_Main_Screen",
        icon = Icons.Default.Tab,
        title = "Fragment3_Main_Screen",
        color = Color(0xFFFF5722)
    )
    data object Fragment_4_Main_Screen : Screen(
        route = "Fragment_4_Main_Screen",
        icon = Icons.Default.Tab,
        title = "Fragment_4_Main_Screen",
        color = Color(0xFFFF5722)
    )
}

// Update NavigationItems to include the new screen
object NavigationItems {
    fun getItems() = listOf(
        Screen.StartFragment ,
        Screen.ClientProductsDisplayerStatsFragment,
        Screen.Fragment3_Main_Screen,
        Screen.Fragment_4_Main_Screen
    )
}




