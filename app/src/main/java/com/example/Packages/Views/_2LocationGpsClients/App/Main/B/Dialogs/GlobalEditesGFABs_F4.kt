package com.example.Packages.Views._2LocationGpsClients.App.Main.B.Dialogs

import Z_MasterOfApps.Kotlin.Model.Extension.clientsDisponible
import Z_MasterOfApps.Kotlin.Model._ModelAppsFather
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import android.Manifest
import android.content.Context
import android.location.LocationManager
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.Packages.Views._2LocationGpsClients.App.Main.B.Dialogs.Utils.ControlButton
import com.example.Packages.Views._2LocationGpsClients.App.Main.B.Dialogs.Utils.LabelsButton
import com.example.Packages.Views._2LocationGpsClients.App.Main.B.Dialogs.Utils.NearbyMarkersDialog
import com.example.Packages.Views._2LocationGpsClients.App.Main.B.Dialogs.Utils.ShowDetailsButton
import com.example.Packages.Views._2LocationGpsClients.App.Main.Utils.rememberLocationTracker
import com.example.c_serveur.R
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.infowindow.MarkerInfoWindow
import kotlin.math.roundToInt
@Composable
fun MapControls(
    mapView: MapView,
    viewModelInitApp: ViewModelInitApp,
    markers: MutableList<Marker>,
    showMarkerDetails: Boolean,
    onShowMarkerDetailsChange: (Boolean) -> Unit,
    onMarkerSelected: (Marker) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var showMenu by remember { mutableStateOf(false) }
    var showLabels by remember { mutableStateOf(false) }
    val proximiteMeter = 50.0

    // États pour le drag
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    val currentLocation = if (ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED
    ) {
        locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            ?: locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
    } else null

    var showNearbyMarkersDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        offsetX += dragAmount.x
                        offsetY += dragAmount.y
                    }
                }
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.align(Alignment.BottomStart),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (showMenu) {
                    NearbyMarkersButton(
                        showLabels = showLabels,
                        onShowNearbyMarkersDialog = { showNearbyMarkersDialog = true }
                    )

                    AddMarkerButton(
                        showLabels = showLabels,
                        mapView = mapView,
                        viewModelInitApp = viewModelInitApp,
                        markers = markers,
                        showMarkerDetails = showMarkerDetails,
                        onMarkerSelected = onMarkerSelected
                    )

                    LocationTrackingButton(
                        showLabels = showLabels,
                        mapView = mapView,
                        proximiteMeter = proximiteMeter
                    )

                    ShowDetailsButton(
                        showLabels = showLabels,
                        showMarkerDetails = showMarkerDetails,
                        onShowMarkerDetailsChange = onShowMarkerDetailsChange
                    )
                }

                // Always visible controls
                LabelsButton(
                    showLabels = showLabels,
                    onShowLabelsChange = { showLabels = it }
                )

                MenuButton(
                    showLabels = showLabels,
                    showMenu = showMenu,
                    onShowMenuChange = { showMenu = it }
                )
            }
        }

        // Show nearby markers dialog when active
        if (showMenu) {
            NearbyMarkersDialog(
                viewModelInitApp = viewModelInitApp,
                showDialog = showNearbyMarkersDialog,
                onDismiss = { showNearbyMarkersDialog = false },
                markers = markers,
                currentLocation = currentLocation,
                proxim = proximiteMeter,
                mapView = mapView
            )
        }
    }
}

@Composable
private fun NearbyMarkersButton(
    showLabels: Boolean,
    onShowNearbyMarkersDialog: () -> Unit
) {
    ControlButton(
        onClick = onShowNearbyMarkersDialog,
        icon = Icons.Default.NearMe,
        contentDescription = "Show nearby markers",
        showLabels = showLabels,
        labelText = "Nearby",
        containerColor = Color(0xFFFF5722)
    )
}

@Composable
private fun AddMarkerButton(
    showLabels: Boolean,
    mapView: MapView,
    viewModelInitApp: ViewModelInitApp,
    markers: MutableList<Marker>,
    showMarkerDetails: Boolean,
    onMarkerSelected: (Marker) -> Unit
) {
    ControlButton(
        onClick = {
            val center = mapView.mapCenter
            val newID = viewModelInitApp._modelAppsFather.clientsDisponible
                .maxOf { it.id } + 1
            val newnom = "Nouveau client *$newID"

            val newClient = _ModelAppsFather.ProduitModel.ClientBonVentModel.ClientInformations(
                id = newID,
                nom = newnom,
            ).apply {
                statueDeBase.cUnClientTemporaire = true
                gpsLocation.apply {
                    latitude = center.latitude
                    longitude = center.longitude
                    title = newnom
                    snippet = "Client temporaire"
                    couleur = "#2196F3"

                    locationGpsMark = Marker(mapView).apply {
                        position = GeoPoint(latitude, longitude)
                        this.title = title
                        this.snippet = snippet
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        infoWindow = MarkerInfoWindow(R.layout.marker_info_window, mapView)
                        setOnMarkerClickListener { marker, _ ->
                            onMarkerSelected(marker)
                            if (showMarkerDetails) marker.showInfoWindow()
                            true
                        }
                    }
                }
            }

            val newBonVent = _ModelAppsFather.ProduitModel.ClientBonVentModel(
                vid = System.currentTimeMillis(),
                init_clientInformations = newClient
            )

            val product = viewModelInitApp.produitsMainDataBase.find { it.id == 0L } ?:
            _ModelAppsFather.ProduitModel(id = 0L).also {
                viewModelInitApp.produitsMainDataBase.add(it)
            }

            product.bonsVentDeCetteCota.add(newBonVent)

            newClient.gpsLocation.locationGpsMark?.let { marker ->
                markers.add(marker)
                mapView.overlays.add(marker)
                if (showMarkerDetails) marker.showInfoWindow()
            }
            mapView.invalidate()

            _ModelAppsFather.updateProduit(product, viewModelInitApp)
        },
        icon = Icons.Default.Add,
        contentDescription = "Add marker",
        showLabels = showLabels,
        labelText = "Add",
        containerColor = Color(0xFF2196F3)
    )
}

@Composable
private fun LocationTrackingButton(
    showLabels: Boolean,
    mapView: MapView,
    proximiteMeter: Double
) {
    var isTracking by remember { mutableStateOf(false) }
    val locationTracker = rememberLocationTracker(mapView, proximiteMeter)

    ControlButton(
        onClick = {
            isTracking = !isTracking
            if (isTracking) {
                locationTracker.startTracking()
            } else {
                locationTracker.stopTracking()
            }
        },
        icon = Icons.Default.LocationOn,
        contentDescription = if (isTracking) "Stop tracking" else "Start tracking",
        showLabels = showLabels,
        labelText = if (isTracking) "Stop tracking" else "Start tracking",
        containerColor = if (isTracking) Color(0xFF4CAF50) else Color(0xFF9C27B0)
    )
}

@Composable
private fun MenuButton(
    showLabels: Boolean,
    showMenu: Boolean,
    onShowMenuChange: (Boolean) -> Unit
) {
    ControlButton(
        onClick = { onShowMenuChange(!showMenu) },
        icon = if (showMenu) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
        contentDescription = if (showMenu) "Hide menu" else "Show menu",
        showLabels = showLabels,
        labelText = if (showMenu) "Hide" else "Options",
        containerColor = Color(0xFF3F51B5)
    )
}

