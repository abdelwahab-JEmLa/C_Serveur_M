package com.example.Packages.Views._2LocationGpsClients.App.Main.B.Dialogs.Utils

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
 fun ControlButton(
    onClick: () -> Unit,
    icon: ImageVector,
    contentDescription: String,
    showLabels: Boolean,
    labelText: String,
    containerColor: Color,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        FloatingActionButton(
            onClick = onClick,
            modifier = modifier.size(40.dp),
            containerColor = containerColor
        ) {
            Icon(icon, contentDescription)
        }
        if (showLabels) {
            Text(
                labelText,
                modifier = Modifier
                    .background(containerColor)
                    .padding(4.dp),
                color = Color.White
            )
        }
    }
}

@Composable
 fun ShowDetailsButton(
    showLabels: Boolean,
    showMarkerDetails: Boolean,
    onShowMarkerDetailsChange: (Boolean) -> Unit
) {
    ControlButton(
        onClick = { onShowMarkerDetailsChange(!showMarkerDetails) },
        icon = Icons.Default.Info,
        contentDescription = "Details",
        showLabels = showLabels,
        labelText = if (showMarkerDetails) "Hide details" else "Show details",
        containerColor = Color(0xFF009688)
    )
}

@Composable
 fun LabelsButton(
    showLabels: Boolean,
    onShowLabelsChange: (Boolean) -> Unit
) {
    ControlButton(
        onClick = { onShowLabelsChange(!showLabels) },
        icon = Icons.Default.Info,
        contentDescription = if (showLabels) "Hide labels" else "Show labels",
        showLabels = showLabels,
        labelText = if (showLabels) "Hide labels" else "Show labels",
        containerColor = Color(0xFF3F51B5)
    )
}
