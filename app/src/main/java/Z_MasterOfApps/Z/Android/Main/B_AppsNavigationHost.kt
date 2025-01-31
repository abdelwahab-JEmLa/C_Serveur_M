package Z_MasterOfApps.Z.Android.Main

import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import Z_MasterOfApps.Z.Android.Base.App.App._1.GerantAfficheurGrossistCommend.App.NH_1.id4_DeplaceProduitsVerGrossist.A_id4_DeplaceProduitsVerGrossist
import Z_MasterOfApps.Z.Android.Base.App.App._1.GerantAfficheurGrossistCommend.App.NH_2.id1_GerantDefinirePosition.A_id1_GerantDefinirePosition
import Z_MasterOfApps.Z.Android.Base.App.App._1.GerantAfficheurGrossistCommend.App.NH_3.id2_TravaillieurListProduitAchercheChezLeGrossist.A_Id2_TravaillieurListProduitAchercheChezLeGrossist
import Z_MasterOfApps.Z.Android.Base.App.App._1.GerantAfficheurGrossistCommend.App.NH_4.id3_AfficheurDesProduitsPourLeColecteur.A_id3_AfficheurDesProduitsPourLeColecteur
import Z_MasterOfApps.Z.Android.Base.App.App2_LocationGpsClients.NH_1.id1_ClientsLocationGps.A_id1_ClientsLocationGps
import Z_MasterOfApps.Z.Android.Main.C_EcranDeDepart.Startup.A_StartupScreen
import Z_MasterOfApps.Z.Android.Main.C_EcranDeDepart.Startup.NavigationBarWithFab
import Z_MasterOfApps.Z.Android.Packages._1.GerantAfficheurGrossistCommend.App.NH_5.ID5_VerificationProduitAcGrossist.A_ID5_VerificationProduitAcGrossist
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FactCheck
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PinDrop
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

@Composable
fun AppNavigationHost(
    viewModelInitApp: ViewModelInitApp,
    modifier: Modifier,
) {
    val navController = rememberNavController()

    // Get manager phone status
    val isManagerPhone = viewModelInitApp._paramatersAppsViewModelModel.cLeTelephoneDuGerant ?: false

    // Get navigation items using the proper function
    val items = remember(isManagerPhone) {
        NavigationItems.getItems(isManagerPhone)
    }

    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.weight(1f)) {
                Box(modifier = modifier.fillMaxSize()) {
                    if (viewModelInitApp.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .size(48.dp)
                                .align(Alignment.Center),
                            color = MaterialTheme.colorScheme.primary
                        )
                    } else {
                        NavHost(
                            navController = navController,
                            startDestination = Screens.Startup_0.route,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            composable(Screens.Startup_0.route) {
                                A_StartupScreen(
                                    viewModelInitApp = viewModelInitApp,
                                    onNavigate = { route ->
                                        navController.navigate(route) {
                                            popUpTo(navController.graph.startDestinationId) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                )
                            }
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
                            composable("مظهر الاماكن لمقسم المنتجات على الزبائن") {
                                A_id3_AfficheurDesProduitsPourLeColecteur(viewModelInitApp = viewModelInitApp)
                            }
                            composable(Screens.NavHostA2_1.route) {
                                A_id1_ClientsLocationGps(viewModel = viewModelInitApp)
                            }
                        }
                    }
                }
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

object NavigationItems {
    // Remove @Composable annotation and make it a function that takes the required parameter
    fun getItems(isManagerPhone: Boolean) = buildList {
        add(Screens.Startup_0)
        if (isManagerPhone) { add(Screens.NavHost_1) }
        add(Screens.NavHost_2)
        add(Screens.NavHost_3)
        add(Screens.NavHost_5)
        add(Screens.NavHost_4)
        if (isManagerPhone) { add(Screens.NavHostA2_1) }
    }
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
    val Startup_0 = StartupIcon_Start
    val NavHost_1 = MainScreenDataObject_F4
    val NavHost_2 = MainScreenDataObject_F1
    val NavHost_3 = MainScreenDataObject_F2
    val NavHost_4 = ID4Icon_Done
    val NavHost_5 = MainScreenDataObject_F3
    val NavHostA2_1 = ID1Icon_Person
}

data object StartupIcon_Start : Screen(
    id =7,
    icon = Icons.Default.Home, // Changed from Start to Home for main entry point
    color = Color(0xFFFF5722),
    route = "StartupIcon_Start",
    titleArab = "المدخل الرئيسي"
)

data object MainScreenDataObject_F1 : Screen(
    id =1,

    icon = Icons.Default.LocationOn, // Changed from Tab to LocationOn for location marking
    route = "fragment_main_screen_1",
    titleArab = "محدد اماكن المنتجات عند الجمال",
    color = Color(0xFFFF5722)
)

data object MainScreenDataObject_F2 : Screen(
    id =2,

    icon = Icons.Default.Visibility, // Changed from PhonelinkRing to Visibility for product viewing
    route = "main_screen_f2",
    titleArab = "مظهر اماكن المنتجات عند الجمال",
    color = Color(0xFFFFEB3B)
)

data object MainScreenDataObject_F3 : Screen(
    id =3,

    route ="مظهر الاماكن لمقسم المنتجات على الزبائن",
    icon = Icons.Default.Groups, // Changed from Person to Groups for customer distribution
    titleArab = "مظهر الاماكن لمقسم المنتجات على الزبائن",
    color = Color(0xFFFF5722)
)

data object MainScreenDataObject_F4 : Screen(
    id =4,

    route = "main_screen_f4",
    icon = Icons.Default.LocalShipping, // Changed from Moving to LocalShipping for product distribution
    titleArab = "مقسم المنتجات الى الجمالين",
    color = Color(0xFF3F51B5)
)

data object ID4Icon_Done : Screen(
    id =5,

    icon = Icons.AutoMirrored.Filled.FactCheck, // Changed from Done to FactCheck for invoice verification
    route = "A_ID5_VerificationProduitAcGrossist",
    titleArab = "التاكد من فواتير مع المنتجات عند الجمال",
    color = Color(0xFFFF5892)
)

data object ID1Icon_Person : Screen(
    id =6,

    icon = Icons.Default.PinDrop, // Changed from Person to PinDrop for GPS location
    route = "Id_App2Fragment1",
    titleArab = "محدد اماكن الزبائن GPS",
    color = Color(0xFFFF5892)

)
abstract class Screen(
    val id: Long,
    val route: String,
    val icon: ImageVector,
    val titleArab: String,
    val color: Color
)



