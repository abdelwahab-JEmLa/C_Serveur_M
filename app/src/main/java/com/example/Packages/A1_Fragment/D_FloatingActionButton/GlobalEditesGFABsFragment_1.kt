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
import java.io.IOException

enum class DeviceMode {
    SERVER,
    DISPLAY
}

@Composable
fun GlobalEditesGFABsFragment_1(
    appsHeadModel: AppsHeadModel,
    modifier: Modifier = Modifier,
    onError: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var showOptions by remember { mutableStateOf(false) }
    var deviceMode by remember { mutableStateOf(DeviceMode.SERVER) }
    var tempImageUri by remember { mutableStateOf<Uri?>(null) }
    var pendingProduct by remember { mutableStateOf<AppsHeadModel.ProduitModel?>(null) }

    fun createTempImageUri(): Uri? {
        return try {
            val tempFile = File.createTempFile("temp_image", ".jpg", context.cacheDir)
            FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                tempFile
            ).also { tempImageUri = it }
        } catch (e: IOException) {
            onError("Failed to create temporary image file: ${e.localizedMessage}")
            null
        }
    }

    suspend fun handleImageCapture(uri: Uri) {
        try {
            pendingProduct?.let { product ->
                val fileName = "${product.id}_1.jpg"
                val localStorageDir = File(imagesProduitsLocalExternalStorageBasePath).apply {
                    if (!exists()) mkdirs()
                }

                val localFile = File(localStorageDir, fileName)

                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    val imageBytes = inputStream.readBytes()

                    // Save to local storage
                    FileOutputStream(localFile).use { output ->
                        output.write(imageBytes)
                    }

                    // Upload to Firebase Storage
                    imagesProduitsFireBaseStorageRef
                        .child(fileName)
                        .putBytes(imageBytes)
                        .await()

                    // Update product status
                    product.apply {
                        statuesBase.apply {
                            prePourCameraCapture = false
                            naAucunImage = false
                        }
                        besoin_To_Be_Updated = true
                    }

                    // Update in Firebase Realtime Database
                    AppsHeadModel.produitsFireBaseRef
                        .child(product.id.toString())
                        .setValue(product)
                        .await()
                }
            } ?: throw IllegalStateException("No pending product found")

        } catch (e: Exception) {
            onError("Failed to process image: ${e.localizedMessage}")
        } finally {
            pendingProduct = null
            tempImageUri = null
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            tempImageUri?.let { uri ->
                scope.launch {
                    handleImageCapture(uri)
                }
            }
        } else {
            onError("Failed to capture image")
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
                        val productForCapture = appsHeadModel.produitsMainDataBase
                            .find { it.statuesBase.prePourCameraCapture }

                        if (productForCapture != null) {
                            pendingProduct = productForCapture
                            createTempImageUri()?.let { uri ->
                                cameraLauncher.launch(uri)
                            }
                        } else {
                            onError("No product marked for image capture")
                        }
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
