package com.example.Packages._1.Fragment.UI.D_FloatingActionButton

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
import com.example.Apps_Head._3.Modules.Add_New_Produit.CameraPickImageHandler
import com.example.Packages._1.Fragment.UI.CE_TELEPHONE_EST
import com.example.Packages._1.Fragment.UI.CE_TELEPHONE_EST.AFFICHEUR
import com.example.Packages._1.Fragment.UI.CE_TELEPHONE_EST.SERVEUR
import kotlinx.coroutines.launch

@Composable
fun GlobalEditesGFABsFragment_1(
    modifier: Modifier = Modifier,
    appsHeadModel: AppsHeadModel,
    onClickFAB: (CE_TELEPHONE_EST) -> Unit
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val imageHandler = remember { CameraPickImageHandler(context, appsHeadModel) }

    var currentMode by remember { mutableStateOf(SERVEUR) }
    var showOptions by remember { mutableStateOf(false) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            imageHandler.tempImageUri?.let { uri ->
                scope.launch { imageHandler.handleImageCaptureResult(uri) }
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
                // Camera Button
                FloatingActionButton(
                    onClick = {
                        val uri = imageHandler.handleNewProductImageCapture(
                            appsHeadModel.produitsMainDataBase.firstOrNull {
                                it.bonCommendDeCetteCota?.grossistInformations?.auFilterFAB == true &&
                                        it.id > 2000
                            }
                        )
                        cameraLauncher.launch(uri)
                    },
                    containerColor = Color(0xFF4CAF50)
                ) {
                    Icon(Icons.Default.AddAPhoto, "Take Photo")
                }

                // Mode Toggle Button
                FloatingActionButton(
                    onClick = {
                        currentMode = if (currentMode == SERVEUR) AFFICHEUR else SERVEUR
                        onClickFAB(currentMode)
                    },
                    containerColor = Color(0xFFFF5722)
                ) {
                    Icon(Icons.Default.Upload, if (currentMode == SERVEUR) "To AFFICHEUR" else "To SERVEUR")
                }
            }
        }

        // Main Toggle Button
        FloatingActionButton(
            onClick = { showOptions = !showOptions },
            containerColor = Color(0xFF3F51B5)
        ) {
            Icon(
                imageVector = if (showOptions) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = if (showOptions) "Hide Options" else "Show Options"
            )
        }
    }
}
