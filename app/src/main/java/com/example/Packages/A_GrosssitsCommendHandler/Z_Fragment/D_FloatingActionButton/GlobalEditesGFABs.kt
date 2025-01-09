package com.example.Packages.A_GrosssitsCommendHandler.Z_Fragment.D_FloatingActionButton

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
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
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example._AppsHeadModel._1.Model.AppsHeadModel
import com.example._AppsHeadModel._1.Model.AppsHeadModel.Companion.imagesProduitsFireBaseStorageRef
import com.example._AppsHeadModel._1.Model.AppsHeadModel.Companion.imagesProduitsLocalExternalStorageBasePath
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
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
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var showOptions by remember { mutableStateOf(false) }
    var deviceMode by remember { mutableStateOf(DeviceMode.SERVER) }
    var tempImageUri by remember { mutableStateOf<Uri?>(null) }
    var pendingProduct by remember { mutableStateOf<AppsHeadModel.ProduitModel?>(null) }

    suspend fun handleImageCapture(uri: Uri) {
        try {
            if (uri.toString().isEmpty()) {
                throw IllegalArgumentException("Invalid URI")
            }

            pendingProduct?.let { product ->
                val fileName = "${product.id}_1.jpg"
                val localStorageDir = File(imagesProduitsLocalExternalStorageBasePath).apply {
                    if (!exists()) mkdirs()
                }

                val localFile = File(localStorageDir, fileName)
                var uploadSuccess = false

                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    val imageBytes = inputStream.readBytes()

                    try {
                        // Save to local storage
                        withContext(Dispatchers.IO) {
                            FileOutputStream(localFile).use { output ->
                                output.write(imageBytes)
                            }
                        }

                        // Upload to Firebase Storage
                        val uploadTask = imagesProduitsFireBaseStorageRef
                            .child(fileName)
                            .putBytes(imageBytes)
                            .await()

                        // Verify upload was successful
                        if (uploadTask.metadata != null) {
                            uploadSuccess = true
                        }

                        // Only update product status if both operations succeeded
                        if (uploadSuccess && localFile.exists() && localFile.length() > 0) {
                            // Update product status
                            product.apply {
                                statuesBase.apply {
                                    prePourCameraCapture = false
                                    naAucunImage = false
                                    imageGlidReloadTigger += 1
                                }
                                besoinToBeUpdated = true
                            }

                            // Update in Firebase Realtime Database
                            AppsHeadModel.produitsFireBaseRef
                                .child(product.id.toString())
                                .setValue(product)
                                .await()

                            withContext(Dispatchers.Main) {
                                Toast.makeText(context, "Image uploaded successfully", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            throw IOException("Upload verification failed")
                        }
                    } catch (e: Exception) {
                        // Clean up local file if upload failed
                        if (localFile.exists() && !uploadSuccess) {
                            localFile.delete()
                        }
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "Failed to upload image: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                        throw e
                    }
                } ?: throw IllegalStateException("Could not open input stream for image URI")

            } ?: throw IllegalStateException("No pending product found")

        } catch (e: Exception) {
            Log.e("ImageUpload", "Failed to handle image capture", e)
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Error handling image: ${e.message}", Toast.LENGTH_LONG).show()
            }
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
            if (pendingProduct != null && tempImageUri != null) {
                cameraLauncher.launch(tempImageUri!!)
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

    fun createTempImageUri(): Uri? {
        return try {
            val tempFile = File.createTempFile("temp_image", ".jpg", context.cacheDir).apply {
                deleteOnExit()
            }
            FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                tempFile
            ).also { tempImageUri = it }
        } catch (e: IOException) {
            Log.e("ImageCapture", "Failed to create temp file", e)
            null
        }
    }

    fun checkAndRequestPermissions() {
        val permissions = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        val hasPermissions = permissions.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }

        if (!hasPermissions) {
            permissionLauncher.launch(permissions)
        } else {
            val productForCapture = appsHeadModel.produitsMainDataBase
                .find { it.statuesBase.prePourCameraCapture }

            if (productForCapture != null) {
                pendingProduct = productForCapture
                createTempImageUri()?.let { uri ->
                    cameraLauncher.launch(uri)
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
                    onClick = { checkAndRequestPermissions() },
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
