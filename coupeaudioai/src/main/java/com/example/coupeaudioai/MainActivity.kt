package com.example.coupeaudioai

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
import com.example.App_Produits_Main._2.ViewModel.AppsProduitsMainDataBase_ViewModel
import com.example.Main.MainScreen.MainScreen
import com.example.coupeaudioai.Modules.Init.PermissionHandler
import com.example.coupeaudioai.ui.theme.B_ServeurTheme

data class AppViewModels(
    val app_Initialize_ViewModel: AppsProduitsMainDataBase_ViewModel,
    )

// ViewModelFactory.kt
class ViewModelFactory(
    private val context: Context,
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AppsProduitsMainDataBase_ViewModel::class.java) ->
                AppsProduitsMainDataBase_ViewModel() as T
            else -> throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
        }
    }
}

class MainActivity : ComponentActivity() {
    private val permissionHandler by lazy { PermissionHandler(this) }
    private val viewModelFactory by lazy { ViewModelFactory(applicationContext) }
    private val app_Initialize_ViewModel: AppsProduitsMainDataBase_ViewModel by viewModels { viewModelFactory }
    private val appViewModels by lazy {
        AppViewModels(
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
