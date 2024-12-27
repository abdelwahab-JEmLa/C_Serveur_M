package P1_StartupScreen.Main.FloatingActionButtonGroup

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Details
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.Main.StartFragment.FragmentsActions
import kotlin.math.roundToInt

@Composable
internal fun FloatingActionButtonGroup(
    modifier: Modifier,
    actions: FragmentsActions,
) {
    var showLabels by remember { mutableStateOf(true) }
    var isExpanded by remember { mutableStateOf(false) }
    var clickCount by remember { mutableIntStateOf(0) }
    var clearDataGrouprurClickCount by remember { mutableIntStateOf(0) }


    // State for drag position
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    // Reset click count when FAB is collapsed
    LaunchedEffect(isExpanded) {
        if (!isExpanded) {
            clickCount = 0
            clearDataGrouprurClickCount = 0
        }
    }

    Column(
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(top = 30.dp, bottom = 60.dp) // Added top padding to start higher
            .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    offsetX += dragAmount.x
                    offsetY += dragAmount.y
                }
            }
    ) {
        AnimatedVisibility(
            visible = isExpanded,
            enter = fadeIn() + expandHorizontally(),
            exit = fadeOut() + shrinkHorizontally()
        ) {
            Surface(
                modifier = Modifier
                    .wrapContentSize()
                    .padding(end = 16.dp), // Added end padding for alignment
                color = Color.Transparent
            ) {
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(bottom = 8.dp) // Reduced bottom padding since we added it to parent
                ) {
                    listOf(
                        FabData(
                            icon = Icons.Default.Upload,
                            label = if (clickCount == 0) "updateProductCategoryReferences" else "Confirm",
                            color = Color(0xFFFF5722),
                            onClick = {
                                if (clickCount == 0) {
                                    clickCount++
                                } else {
                                    actions.updateProductCategoryReferences()
                                    clickCount = 0
                                }
                            }
                        ),

                        FabData(
                            icon = Icons.Default.Download,
                            label = "Import Clients",
                            color = Color(0xFF4CAF50),
                            onClick = {
                                actions.importClientsDataBase()
                            }
                        ),

                        FabData(
                            icon = if (showLabels) Icons.Default.Close else Icons.Default.Details,
                            label = if (showLabels) "Hide Labels" else "Show Labels",
                            color = Color(0xFFFFC107),
                            onClick = { showLabels = !showLabels }
                        ),
                        FabData(
                            icon = Icons.Default.Close,
                            label =  "not Expande ",
                            color = Color(0xFFDE1010),
                            onClick = {
                                isExpanded=false
                            }
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

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 16.dp)
        ) {
            FloatingActionButton(
                onClick = { isExpanded = !isExpanded },
                modifier = Modifier
                    .size(48.dp)
                    .align(Alignment.BottomEnd),
                containerColor = Color(0xFF3F51B5)
            ) {
                Icon(
                    imageVector = if (isExpanded) Icons.Default.Close else Icons.Default.Details,
                    contentDescription = if (isExpanded) "Collapse" else "Expand"
                )
            }
        }
    }
}

private data class FabData(
    val icon: ImageVector,
    val label: String,
    val color: Color,
    val onClick: () -> Unit
)

@Composable
private fun FabButton(
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
                color = color.copy(alpha = 0.12f) // Light version of the button color for the label
            ) {
                Text(
                    text = label,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = color // Matching text color
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
