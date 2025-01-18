package com.example.Packages.App._1_GerantAfficheurGrossistCommend.F1_0

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Moving
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhonelinkRing
import androidx.compose.material.icons.filled.Tab
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.Packages.App._1_GerantAfficheurGrossistCommend.F1_0._1NavHost_Id4_Fragment.A_DeplaceProduitsVerGrossist_F1_Decal
import com.example.Packages.App._1_GerantAfficheurGrossistCommend.F1_0.Fragment_1.A_GerantDefinirePosition_F1
import com.example.Packages.App._1_GerantAfficheurGrossistCommend.F1_0.Fragment_2.A_TravaillieurListProduitAchercheChezLeGrossist_F2
import com.example.Packages.App._1_GerantAfficheurGrossistCommend.F1_0.Fragment_3.A_AfficheurDesProduitsPourLeColecteur_F3
import com.example.c_serveur.AppViewModels

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    appViewModels: AppViewModels,
    navController: NavHostController,
) {
    Box(modifier = modifier.fillMaxSize()) {
        NavHost(
            navController = navController,
            startDestination = Screen.MainScreen_F4.route,
            modifier = Modifier.fillMaxSize()
        ) {
            composable(Screen.MainScreen_F4.route) {
                A_DeplaceProduitsVerGrossist_F1_Decal(viewModelInitApp = appViewModels.initViewModel)
            }

            composable(Screen.MainScreen_F1.route) {
                A_GerantDefinirePosition_F1(viewModelInitApp = appViewModels.initViewModel)
            }

            composable(Screen.MainScreen_F2.route) {
                A_TravaillieurListProduitAchercheChezLeGrossist_F2(viewModelInitApp = appViewModels.initViewModel)
            }

            composable(Screen.MainScreen_F3.route) {
                A_AfficheurDesProduitsPourLeColecteur_F3(viewModelInitApp = appViewModels.initViewModel)
            }

        }
    }
}

sealed class Screen(
    val route: String,
    val icon: ImageVector,
    val title: String,
    val color: Color
) {
    data object MainScreen_F4 : Screen(
        route = "main_screen_f4",
        icon = Icons.Default.Moving,
        title = "main_screen_f4",
        color = Color(0xFF3F51B5)
    )

    data object MainScreen_F1 : Screen(
        route = "fragment_main_screen_1",
        icon = Icons.Default.Tab,
        title = "Serveur Grossist",
        color = Color(0xFFFF5722)
    )

    data object MainScreen_F2 : Screen(
        route = "main_screen_f2",
        icon = Icons.Default.PhonelinkRing,
        title = "Phone Client Grossist",
        color = Color(0xFFFFEB3B)
    )

    data object MainScreen_F3 : Screen(
        route = "main_screen_f3",
        icon = Icons.Default.Person,
        title = "Phone Client Client",
        color = Color(0xFFFF5722)
    )

}

object NavigationItems {
    val items = listOf(
        Screen.MainScreen_F4,
        Screen.MainScreen_F1,
        Screen.MainScreen_F2,
        Screen.MainScreen_F3,
    )
}
@Preview
@Composable
private fun Preview_Fragment1() {
    A_GerantDefinirePosition_F1(modifier = Modifier.fillMaxSize())
}

@Preview
@Composable
private fun Preview_Fragment2() {
    A_TravaillieurListProduitAchercheChezLeGrossist_F2(modifier = Modifier.fillMaxSize())
}

@Preview
@Composable
private fun Preview_Fragment3() {
    A_AfficheurDesProduitsPourLeColecteur_F3(modifier = Modifier.fillMaxSize())
}

@Preview
@Composable
private fun Preview_Fragment4() {
    A_DeplaceProduitsVerGrossist_F1_Decal(modifier = Modifier.fillMaxSize())
}
