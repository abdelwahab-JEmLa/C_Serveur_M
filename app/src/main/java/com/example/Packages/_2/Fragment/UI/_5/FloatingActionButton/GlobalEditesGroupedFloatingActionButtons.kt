package com.example.Packages._2.Fragment.UI._5.FloatingActionButton

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Dehaze
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.Apps_Head._1.Model.AppsHeadModel
import com.example.Apps_Head._3.Modules.Add_New_Produit.CameraPickImageHandler
import com.example.Packages._2.Fragment.ViewModel.Models.UiState
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
internal fun GlobalEditesGroupedFloatingActionButtons(
    modifier: Modifier = Modifier,
    fragment_Ui_State: UiState,
    app_Initialize_Model: AppsHeadModel,
    produitsMainDataBase: List<AppsHeadModel.ProduitModel>,
) {
    var showLabels by remember { mutableStateOf(true) }
    var showFloatingButtons by remember { mutableStateOf(false) }

    var offsetX by remember { mutableFloatStateOf(-600f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    val context = LocalContext.current
    val imageHandler = remember { CameraPickImageHandler(context, app_Initialize_Model) }
    val scope = rememberCoroutineScope()

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            imageHandler.tempImageUri?.let { uri ->
                scope.launch {
                    imageHandler.handleImageCaptureResult(uri)
                }
            }
        }
    }

    Column(
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(16.dp)
            .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    offsetX += dragAmount.x
                    offsetY += dragAmount.y
                }
            }
            .zIndex(1f)
    ) {
        AnimatedVisibility(
            visible = showFloatingButtons,
            enter = fadeIn() + expandHorizontally(),
            exit = fadeOut() + shrinkHorizontally()
        ) {
            Surface(
                modifier = Modifier.wrapContentHeight(),
                color = Color.Transparent
            ) {
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    // Camera FAB
                    FabButton(
                        icon = Icons.Default.AddAPhoto,
                        label = "Take Photo",
                        color = Color(0xFF4CAF50),
                        showLabel = showLabels,
                        onClick = {
                            val uri = imageHandler.handleNewProductImageCapture(
                                app_Initialize_Model.produitsMainDataBase
                                    .firstOrNull{ produit ->
                                        produit.bonCommendDeCetteCota?.grossistInformations?.auFilterFAB == true
                                                && produit.id>2000
                                    }
                            )
                            cameraLauncher.launch(uri)
                        }
                    )

                    // Mode Toggle FAB
                    FabButton(
                        icon = Icons.Default.Upload,
                        label = when (fragment_Ui_State.currentMode) {
                            UiState.Affichage_Et_Click_Modes.MODE_Click_Change_Position -> "Change Position Mode"
                            UiState.Affichage_Et_Click_Modes.MODE_Affiche_Achteurs -> "Buyers Mode"
                            UiState.Affichage_Et_Click_Modes.MODE_Affiche_Produits -> "Products Mode"
                        },
                        color = Color(0xFFFF5722),
                        showLabel = showLabels,
                        isFiltered = true,
                        onClick = {
                            fragment_Ui_State.currentMode = UiState.Affichage_Et_Click_Modes.toggle(fragment_Ui_State.currentMode)
                        }
                    )

                    // Labels Toggle FAB
                    FabButton(
                        icon = if (showLabels) Icons.Default.Close else Icons.Default.Dehaze,
                        label = if (showLabels) "Hide Labels" else "Show Labels",
                        color = Color(0xFFE91E63),
                        showLabel = showLabels,
                        onClick = { showLabels = !showLabels }
                    )
                }
            }
        }

        // Main Toggle FAB
        FloatingActionButton(
            onClick = { showFloatingButtons = !showFloatingButtons },
            modifier = Modifier.size(48.dp),
            containerColor = Color(0xFF3F51B5)
        ) {
            Icon(
                imageVector = if (showFloatingButtons)
                    Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = if (showFloatingButtons) "Collapse" else "Expand"
            )
        }
    }
}

@Composable
private fun FabButton(
    icon: ImageVector,
    label: String,
    color: Color,
    showLabel: Boolean,
    isFiltered: Boolean = false,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End
    ) {
        AnimatedVisibility(
            visible = showLabel,
            enter = fadeIn() + expandHorizontally(),
            exit = fadeOut() + shrinkHorizontally()
        ) {
            Surface(
                modifier = Modifier.padding(end = 8.dp),
                shape = MaterialTheme.shapes.medium,
                color = Color.Black.copy(alpha = 0.6f)
            ) {
                Text(
                    text = label,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = color
                )
            }
        }

        FloatingActionButton(
            onClick = onClick,
            modifier = Modifier.size(48.dp),
            containerColor = color
        ) {
            Icon(
                imageVector = if (isFiltered) Icons.Default.Close else icon,
                contentDescription = label
            )
        }
    }
}
