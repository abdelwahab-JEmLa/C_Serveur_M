package com.example.Packages.Views._2LocationGpsClients.App.Main

import Z_MasterOfApps.Kotlin.Model.Extension.clientsDisponible
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import android.Manifest
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.Packages.Views._2LocationGpsClients.App.Main.B.Dialogs.MapControls
import com.example.c_serveur.R
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.infowindow.MarkerInfoWindow
import kotlin.math.roundToInt

@Composable
fun A_ClientsLocationGps(
    modifier: Modifier = Modifier,
    viewModelInitApp: ViewModelInitApp = viewModel(),
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val currentZoom by remember { mutableStateOf(18.2) }

    val mapView = remember { MapView(context) }
    val markers = remember { mutableStateListOf<Marker>() }
    var selectedMarker by remember { mutableStateOf<Marker?>(null) }
    var showNavigationDialog by remember { mutableStateOf(false) }
    var showMarkerDetails by remember { mutableStateOf(true) }

    var currentLocation by remember { mutableStateOf(getDefaultLocation()) }

    // Load client markers
    LaunchedEffect(viewModelInitApp._modelAppsFather.clientsDisponible) {
        markers.clear()
        mapView.overlays.clear()

        // Create marker icon using VectorDrawableCompat
        val markerDrawable = ContextCompat.getDrawable(context, R.drawable.ic_location_on)?.mutate()

        viewModelInitApp._modelAppsFather.clientsDisponible.forEach { client ->
            client.gpsLocation.locationGpsMark?.let { existingMarker ->
                // If marker already exists, update its position
                existingMarker.position = GeoPoint(
                    client.gpsLocation.latitude,
                    client.gpsLocation.longitude
                )
                existingMarker.title = client.nom
                existingMarker.snippet = if (client.statueDeBase.cUnClientTemporaire) "Client temporaire" else "Client permanent"
                markers.add(existingMarker)
                mapView.overlays.add(existingMarker)
            } ?: run {
                // Create new marker if it doesn't exist
                Marker(mapView).apply {
                    position = GeoPoint(
                        client.gpsLocation.latitude,
                        client.gpsLocation.longitude
                    )
                    title = client.nom
                    snippet = if (client.statueDeBase.cUnClientTemporaire) "Client temporaire" else "Client permanent"
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    infoWindow = MarkerInfoWindow(R.layout.marker_info_window, mapView)

                    // Set the icon using the vector drawable
                    markerDrawable?.let { drawable ->
                        // Create a wrapped drawable that we can tint
                        val wrappedDrawable = DrawableCompat.wrap(drawable).mutate()
                        DrawableCompat.setTint(
                            wrappedDrawable,
                            Color(android.graphics.Color.parseColor(client.gpsLocation.couleur)).toArgb()
                        )
                        icon = wrappedDrawable
                    }

                    setOnMarkerClickListener { marker, _ ->
                        selectedMarker = marker
                        showNavigationDialog = true
                        if (showMarkerDetails) marker.showInfoWindow()
                        true
                    }
                    client.gpsLocation.locationGpsMark = this
                    markers.add(this)
                    mapView.overlays.add(this)
                }
            }
        }

        if (showMarkerDetails) {
            markers.forEach { it.showInfoWindow() }
        }
        mapView.invalidate()
    }

    // Effect for loading initial position
    LaunchedEffect(Unit) {
        val location = getCurrentLocation(context)
        if (location != null) {
            currentLocation = location
        }
    }

    // Initial configuration
    DisposableEffect(context) {
        Configuration.getInstance()
            .load(context, context.getSharedPreferences("osmdroid", Context.MODE_PRIVATE))
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setMultiTouchControls(true)

        onDispose {
            mapView.overlays.clear()
        }
    }

    // Function to update markers visibility
    fun updateMarkersVisibility() {
        markers.forEach { marker ->
            if (showMarkerDetails) marker.showInfoWindow() else marker.closeInfoWindow()
        }
        mapView.invalidate()
    }

    val geoPoint = GeoPoint(currentLocation.latitude, currentLocation.longitude)

    Box(modifier = modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { mapView }
        ) { view ->
            scope.launch {
                view.controller.apply {
                    setCenter(geoPoint)
                    setZoom(currentZoom)
                }
            }
        }

        Box(
            modifier = Modifier
                .size(8.dp)
                .background(Color.Red, CircleShape)
                .align(Alignment.Center)
        )

        if (viewModelInitApp._paramatersAppsViewModelModel.fabsVisibility) {
            MapControls(
                viewModelInitApp = viewModelInitApp,
                mapView = mapView,
                markers = markers,
                showMarkerDetails = showMarkerDetails,
                onShowMarkerDetailsChange = {
                    showMarkerDetails = it
                    updateMarkersVisibility()
                },
                onMarkerSelected = {
                    selectedMarker = it
                    showNavigationDialog = true
                }
            )
        }

        if (showNavigationDialog && selectedMarker != null) {
            NavigationDialog(
                onDismiss = { showNavigationDialog = false },
                onConfirm = { marker ->
                    val uri = Uri.parse("google.navigation:q=${marker.position.latitude},${marker.position.longitude}&mode=d")
                    val mapIntent = Intent(Intent.ACTION_VIEW, uri).apply {
                        setPackage("com.google.android.apps.maps")
                    }
                    context.startActivity(mapIntent)
                },
                marker = selectedMarker!!
            )
        }
    }
}

private fun getDefaultLocation() = Location("default").apply {
    latitude = -34.0
    longitude = 151.0
}

private fun getCurrentLocation(context: Context): Location? {
    return if (ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED
    ) {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            ?: locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
    } else null
}
@Composable
private fun NavigationDialog(
    onDismiss: () -> Unit,
    onConfirm: (Marker) -> Unit,
    marker: Marker
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Navigation") },
        text = { Text("Voulez-vous démarrer la navigation vers ce point ?") },
        confirmButton = {
            Button(onClick = { onConfirm(marker); onDismiss() }) {
                Text("Démarrer")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Annuler")
            }
        }
    )
}
