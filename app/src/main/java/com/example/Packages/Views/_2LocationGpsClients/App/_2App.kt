package com.example.Packages.Views._2LocationGpsClients.App

import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.Packages.Views.Screen
import com.example.Packages.Views._2LocationGpsClients.App.MainApp.A.ViewModel.ChildViewModelOfFragment
import com.example.Packages.Views._2LocationGpsClients.App.MainApp.A_ClientsLocationGps

fun NavGraphBuilder._2App(viewModelInitApp: ViewModelInitApp) {
    composable(ScreensApp2.Fragment1Screen.route) {
        A_ClientsLocationGps(viewModel = ChildViewModelOfFragment(viewModelInitApp))
    }
}

object ScreensApp2 {
    val Fragment1Screen = Fragment1ScreenDataObject
}

data object Fragment1ScreenDataObject : Screen(
    route = "Fragment1",
    icon = Icons.Default.Person,
    title = "A_ClientsLocationGps",
    color = Color(0xFFFF5722)
)

@Preview
@Composable
private fun PreviewApp2_F1() {
    A_ClientsLocationGps(modifier = Modifier.fillMaxSize())
}
