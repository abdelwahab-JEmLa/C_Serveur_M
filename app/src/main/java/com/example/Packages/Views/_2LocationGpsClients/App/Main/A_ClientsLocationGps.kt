// File: A_ClientsLocationGps.kt
package com.example.Packages.Views._2LocationGpsClients.App.Main

import Z_MasterOfApps.Kotlin.Model.Extension.clientsDisponible
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import android.Manifest
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.Packages.Views._2LocationGpsClients.App.Main.B.Dialogs.MapControls
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.infowindow.InfoWindow

class CustomMarkerInfoWindow(mapView: MapView) :
    InfoWindow(com.example.c_serveur.R.layout.marker_info_window, mapView) {

    override fun onOpen(item: Any?) {
        try {
            if (item is Marker) {
                // Setup title with null check
                mView.findViewById<android.widget.TextView>(
                    com.example.c_serveur.R.id.bubble_title
                )?.apply {
                    text = item.title
                }

                // Setup subdescription with null check
                mView.findViewById<android.widget.TextView>(
                    com.example.c_serveur.R.id.bubble_subdescription
                )?.apply {
                    text = item.snippet
                }

                // Setup image if needed
                mView.findViewById<android.widget.ImageView>(
                    com.example.c_serveur.R.id.bubble_image
                )?.apply {
                    visibility = android.view.View.GONE  // Hidden by default
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("InfoWindow", "Error opening info window", e)
        }
    }

    override fun onClose() {
        // Clean up any resources if needed
    }
}
@Composable
fun A_ClientsLocationGps(
    modifier: Modifier = Modifier,
    viewModelInitApp: ViewModelInitApp = viewModel()
) {
    val context = LocalContext.current

    var showMarkerDetails by remember { mutableStateOf(true) }
    var showNavigationDialog by remember { mutableStateOf(false) }
    var selectedMarker by remember { mutableStateOf<Marker?>(null) }
    val markers = remember { mutableStateListOf<Marker>() }

    // Show loading indicator while data is loading
    if (viewModelInitApp.isLoading) {
        Box(modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator(
                progress = { viewModelInitApp.loadingProgress },
                modifier = Modifier.align(Alignment.Center),
                trackColor = ProgressIndicatorDefaults.circularIndeterminateTrackColor,
            )
        }
        return
    }

    // Initialize map with proper configuration
    val mapView = remember {
        MapView(context).apply {
            Configuration.getInstance().load(
                context,
                context.getSharedPreferences("osmdroid", Context.MODE_PRIVATE)
            )
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
        }
    }

    // Create shared InfoWindow instance
    val sharedInfoWindow = remember(mapView) { CustomMarkerInfoWindow(mapView) }

    // Update markers when clients change
    LaunchedEffect(viewModelInitApp._modelAppsFather.clientsDisponible) {
        try {
            markers.clear()
            mapView.overlays.clear()

            viewModelInitApp._modelAppsFather.clientsDisponible
                .filter { it.gpsLocation.latitude != 0.0 && it.gpsLocation.longitude != 0.0 }
                .forEach { client ->
                    val marker = client.gpsLocation.locationGpsMark ?: Marker(mapView).also {
                        client.gpsLocation.locationGpsMark = it
                    }

                    marker.apply {
                        position = GeoPoint(client.gpsLocation.latitude, client.gpsLocation.longitude)
                        title = client.nom
                        snippet = if (client.statueDeBase.cUnClientTemporaire)
                            "Client temporaire" else "Client permanent"
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        infoWindow = sharedInfoWindow

                        // Set marker icon and color
                        ContextCompat.getDrawable(
                            context,
                            com.example.c_serveur.R.drawable.ic_location_on
                        )?.let { drawable ->
                            val wrappedDrawable = androidx.core.graphics.drawable.DrawableCompat
                                .wrap(drawable)
                                .mutate()
                            androidx.core.graphics.drawable.DrawableCompat.setTint(
                                wrappedDrawable,
                                Color(android.graphics.Color.parseColor(client.gpsLocation.couleur)).toArgb()
                            )
                            icon = wrappedDrawable
                        }

                        setOnMarkerClickListener { clickedMarker, _ ->
                            selectedMarker = clickedMarker
                            if (showMarkerDetails) {
                                InfoWindow.closeAllInfoWindowsOn(mapView)
                                clickedMarker.showInfoWindow()
                            }
                            showNavigationDialog = true
                            true
                        }
                    }

                    if (!markers.contains(marker)) {
                        markers.add(marker)
                        mapView.overlays.add(marker)
                    }
                }

            if (showMarkerDetails) {
                InfoWindow.closeAllInfoWindowsOn(mapView)
                markers.forEach { it.showInfoWindow() }
            }
            mapView.invalidate()
        } catch (e: Exception) {
            // Log error and handle gracefully
            android.util.Log.e("MapUpdate", "Error updating markers", e)
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        // Map view
        AndroidView(
            factory = { mapView },
            modifier = Modifier.fillMaxSize()
        ) { view ->
            try {
                if (ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == android.content.pm.PackageManager.PERMISSION_GRANTED
                ) {
                    val locationManager =
                        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                    val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                        ?: locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

                    location?.let {
                        view.controller.apply {
                            setCenter(GeoPoint(it.latitude, it.longitude))
                            setZoom(18.2)
                        }
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("Location", "Error getting location", e)
            }
        }

        // Center indicator
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(Color.Red, CircleShape)
                .align(Alignment.Center)
        )

        // Map controls
        if (viewModelInitApp._paramatersAppsViewModelModel.fabsVisibility) {
            MapControls(
                mapView = mapView,
                viewModelInitApp = viewModelInitApp,
                showMarkerDetails = showMarkerDetails,
                onShowMarkerDetailsChange = { show ->
                    showMarkerDetails = show
                    markers.forEach { if (show) it.showInfoWindow() else it.closeInfoWindow() }
                },
                onMarkerSelected = { marker ->
                    selectedMarker = marker
                    showNavigationDialog = true
                }
            )
        }

        // Navigation dialog
        if (showNavigationDialog && selectedMarker != null) {
            AlertDialog(
                onDismissRequest = { showNavigationDialog = false },
                title = { Text("Navigation") },
                text = { Text("Voulez-vous démarrer la navigation vers ce point ?") },
                confirmButton = {
                    Button(onClick = {
                        try {
                            selectedMarker?.let { marker ->
                                val uri = Uri.parse(
                                    "google.navigation:q=${marker.position.latitude},${marker.position.longitude}&mode=d"
                                )
                                val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                                    setPackage("com.google.android.apps.maps")
                                }
                                context.startActivity(intent)
                            }
                        } catch (e: Exception) {
                            android.util.Log.e("Navigation", "Error starting navigation", e)
                        }
                        showNavigationDialog = false
                    }) {
                        Text("Démarrer")
                    }
                },
                dismissButton = {
                    Button(onClick = { showNavigationDialog = false }) {
                        Text("Annuler")
                    }
                }
            )
        }
    }
}

