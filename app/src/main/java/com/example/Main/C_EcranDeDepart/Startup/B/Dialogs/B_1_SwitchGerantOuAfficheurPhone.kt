package com.example.Main.C_EcranDeDepart.Startup.B.Dialogs

import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.Packages.Views._2LocationGpsClients.App.NH_1.id1_ClientsLocationGps.B.Dialogs.ControlButton

@Composable
fun B_1_SwitchGerantOuAfficheurPhone(
    viewModelInitApp: ViewModelInitApp,
    showLabels: Boolean
) {
    ControlButton(
        onClick = {
            viewModelInitApp
                ._paramatersAppsViewModelModel
                .cLeTelephoneDuGerant = true
        },
        icon = Icons.Default.Delete,
        contentDescription = "",
        showLabels = showLabels,
        labelText =  "",
        containerColor =  Color(0xFFF44336)
    )
}
