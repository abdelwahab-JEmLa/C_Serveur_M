package com.example.c_serveur

import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
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

data class AppViewModels(
    val initViewModel: ViewModelInitApp,
)

class ViewModelFactory() : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ViewModelInitApp::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ViewModelInitApp() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class MainActivity : ComponentActivity() {
    private val viewModelFactory by lazy { ViewModelFactory() }
    private val app_Initialize_ViewModel: ViewModelInitApp by viewModels { viewModelFactory }


    private val appViewModels by lazy {
        AppViewModels(
            initViewModel = app_Initialize_ViewModel
        )
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

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
}
