package com.example.Packages.Views._2LocationGpsClients.App.NH_1.id1_ClientsLocationGps.B.Dialogs.Utils

import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.Packages.Views._2LocationGpsClients.App.NH_1.id1_ClientsLocationGps.B.Dialogs.ControlButton
import com.example.Packages.Views._2LocationGpsClients.App.NH_1.id1_ClientsLocationGps.ViewModel.Extension.ViewModelExtension_App2_F1
import org.osmdroid.views.MapView

@Composable
fun AddMarkerButton(
    extensionVM: ViewModelExtension_App2_F1,
    viewModelInitApp: ViewModelInitApp,
    showLabels: Boolean,
    mapView: MapView,
) {
    ControlButton(
        onClick = {
            extensionVM
                .onClickAddMarkerButton(
                    mapView
                )
        },
        icon = Icons.Default.Add,
        contentDescription = "Add marker",
        showLabels = showLabels,
        labelText = "Add",
        containerColor = Color(0xFF2196F3)
    )
}
