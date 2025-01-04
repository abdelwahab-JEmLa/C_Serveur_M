package com.example.Packages.A2_Fragment.D_FloatingActionButton

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.Apps_Head._1.Model.AppsHeadModel
import com.example.Packages.A1_Fragment.D_FloatingActionButton.CE_TELEPHONE_EST

enum class CE_TELEPHONE_EST {
    _SERVEUR,
    _AFFICHEUR
}
@Composable
fun GlobalEditesGFABsFragment_2(
    modifier: Modifier = Modifier,
    appsHeadModel: AppsHeadModel,
) {

    var currentMode by remember { mutableStateOf(CE_TELEPHONE_EST._SERVEUR) }
    var showOptions by remember { mutableStateOf(false) }



    Box(
        modifier = modifier.padding(16.dp),
        contentAlignment = Alignment.BottomEnd
    ) {
        if (showOptions) {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Mode Toggle Button
                FloatingActionButton(
                    onClick = {
                        currentMode = if (currentMode == CE_TELEPHONE_EST._SERVEUR) CE_TELEPHONE_EST._AFFICHEUR else CE_TELEPHONE_EST._SERVEUR
                    },
                    containerColor = Color(0xFFFF5722)
                ) {
                    Icon(Icons.Default.Upload, if (currentMode == CE_TELEPHONE_EST._SERVEUR) "To _AFFICHEUR" else "To _SERVEUR")
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
