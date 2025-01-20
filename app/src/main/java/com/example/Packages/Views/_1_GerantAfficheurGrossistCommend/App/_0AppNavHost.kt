package com.example.Packages.Views._1_GerantAfficheurGrossistCommend.App

import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Moving
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhonelinkRing
import androidx.compose.material.icons.filled.Tab
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.Packages.Views.Screen
import com.example.Packages.Views._1_GerantAfficheurGrossistCommend.App.Fragment_2InNavHost_Id1.A_GerantDefinirePosition_F1
import com.example.Packages.Views._1_GerantAfficheurGrossistCommend.App.Fragment_3InNavHost_Id2.A_TravaillieurListProduitAchercheChezLeGrossist_F2
import com.example.Packages.Views._1_GerantAfficheurGrossistCommend.App.Fragment_4InNavHost_Id3.A_AfficheurDesProduitsPourLeColecteur_F3
import com.example.Packages.Views._1_GerantAfficheurGrossistCommend.App._1NavHost.Fragment_Id4.A_DeplaceProduitsVerGrossist


fun NavGraphBuilder._1GerantAfficheurGrossistCommendApp(viewModelInitApp: ViewModelInitApp) {
    composable(Screens.MainScreen_F4.route) {
        A_DeplaceProduitsVerGrossist(viewModelInitApp = viewModelInitApp)
    }

    composable(Screens.MainScreen_F1.route) {
        A_GerantDefinirePosition_F1(viewModelInitApp = viewModelInitApp)
    }

    composable(Screens.MainScreen_F2.route) {
        A_TravaillieurListProduitAchercheChezLeGrossist_F2(viewModelInitApp = viewModelInitApp)
    }

    composable(Screens.MainScreen_F3.route) {
        A_AfficheurDesProduitsPourLeColecteur_F3(viewModelInitApp = viewModelInitApp)
    }
}

object Screens {
    val MainScreen_F4 = MainScreenDataObject_F4
    val MainScreen_F1 = MainScreenDataObject_F1
    val MainScreen_F2 = MainScreenDataObject_F2
    val MainScreen_F3 = MainScreenDataObject_F3
}

data object MainScreenDataObject_F4 : Screen(
    route = "main_screen_f4",
    icon = Icons.Default.Moving,
    title = "main_screen_f4",
    color = Color(0xFF3F51B5)
)

data object MainScreenDataObject_F1 : Screen(
    route = "fragment_main_screen_1",
    icon = Icons.Default.Tab,
    title = "Serveur Grossist",
    color = Color(0xFFFF5722)
)

data object MainScreenDataObject_F2 : Screen(
    route = "main_screen_f2",
    icon = Icons.Default.PhonelinkRing,
    title = "Phone Client Grossist",
    color = Color(0xFFFFEB3B)
)

data object MainScreenDataObject_F3 : Screen(
    route = "main_screen_f3",
    icon = Icons.Default.Person,
    title = "Phone Client Client",
    color = Color(0xFFFF5722)
)

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
    A_DeplaceProduitsVerGrossist(modifier = Modifier.fillMaxSize())
}
