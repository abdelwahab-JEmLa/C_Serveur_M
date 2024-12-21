package com.example.Packages.Z.Archives.P3.F.FABs.Modules

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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Dehaze
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Filter1
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Moving
import androidx.compose.material.icons.filled.Transform
import androidx.compose.material.icons.filled.Upcoming
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.Packages.Z.Archives.P3.Ui_Statue_DataBase
import com.example.Packages.Z.Archives.P3.E.ViewModel.B.Components.Parent_Ui_Statue_DataBase_Update
import com.example.Packages.Z.Archives.P3.E.ViewModel.ViewModelFragment
import kotlin.math.roundToInt

@Composable
fun Global_Controls_FloatingActionButtons(
    modifier: Modifier,
    showFloatingButtons: Boolean,
    onToggleFloatingButtons: () -> Unit,
    onToggleToFilterToMove: () -> Unit,
    onChangeGridColumns: (Int) -> Unit,
    filterSuppHandledNow: Boolean,
    onLaunchVoiceRecognition: () -> Unit,
    viewModelFragment: ViewModelFragment,
    onToggleMoveFirstNonDefined: () -> Unit,
    uiState: Ui_Statue_DataBase,
) {
    var currentGridColumns by remember { mutableIntStateOf(2) }
    val maxGridColumns = 6
    var showLabels by remember { mutableStateOf(true) }
    var showToggleMoveFirstNonDefined by remember { mutableStateOf(false) }

    // State for drag position
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

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
                    listOf(
                        FabData(
                            icon = Icons.Default.Upcoming,
                            label = "Initialize Articles",
                            color = Color(0xFF4CAF50),
                            onClick = { viewModelFragment.intialaizeArticlesCommendToSupplierFromClientNeed() }
                        ),
                        FabData(
                            icon = if (uiState.mode_click_is_trensfert_to_fab_gross) Icons.Default.Close else Icons.Default.Transform,
                            label = " mode_click_is_trensfert... ",
                            color = Color(0xFF2196F3),
                            onClick = {
                                viewModelFragment.Parent_Ui_Statue_DataBase_Update(
                                    "mode_click_is_trensfert_to_fab_gross",
                                    !uiState.mode_click_is_trensfert_to_fab_gross
                                )
                            }
                        ),
                        FabData(
                            icon = if (showToggleMoveFirstNonDefined) Icons.Default.Close else Icons.Default.Filter1,
                            label = "Toggle Move First",
                            color = Color(0xFF2196F3),
                            onClick = {
                                onToggleMoveFirstNonDefined()
                                showToggleMoveFirstNonDefined = !showToggleMoveFirstNonDefined
                            }
                        ),
                        FabData(
                            icon = Icons.Default.Mic,
                            label = "Voice Recognition",
                            color = Color(0xFF9C27B0),
                            onClick = { onLaunchVoiceRecognition() }
                        ),
                        FabData(
                            icon = if (filterSuppHandledNow) Icons.Default.FileUpload else Icons.Default.Moving,
                            label = "Filter To Move",
                            color = Color(0xFFFF9800),
                            onClick = { onToggleToFilterToMove() }
                        ),
                        FabData(
                            icon = Icons.Default.GridView,
                            label = "Change Grid (${currentGridColumns})",
                            color = Color(0xFF607D8B),
                            onClick = {
                                currentGridColumns = (currentGridColumns % maxGridColumns) + 1
                                onChangeGridColumns(currentGridColumns)
                            }
                        ),
                        FabData(
                            icon = if (showLabels) Icons.Default.Close else Icons.Default.Dehaze,
                            label = if (showLabels) "Hide Labels" else "Show Labels",
                            color = Color(0xFFE91E63),
                            onClick = { showLabels = !showLabels }
                        )
                    ).forEach { fabData ->
                        FabButton(
                            icon = fabData.icon,
                            label = fabData.label,
                            color = fabData.color,
                            showLabel = showLabels,
                            onClick = fabData.onClick
                        )
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = onToggleFloatingButtons,
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

internal data class FabData(
    val icon: ImageVector,
    val label: String,
    val color: Color,
    val onClick: () -> Unit
)

@Composable
internal fun FabButton(
    icon: ImageVector,
    label: String,
    color: Color,
    showLabel: Boolean,
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
            Icon(icon, contentDescription = label)
        }
    }
}
