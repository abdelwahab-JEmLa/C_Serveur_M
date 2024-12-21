package com.example.Packages._3.Fragment.V.FABs.Modules

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.example.Packages._3.Fragment.Models.UiState
import com.example.App_Produits_Main._3.Modules.Add_New_Produit.CameraPickImageHandler
import com.example.App_Produits_Main.Model.App_Initialize_Model
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
internal fun GlobalActions_FloatingActionButtons_Grouped(
    modifier: Modifier = Modifier,
    fragment_Ui_State: UiState,
    app_Initialize_Model: App_Initialize_Model,
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

    // First filter products based on quantity and supplier
    val existingProduct = app_Initialize_Model.produit_Main_DataBase
        .filter { produit ->
            // Calculate total quantity ordered across all suppliers and colors
            val totalQuantity = produit.grossist_Choisi_Pour_Acheter_CeProduit
                .flatMap { it.colours_Et_Gouts_Commende }
                .sumOf { it.quantity_Achete }

            // Check if the product matches the selected supplier filter
            val supplierMatch = if (fragment_Ui_State.selectedSupplierId != 0L) {
                produit.grossist_Choisi_Pour_Acheter_CeProduit.any {
                    it.supplier_id == fragment_Ui_State.selectedSupplierId
                }
            } else true

            // Return true for products meeting both criteria
            totalQuantity > 0 && supplierMatch
        }
        // Then find the first product that has a valid position in its supplier data
        .firstOrNull { produit ->
            produit.grossist_Choisi_Pour_Acheter_CeProduit
                .any { supplier ->
                    supplier.supplier_id == fragment_Ui_State.selectedSupplierId &&
                            supplier.position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit >= 1 &&
                            produit.id>2000
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
                            val uri = imageHandler.handleNewProductImageCapture(existingProduct)
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
