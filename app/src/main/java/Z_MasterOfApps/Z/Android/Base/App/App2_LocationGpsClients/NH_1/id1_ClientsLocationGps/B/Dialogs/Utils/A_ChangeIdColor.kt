package Z_MasterOfApps.Z.Android.Base.App.App2_LocationGpsClients.NH_1.id1_ClientsLocationGps.B.Dialogs.Utils

import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import Z_MasterOfApps.Z.Android.Base.App.App2_LocationGpsClients.NH_1.id1_ClientsLocationGps.B.Dialogs.ControlButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color

@Composable
fun A_ChangeIdColor(
    viewModelInitApp: ViewModelInitApp,
    showLabels: Boolean,
) {
    var clearDataClickCount by remember { mutableIntStateOf(0) }

    ControlButton(
        onClick = {
            if (clearDataClickCount == 0) {
                clearDataClickCount++
            } else {
                viewModelInitApp.extentionStartup.updateProductsIdColor1()
                clearDataClickCount = 0
            }
        },
        icon = Icons.Default.Repeat,
        contentDescription = "Repeat",
        showLabels = showLabels,
        labelText = if (clearDataClickCount == 0) "Repeat" else "Click again to confirm",
        containerColor = if (clearDataClickCount == 0) Color(0xFFE91E63) else Color(0xFFFFEB3B)
    )
}
