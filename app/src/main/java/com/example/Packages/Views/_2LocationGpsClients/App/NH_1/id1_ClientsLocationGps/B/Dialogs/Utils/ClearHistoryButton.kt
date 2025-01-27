package com.example.Packages.Views._2LocationGpsClients.App.NH_1.id1_ClientsLocationGps.B.Dialogs.Utils

import com.example.Packages.Views._2LocationGpsClients.App.NH_1.id1_ClientsLocationGps.ViewModel.Extension.Extensions.clearAllData
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.Packages.Views._2LocationGpsClients.App.NH_1.id1_ClientsLocationGps.B.Dialogs.ControlButton
import com.example.Packages.Views._2LocationGpsClients.App.NH_1.id1_ClientsLocationGps.ViewModel.Extension.ViewModelExtension_App2_F1

@Composable
fun ClearHistoryButton(
    extensionVM: ViewModelExtension_App2_F1,
    showLabels: Boolean,
    viewModelInitApp: ViewModelInitApp
) {
    var clearDataClickCount by remember { mutableIntStateOf(0) }
    val context = LocalContext.current

    ControlButton(
        onClick = {
            if (clearDataClickCount == 0) {
                clearDataClickCount++
            } else {
                extensionVM.clearAllData()
                clearDataClickCount = 0
            }
        },
        icon = Icons.Default.Delete,
        contentDescription = "Clear history",
        showLabels = showLabels,
        labelText = if (clearDataClickCount == 0) "Clear History" else "Click again to confirm",
        containerColor = if (clearDataClickCount == 0) Color(0xFFE91E63) else Color(0xFFF44336)
    )
}
