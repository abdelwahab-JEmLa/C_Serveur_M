package Z_MasterOfApps.Z_AppsFather.Kotlin._0.Main.ContimentDeveloping.Code.App

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Moving
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.Packages.App._1_GerantAfficheurGrossistCommend.App._1NavHost.Fragment_Id4.A_DeplaceProduitsVerGrossist
import com.example.c_serveur.AppViewModels

@Composable
fun AppNavHostDEV(
    modifier: Modifier = Modifier,
    appViewModels: AppViewModels,
    navController: NavHostController,
) {
    Box(modifier = modifier.fillMaxSize()) {
        NavHost(
            navController = navController,
            startDestination = Screen.Fragment_5.route,
            modifier = Modifier.fillMaxSize()
        ) {
            composable(Screen.Fragment_5.route) {
                A_DeplaceProduitsVerGrossist(viewModelInitApp = appViewModels.initViewModel)
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
    data object Fragment_5 : Screen(
        route = "main_screen_F5",
        icon = Icons.Default.Moving,
        title = "main_screen_F5",
        color = Color(0xFF3F51B5)
    )

}

object NavigationItems {
    val items = listOf(
        Screen.Fragment_5,

        )
}

@Preview
@Composable
private fun Preview_Fragment4() {
    A_DeplaceProduitsVerGrossist(modifier = Modifier.fillMaxSize())
}
