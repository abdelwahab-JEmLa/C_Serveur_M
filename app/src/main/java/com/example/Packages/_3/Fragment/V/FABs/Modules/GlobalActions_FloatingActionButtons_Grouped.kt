package com.example.Packages._3.Fragment.V.FABs.Modules

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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.Packages._3.Fragment.Models.Ui_Mutable_State
import kotlin.math.roundToInt

@Composable
fun GlobalActions_FloatingActionButtons_Grouped(
    modifier: Modifier = Modifier,
    ui_Mutable_State: Ui_Mutable_State,
) {
    var showLabels by remember { mutableStateOf(true) }
    var showFloatingButtons by remember { mutableStateOf(false) }
    var clickCount by remember { mutableIntStateOf(0) }

    // State for drag position
    var offsetX by remember { mutableFloatStateOf(-600f) }
    var offsetY by remember { mutableFloatStateOf(-0f) }

    LaunchedEffect(showFloatingButtons) {
        if (!showFloatingButtons) {
            clickCount = 0
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
                    FabButton(
                        icon =  Icons.Default.CleaningServices ,
                        label =  "No Mode" ,
                        color = Color(0xEC212020),
                        showLabel = showLabels,
                        onClick = {  // Reset all modes
                            ui_Mutable_State.apply {
                                mode_Update_Produits_Non_Defini_Grossist = false
                                mode_Trie_Produit_Non_Trouve = false
                                Update_Parent_Ui_State_Var(this)
                                clickCount=0
                            }
                        }
                    )
                    FabButton(
                        icon = Icons.Default.Upload,
                        label = when (clickCount) {
                            1 -> "mode_Update_Produits_..."
                            2 -> "mode_Trie_Produit_Non_Trouve"
                            else -> "No Mode"
                        },
                        color = when (clickCount) {
                            1 -> Color(0xFFFF5722)
                            2 -> Color(0xFFFFC107)
                            else -> Color(0xEC212020)
                        },
                        showLabel = showLabels,
                        isFiltered = clickCount > 0,
                        onClick = {
                            when (clickCount) {
                                0 -> {
                                    // First click: Enable update mode
                                    ui_Mutable_State.apply {
                                        mode_Update_Produits_Non_Defini_Grossist = true
                                        mode_Trie_Produit_Non_Trouve = false
                                        Update_Parent_Ui_State_Var(this)
                                    }
                                    clickCount++
                                }
                                1 -> {
                                    // Second click: Enable trie mode
                                    ui_Mutable_State.apply {
                                        mode_Update_Produits_Non_Defini_Grossist = false
                                        mode_Trie_Produit_Non_Trouve = true
                                        Update_Parent_Ui_State_Var(this)
                                    }
                                    clickCount++
                                }
                                else -> {
                                    // Reset all modes
                                    ui_Mutable_State.apply {
                                        mode_Update_Produits_Non_Defini_Grossist = false
                                        mode_Trie_Produit_Non_Trouve = false
                                        Update_Parent_Ui_State_Var(this)
                                    }
                                    clickCount = 0
                                }
                            }
                        }
                    )

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

        FloatingActionButton(
            onClick = { showFloatingButtons = !showFloatingButtons },
            modifier = Modifier.size(48.dp),
            containerColor = Color(0xFF3F51B5)
        ) {
            Icon(
                imageVector = if (showFloatingButtons) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
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
