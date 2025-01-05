package com.example.Packages.A1_Fragment.D_FloatingActionButton

import android.net.Uri
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
import androidx.core.content.FileProvider
import com.example.Apps_Head._1.Model.AppsHeadModel
import com.example.Apps_Head._1.Model.AppsHeadModel.Companion.imagesProduitsFireBaseStorageRef
import com.example.Apps_Head._1.Model.AppsHeadModel.Companion.imagesProduitsLocalExternalStorageBasePath
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.File
import java.io.FileOutputStream

enum class DeviceMode {
    SERVER,
    DISPLAY
}

@Composable
fun GlobalEditesGFABsFragment_1(
    appsHeadModel: AppsHeadModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var showOptions by remember { mutableStateOf(false) }
    var deviceMode by remember { mutableStateOf(DeviceMode.SERVER) }

    val handleImage = { uri: Uri ->
        scope.launch {
            try {
                // Find the first product that needs an image
                val productNeedingImage = appsHeadModel.produitsMainDataBase
                    .find { product ->
                        product.statuesBase.prePourCameraCapture
                    }

                productNeedingImage?.let { product ->
                    val fileName = "${product.id}_1.jpg"

                    // Create local storage directory if it doesn't exist
                    val localStorageDir = File(imagesProduitsLocalExternalStorageBasePath)
                    if (!localStorageDir.exists()) {
                        localStorageDir.mkdirs()
                    }

                    // Save to local storage
                    val localFile = File(localStorageDir, fileName)
                    context.contentResolver.openInputStream(uri)?.use { input ->
                        val imageBytes = input.readBytes()

                        // Save to local storage
                        FileOutputStream(localFile).use { output ->
                            output.write(imageBytes)
                        }

                        // Upload to Firebase
                        imagesProduitsFireBaseStorageRef.child(fileName)
                            .putBytes(imageBytes)
                            .await()
                    }

                    // Update product image status
                    product.statuesBase.apply {
                        prePourCameraCapture = false
                        naAucunImage = false
                    }

                    // Update database
                    AppsHeadModel.produitsFireBaseRef
                        .child(product.id.toString())
                        .setValue(product)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            val tempFile = File.createTempFile("temp_image", ".jpg", context.cacheDir)
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                tempFile
            )
            handleImage(uri)
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
                        val tempFile = File.createTempFile("temp_image", ".jpg", context.cacheDir)
                        val uri = FileProvider.getUriForFile(
                            context,
                            "${context.packageName}.fileprovider",
                            tempFile
                        )
                        cameraLauncher.launch(uri)
                    },
                    containerColor = Color(0xFF4CAF50)
                ) {
                    Icon(Icons.Default.AddAPhoto, contentDescription = "Take Photo")
                }

                // Mode Toggle FAB
                FloatingActionButton(
                    onClick = {
                        deviceMode = if (deviceMode == DeviceMode.SERVER)
                            DeviceMode.DISPLAY else DeviceMode.SERVER
                    },
                    containerColor = Color(0xFFFF5722)
                ) {
                    Icon(
                        Icons.Default.Upload,
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
