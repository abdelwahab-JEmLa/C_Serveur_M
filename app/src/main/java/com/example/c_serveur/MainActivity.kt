package com.example.c_serveur

import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import com.example.Main.MainScreen.MainScreen
import com.example.Main.StartFragment.StartFragmentViewModel
import com.example.Packages.P1.ClientProductsDisplayerStatsViewModel
import com.example.Packages.P3.E.ViewModel.ViewModelFragment
import com.example.Apps_Produits_Main_DataBase._2.ViewModel.Apps_Produits_Main_DataBase_ViewModel
import com.example.c_serveur.ui.theme.B_ServeurTheme
import com.example.clientjetpack.Modules.AppDatabase
import com.example.clientjetpack.Modules.PermissionHandler
import com.example.clientjetpack.ViewModel.InitializeViewModel

data class AppViewModels(
    val initializeViewModel: InitializeViewModel,
    val startFragmentViewModel: StartFragmentViewModel,
    val clientProductsDisplayerStatsViewModel: ClientProductsDisplayerStatsViewModel,
    val grossistProductsDiviseurViewModelsFragment: ViewModelFragment,
    val app_Initialize_ViewModel: Apps_Produits_Main_DataBase_ViewModel,
    )

// ViewModelFactory.kt
class ViewModelFactory(
    private val context: Context,
    private val database: AppDatabase,
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(InitializeViewModel::class.java) ->
                InitializeViewModel(
                    context.applicationContext,
                    database,
                ) as T
            modelClass.isAssignableFrom(StartFragmentViewModel::class.java) ->
                StartFragmentViewModel(
                    context.applicationContext,
                    database,
                ) as T
            modelClass.isAssignableFrom(ClientProductsDisplayerStatsViewModel::class.java) ->
                ClientProductsDisplayerStatsViewModel(
                    context.applicationContext,
                    database,
                ) as T
            modelClass.isAssignableFrom(ViewModelFragment::class.java) ->
                ViewModelFragment(
                    context.applicationContext,
                    database,
                ) as T
            modelClass.isAssignableFrom(Apps_Produits_Main_DataBase_ViewModel::class.java) ->
                Apps_Produits_Main_DataBase_ViewModel() as T
            else -> throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
        }
    }
}

class MainActivity : ComponentActivity() {
    private val database by lazy {
        AppDatabase.DatabaseModule.getDatabase(applicationContext)
    }
    private val permissionHandler by lazy { PermissionHandler(this) }
    private val viewModelFactory by lazy { ViewModelFactory(applicationContext, database) }
    private val initializeViewModel: InitializeViewModel by viewModels { viewModelFactory }
    private val startFragmentViewModel: StartFragmentViewModel by viewModels { viewModelFactory }
    private val clientProductsDisplayerStatsViewModel: ClientProductsDisplayerStatsViewModel by viewModels { viewModelFactory }
    private val grossistProductsDiviseurViewModelsFragment: ViewModelFragment by viewModels { viewModelFactory }
    private val app_Initialize_ViewModel: Apps_Produits_Main_DataBase_ViewModel by viewModels { viewModelFactory }



    private val appViewModels by lazy {
        AppViewModels(
            initializeViewModel = initializeViewModel,
            startFragmentViewModel = startFragmentViewModel,
            clientProductsDisplayerStatsViewModel = clientProductsDisplayerStatsViewModel,
            grossistProductsDiviseurViewModelsFragment = grossistProductsDiviseurViewModelsFragment,
            app_Initialize_ViewModel=app_Initialize_ViewModel
            )
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Move permission check before setting content
        permissionHandler.checkAndRequestPermissions(object : PermissionHandler.PermissionCallback {
            @RequiresApi(Build.VERSION_CODES.Q)
            override fun onPermissionsGranted() {
                setContent {
                    B_ServeurTheme {
                        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                            MainScreen(
                                modifier = Modifier.padding(innerPadding),
                                appViewModels
                            )
                        }
                    }
                }
            }

            @RequiresApi(Build.VERSION_CODES.Q)
            override fun onPermissionsDenied() {
                // Handle permission denial, maybe show a message or close the app
                finish()
            }

            override fun onPermissionRationale(permissions: Array<String>) {
                // Optionally show a rationale to the user about why permissions are needed
            }
        })
    }
}
