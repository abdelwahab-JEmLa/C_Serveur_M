package Z_MasterOfApps.Z_AppsFather.Kotlin._0.Main.ContimentDeveloping.Code

import Z_MasterOfApps.Z_AppsFather.Kotlin._0.Main.ContimentDeveloping.Code.App.AppNavHostDEV
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.Packages.App._1_GerantAfficheurGrossistCommend.App.NavigationItems
import com.example.c_serveur.AppViewModels

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun MainScreenDEV(
    modifier: Modifier,
    appViewModels: AppViewModels,
) {
    val context = LocalContext.current
    val activity = context as ComponentActivity
    val navController = rememberNavController()
    val items = NavigationItems.items
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    // État pour gérer si les permissions sont accordées
    var permissionsGranted by remember { mutableStateOf(false) }

    // Créer le gestionnaire de permissions
    val permissionHandler = remember { PermissionHandlerDEV(activity) }

    // Effet pour vérifier les permissions au démarrage
    LaunchedEffect(Unit) {
        permissionHandler.checkAndRequestPermissions(object : PermissionHandlerDEV.PermissionCallback {
            override fun onPermissionsGranted() {
                permissionsGranted = true
            }

            override fun onPermissionsDenied() {
                activity.finish()
            }

            override fun onPermissionRationale(permissions: Array<String>) {
                // Gérer le cas où l'utilisateur a besoin d'une explication
            }
        })
    }

    // Contenu principal uniquement affiché si les permissions sont accordées
    if (permissionsGranted) {
        Surface(
            modifier = modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Column(modifier = Modifier.fillMaxSize()) {
                    Box(modifier = Modifier.weight(1f)) {
                        AppNavHostDEV(
                            modifier = Modifier.fillMaxSize(),
                            appViewModels = appViewModels,
                            navController = navController,
                        )
                    }
                }

                AnimatedVisibility(
                    visible = true,
                    modifier = Modifier.align(Alignment.BottomCenter)
                ) {
                    NavigationBarWithFabDEV(
                        items = items,
                        initViewModel = appViewModels.initViewModel,
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
    }
}
