package com.example.Main

import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Moving
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhonelinkRing
import androidx.compose.material.icons.filled.Tab
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.Packages.Views._1_GerantAfficheurGrossistCommend.App.NH_1.id4_DeplaceProduitsVerGrossist.A_id4_DeplaceProduitsVerGrossist
import com.example.Packages.Views._1_GerantAfficheurGrossistCommend.App.NH_2.id1_GerantDefinirePosition.A_id1_GerantDefinirePosition
import com.example.Packages.Views._1_GerantAfficheurGrossistCommend.App.NH_3.id2_TravaillieurListProduitAchercheChezLeGrossist.A_Id2_TravaillieurListProduitAchercheChezLeGrossist
import com.example.Packages.Views._1_GerantAfficheurGrossistCommend.App.NH_4.id3_AfficheurDesProduitsPourLeColecteur.A_id3_AfficheurDesProduitsPourLeColecteur
import com.example.Packages.Views._1_GerantAfficheurGrossistCommend.App.NH_5.ID5_VerificationProduitAcGrossist.A_ID5_VerificationProduitAcGrossist
import com.example.Packages.Views._2LocationGpsClients.App.NH_1.id1_ClientsLocationGps.A_id1_ClientsLocationGps

@Composable
fun ParentAppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModelInitApp: ViewModelInitApp,
) {
    Box(modifier = modifier.fillMaxSize()) {
        if (viewModelInitApp.isLoading) {
            // Loading indicator centered in the box
            CircularProgressIndicator(
                modifier = Modifier
                    .size(48.dp)
                    .align(Alignment.Center),
                color = MaterialTheme.colorScheme.primary
            )
        } else {
            NavHost(
                navController = navController,
                startDestination = Screens.NavHost_1.route,
                modifier = Modifier.fillMaxSize()
            ) {
                composable(Screens.NavHost_1.route) {
                    A_id4_DeplaceProduitsVerGrossist(viewModelInitApp = viewModelInitApp)
                }
                composable(Screens.NavHost_2.route) {
                    A_id1_GerantDefinirePosition(viewModelInitApp = viewModelInitApp)
                }
                composable(Screens.NavHost_3.route) {
                    A_Id2_TravaillieurListProduitAchercheChezLeGrossist(viewModelInitApp)
                }
                composable(Screens.NavHost_4.route) {
                    A_ID5_VerificationProduitAcGrossist(viewModelInitApp)
                }
                composable(Screens.NavHost_5.route) {
                    A_id3_AfficheurDesProduitsPourLeColecteur(viewModelInitApp = viewModelInitApp)
                }
                composable(Screens.NavHostA2_1.route) {
                    A_id1_ClientsLocationGps(viewModel = viewModelInitApp)
                }
            }
        }
    }
}

object NavigationItems {
    val items = listOf(
        Screens.NavHost_1,
        Screens.NavHost_2,
        Screens.NavHost_3,
        Screens.NavHost_5,
        Screens.NavHost_4,

        Screens.NavHostA2_1
    )
}

@Preview
@Composable
private fun Preview_Fragment() {
    val viewModelInitApp: ViewModelInitApp = viewModel()
    Box(modifier = Modifier.fillMaxSize()) {
        if (viewModelInitApp.isLoading) {
            // Loading indicator centered in the box
            CircularProgressIndicator(
                modifier = Modifier
                    .size(48.dp)
                    .align(Alignment.Center),
                color = MaterialTheme.colorScheme.primary
            )
        } else {
               A_id1_GerantDefinirePosition(viewModelInitApp = viewModelInitApp)
        }
    }
}

object Screens {
    val NavHost_1 = MainScreenDataObject_F4
    val NavHost_2 = MainScreenDataObject_F1
    val NavHost_3 = MainScreenDataObject_F2
    val NavHost_5 = MainScreenDataObject_F3
    val NavHost_4 = ID4Icon_Done
    val NavHostA2_1 = IconNavApp2_F1
}

abstract class Screen(
    val route: String,
    val icon: ImageVector,
    val title: String,
    val color: Color
)

data object MainScreenDataObject_F1 : Screen(
    icon = Icons.Default.Tab,
    route = "fragment_main_screen_1",
    title = "Serveur Grossist",
    color = Color(0xFFFF5722)
)

data object MainScreenDataObject_F2 : Screen(
    icon = Icons.Default.PhonelinkRing,
    route = "main_screen_f2",
    title = "Phone Client Grossist",
    color = Color(0xFFFFEB3B)
)

data object MainScreenDataObject_F3 : Screen(
    route = "main_screen_f3",
    icon = Icons.Default.Person,
    title = "Phone Client Client",
    color = Color(0xFFFF5722)
)

data object MainScreenDataObject_F4 : Screen(
    route = "main_screen_f4",
    icon = Icons.Default.Moving,
    title = "main_screen_f4",
    color = Color(0xFF3F51B5)
)

data object ID4Icon_Done : Screen(
    route = "A_ID5_VerificationProduitAcGrossist",
    icon = Icons.Default.Done,
    title = "A_ID5_VerificationProduitAcGrossist",
    color = Color(0xFFFF5892)
)


data object IconNavApp2_F1 : Screen(
    route = "Fragment1",
    icon = Icons.Default.Person,
    title = "A_id1_ClientsLocationGps",
    color = Color(0xFF03A9F4)
)


