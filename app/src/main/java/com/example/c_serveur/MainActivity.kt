package com.example.c_serveur

import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import Z_MasterOfApps.Z_AppsFather.Kotlin._0.Main.Developed.Code.PermissionHandler
import Z_MasterOfApps.Z_AppsFather.Kotlin._3.Init.A_LoadFireBase.FirebaseOfflineHandler
import android.app.Application
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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.Main.AppNavHost.MainScreen
import com.example.c_serveur.ui.theme.B_ServeurTheme
import com.google.firebase.FirebaseApp

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)?.let { app ->
            FirebaseOfflineHandler.initializeFirebase(app)
        }
    }
}

data class AppViewModels(
    val initViewModel: ViewModelInitApp,
    )

class ViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ViewModelInitApp::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ViewModelInitApp(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
class MainActivity : ComponentActivity() {
    private val permissionHandler by lazy { PermissionHandler(this) }
    private val viewModelFactory by lazy { ViewModelFactory(applicationContext, ) }
    private val app_Initialize_ViewModel: ViewModelInitApp by viewModels { viewModelFactory }



    private val appViewModels by lazy {
        AppViewModels(
           initViewModel=app_Initialize_ViewModel
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
