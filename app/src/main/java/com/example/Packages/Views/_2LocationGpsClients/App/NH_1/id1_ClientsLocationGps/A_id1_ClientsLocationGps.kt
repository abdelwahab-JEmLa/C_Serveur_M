package com.example.Packages.Views._2LocationGpsClients.App.NH_1.id1_ClientsLocationGps

import Z_MasterOfApps.Kotlin.Model.ClientsDataBase
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import android.content.Context
import android.widget.LinearLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.Packages.Views._2LocationGpsClients.App.NH_1.id1_ClientsLocationGps.B.Dialogs.MapControls
import com.example.Packages.Views._2LocationGpsClients.App.NH_1.id1_ClientsLocationGps.B.Dialogs.MarkerStatusDialog
import com.example.Packages.Views._2LocationGpsClients.App.NH_1.id1_ClientsLocationGps.B.Dialogs.Utils.DEFAULT_LATITUDE
import com.example.Packages.Views._2LocationGpsClients.App.NH_1.id1_ClientsLocationGps.B.Dialogs.Utils.DEFAULT_LONGITUDE
import com.example.Packages.Views._2LocationGpsClients.App.NH_1.id1_ClientsLocationGps.B.Dialogs.Utils.getCurrentLocation
import com.example.Packages.Views._2LocationGpsClients.App.NH_1.id1_ClientsLocationGps.ViewModel.Extension.ViewModelExtension_App2_F1
import com.example.c_serveur.R
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.infowindow.MarkerInfoWindow

@Composable
fun A_id1_ClientsLocationGps(
    modifier: Modifier = Modifier,
    viewModel: ViewModelInitApp = viewModel(),
    clientEnCourDeVent: Long=0, onUpdateLongAppSetting: () -> Unit = {},

    ) {
    val extensionVM = ViewModelExtension_App2_F1(viewModel.viewModelScope,viewModel.produitsMainDataBase,viewModel.clientDataBaseSnapList,viewModel)

    val context = LocalContext.current
    val currentZoom by remember { mutableDoubleStateOf(18.2) }
    val mapView =remember { MapView(context) }
    var selectedMarker by remember { mutableStateOf<Marker?>(null) }
    var showMarkerDialog by remember { mutableStateOf(false) }
    val showMarkerDetails by remember { mutableStateOf(true) }

    // Initialize map position with current location
    LaunchedEffect(Unit) {
        val location = getCurrentLocation(context)

        // Set initial position
        val initialPosition = if (location != null) {
            MapPosition(
                latitude = location.latitude,
                longitude = location.longitude,
                isInitialized = true
            )
        } else {
            MapPosition(
                latitude = DEFAULT_LATITUDE,
                longitude = DEFAULT_LONGITUDE,
                isInitialized = true
            )
        }

        // Configure map settings
        mapView.apply {
            setMultiTouchControls(true)
            setTileSource(TileSourceFactory.MAPNIK)
            controller.setZoom(currentZoom)
            controller.animateTo(GeoPoint(initialPosition.latitude, initialPosition.longitude))
        }
    }
    // Configuration initiale de la carte
    DisposableEffect(context) {
        Configuration.getInstance()
            .load(context, context.getSharedPreferences("osmdroid", Context.MODE_PRIVATE))
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setMultiTouchControls(true)

        onDispose {
            mapView.overlays.clear()
        }
    }

    val clientDataBaseSnapList = viewModel.clientDataBaseSnapList

    LaunchedEffect(clientDataBaseSnapList.toList(), clientEnCourDeVent) {
        // Clear existing client markers
        val markersToRemove = mapView.overlays.filterIsInstance<Marker>()
            .filter { marker -> clientDataBaseSnapList.any { it.id.toString() == marker.id } }
        mapView.overlays.removeAll(markersToRemove)

        clientDataBaseSnapList.forEach { client ->
            val actuelleEtat =
                if (client.id==clientEnCourDeVent)
                    ClientsDataBase.GpsLocation.DernierEtatAAffiche.ON_MODE_COMMEND_ACTUELLEMENT else
                client.gpsLocation.actuelleEtat

            val marker = Marker(mapView).apply {
                id = client.id.toString()
                position = GeoPoint(
                    client.gpsLocation.latitude.takeIf { it != 0.0 } ?: DEFAULT_LATITUDE,
                    client.gpsLocation.longitude
                )
                title = client.nom
                snippet = if (client.statueDeBase.cUnClientTemporaire)
                    "Client temporaire" else "Client permanent"
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                infoWindow = MarkerInfoWindow(R.layout.marker_info_window, mapView)

                val container = infoWindow.view.findViewById<LinearLayout>(R.id.info_window_container)
                    ?: return@forEach // Skip if container not found
                val backgroundColor = actuelleEtat?.let { statue ->
                    ContextCompat.getColor(context, statue.color)
                } ?: ContextCompat.getColor(context, android.R.color.white)
                container.setBackgroundColor(backgroundColor)

                setOnMarkerClickListener { clickedMarker, _ ->
                    selectedMarker = clickedMarker
                    showMarkerDialog = true
                    if (showMarkerDetails) clickedMarker.showInfoWindow()
                    true
                }
            }
            mapView.overlays.add(marker)
            marker.showInfoWindow()
        }
        mapView.invalidate() // Refresh the map to show changes
    }
    Box(modifier = modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { mapView }
        )

        Box(
            modifier = Modifier
                .size(8.dp)
                .background(Color.Red, CircleShape)
                .align(Alignment.Center)
        )

        if (viewModel._paramatersAppsViewModelModel.fabsVisibility) {
            MapControls(
                extensionVM=extensionVM,
                mapView = mapView,
                viewModelInitApp = viewModel
            )
        }
        if (showMarkerDialog && selectedMarker != null) {
            MarkerStatusDialog(
                extensionVM=extensionVM,
                 viewModel = viewModel,
                 selectedMarker = selectedMarker,
                 onDismiss = { showMarkerDialog = false },
                onUpdateLongAppSetting = onUpdateLongAppSetting
            )
        }
    }
}

private data class MapPosition(
    val latitude: Double,
    val longitude: Double,
    val isInitialized: Boolean
)

