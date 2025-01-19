package com.example.Main

import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import Z_MasterOfApps.Z.Android.Base.NavigationBarWithFabDEV
import Z_MasterOfApps.Z.Android.Base.NavigationItems
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.Packages.App._1_GerantAfficheurGrossistCommend.App.AppNavHost
import com.example.c_serveur.Modules.PermissionHandler

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun MainScreen(
    modifier: Modifier,
    viewModelInitApp: ViewModelInitApp = viewModel(),
) {
    val context = LocalContext.current
    val activity = context as ComponentActivity
    val navController = rememberNavController()
    val items = NavigationItems.items
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    //
    // État pour gérer si les permissions sont accordées
    var permissionsGranted by remember { mutableStateOf(false) }

    // Créer le gestionnaire de permissions
    val permissionHandler = remember { PermissionHandler(activity) }

    // Effet pour vérifier les permissions au démarrage
    LaunchedEffect(Unit) {
        permissionHandler.checkAndRequestPermissions(object :
            PermissionHandler.PermissionCallback {
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
                        AppNavHost(
                            modifier = Modifier.fillMaxSize(),
                            viewModelInitApp = viewModelInitApp,
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
    }
}
