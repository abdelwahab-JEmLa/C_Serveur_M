package com.example.c_serveur

import Z_MasterOfApps.Z_AppsFather.Kotlin._3.Init.A_LoadFireBase.FirebaseOfflineHandler
import android.app.Application
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import com.example.Main.MainScreen
import com.example.c_serveur.Modules.PermissionHandler
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

class MainActivity : ComponentActivity() {
    // Create PermissionHandler at the activity level
    private lateinit var permissionHandler: PermissionHandler
    private var permissionsGranted = mutableStateOf(false)

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize PermissionHandler before setContent
        permissionHandler = PermissionHandler(this)

        // Check permissions immediately
        permissionHandler.checkAndRequestPermissions(object : PermissionHandler.PermissionCallback {
            override fun onPermissionsGranted() {
                permissionsGranted.value = true
            }

            override fun onPermissionsDenied() {
                finish()
            }

            override fun onPermissionRationale(permissions: Array<String>) {
                // Handle rationale if needed
            }
        })

        setContent {
            B_ServeurTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainScreen(
                        modifier = Modifier.padding(innerPadding),
                        permissionsGranted = permissionsGranted.value
                    )
                }
            }
        }
    }
}
