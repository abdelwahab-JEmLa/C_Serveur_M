package Views._2LocationGpsClients.App.MainApp.B.Dialogs.Utils

import Views._2LocationGpsClients.App.MainApp.B.Dialogs.ControlButton
import Views._2LocationGpsClients.App.MainApp.ViewModel.Extension.Extensions.clearAllData
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

@Composable
fun ClearHistoryButton(
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
                viewModelInitApp.mapsHandler.clearAllData()
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
