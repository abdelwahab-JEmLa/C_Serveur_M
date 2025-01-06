package com.example.Packages.A1_Fragment.D_FloatingActionButton

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.Apps_Head._1.Model.AppsHeadModel
import com.example.Packages.A1_Fragment.E.Modules.CameraHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

enum class DeviceMode {
    SERVER,
    DISPLAY
}

@Composable
fun GlobalEditesGFABsFragment_1(
    appsHeadModel: AppsHeadModel,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var showOptions by remember { mutableStateOf(false) }
    var deviceMode by remember { mutableStateOf(DeviceMode.SERVER) }

    val cameraHandler = remember { CameraHandler(context) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            cameraHandler.getTempImageUri()?.let { uri ->
                scope.launch {
                    cameraHandler.handleImageCapture(uri)
                }
            }
        } else {
            scope.launch {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Failed to capture image", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.values.all { it }
        if (allGranted) {
            if (cameraHandler.getPendingProduct() != null && cameraHandler.getTempImageUri() != null) {
                cameraLauncher.launch(cameraHandler.getTempImageUri()!!)
            }
        } else {
            scope.launch {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        "Permissions required for camera operation",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    Box(
        modifier = modifier.padding(16.dp),
        contentAlignment = Alignment.BottomEnd
    ) {
        if (showOptions) {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Camera FAB
                FloatingActionButton(
                    onClick = {
                        cameraHandler.checkAndRequestPermissions(
                            permissionLauncher,
                            cameraLauncher,
                            appsHeadModel
                        )
                    },
                    containerColor = Color(0xFF4CAF50)
                ) {
                    Icon(
                        imageVector = Icons.Default.AddAPhoto,
                        contentDescription = "Take Photo"
                    )
                }

                // Mode Toggle FAB
                FloatingActionButton(
                    onClick = {
                        deviceMode = when (deviceMode) {
                            DeviceMode.SERVER -> DeviceMode.DISPLAY
                            DeviceMode.DISPLAY -> DeviceMode.SERVER
                        }
                    },
                    containerColor = Color(0xFFFF5722)
                ) {
                    Icon(
                        imageVector = Icons.Default.Upload,
                        contentDescription = if (deviceMode == DeviceMode.SERVER)
                            "Switch to Display Mode" else "Switch to Server Mode"
                    )
                }
            }
        }

        // Main FAB
        FloatingActionButton(
            onClick = { showOptions = !showOptions },
            containerColor = Color(0xFF3F51B5)
        ) {
            Icon(
                imageVector = if (showOptions) Icons.Default.ExpandLess
                else Icons.Default.ExpandMore,
                contentDescription = if (showOptions) "Hide Options" else "Show Options"
            )
        }
    }
}
