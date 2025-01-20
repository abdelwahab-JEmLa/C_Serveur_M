package com.example.Packages.Views._2LocationGpsClients.App.Main

import Z_MasterOfApps.Kotlin.Model.Extension.clientsDisponible
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import android.Manifest
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.infowindow.InfoWindow

class CustomMarkerInfoWindow(
    mapView: MapView
) : InfoWindow(com.example.c_serveur.R.layout.marker_info_window, mapView) {

    override fun onOpen(item: Any?) {
        try {
            // Fermer toutes les autres fenêtres d'info
            closeAllInfoWindowsOn(mMapView)

            // Vérifier que c'est bien un marker
            if (item !is Marker) return

            // Créer une nouvelle vue pour l'info window
            val inflater = mMapView.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            mView = inflater.inflate(com.example.c_serveur.R.layout.marker_info_window, null)

            // Mettre à jour le contenu
            val titleView = mView.findViewById<TextView>(com.example.c_serveur.R.id.bubble_title)
            val descView = mView.findViewById<TextView>(com.example.c_serveur.R.id.bubble_subdescription)
            val imageView = mView.findViewById<ImageView>(com.example.c_serveur.R.id.bubble_image)

            titleView?.text = item.title ?: ""
            descView?.text = item.snippet ?: ""
            imageView?.visibility = View.GONE

        } catch (e: Exception) {
            Log.e("InfoWindow", "Error opening InfoWindow", e)
        }
    }

    override fun onClose() {
        // Nettoyer les ressources si nécessaire
    }
}

@Composable
fun A_ClientsLocationGps(
    modifier: Modifier = Modifier,
    viewModelInitApp: ViewModelInitApp = viewModel()
) {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var showMarkerDetails by remember { mutableStateOf(true) }
    var showNavigationDialog by remember { mutableStateOf(false) }
    var selectedMarker by remember { mutableStateOf<Marker?>(null) }
    val markers = remember { mutableStateListOf<Marker>() }
    var mapInitialized by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Show loading indicator while initializing
    if (viewModelInitApp.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(48.dp),
                color = MaterialTheme.colorScheme.primary
            )
        }
        return
    }

    // Show error message if any
    errorMessage?.let { message ->
        AlertDialog(
            onDismissRequest = { errorMessage = null },
            title = { Text("Error") },
            text = { Text(message) },
            confirmButton = {
                Button(onClick = { errorMessage = null }) {
                    Text("OK")
                }
            }
        )
    }

    // Initialize map
    val mapView = remember {
        try {
            MapView(context).apply {
                Configuration.getInstance().load(
                    context,
                    context.getSharedPreferences("osmdroid", Context.MODE_PRIVATE)
                )
                setTileSource(TileSourceFactory.MAPNIK)
                setMultiTouchControls(true)
                mapInitialized = true
            }
        } catch (e: Exception) {
            Log.e("MapView", "Error initializing MapView", e)
            errorMessage = "Failed to initialize map: ${e.message}"
            null
        }
    }

    // Create shared InfoWindow instance
    val sharedInfoWindow = remember(mapView) {
        mapView?.let { CustomMarkerInfoWindow(it) }
    }

    // Update markers when clients change
    LaunchedEffect(viewModelInitApp._modelAppsFather.clientsDisponible) {
        try {
            mapView?.let { map ->
                markers.clear()
                map.overlays.clear()

                viewModelInitApp._modelAppsFather.clientsDisponible
                    .filter { it.gpsLocation.latitude != 0.0 && it.gpsLocation.longitude != 0.0 }
                    .forEach { client ->
                        val marker = client.gpsLocation.locationGpsMark ?: Marker(map).also {
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
                            try {
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
                            } catch (e: Exception) {
                                Log.e("Marker", "Error setting marker icon", e)
                            }

                            setOnMarkerClickListener { clickedMarker, _ ->
                                selectedMarker = clickedMarker
                                if (showMarkerDetails) {
                                    InfoWindow.closeAllInfoWindowsOn(map)
                                    clickedMarker.showInfoWindow()
                                }
                                showNavigationDialog = true
                                true
                            }
                        }

                        if (!markers.contains(marker)) {
                            markers.add(marker)
                            map.overlays.add(marker)
                        }
                    }

                if (showMarkerDetails) {
                    InfoWindow.closeAllInfoWindowsOn(map)
                    markers.forEach { it.showInfoWindow() }
                }
                map.invalidate()
            }
        } catch (e: Exception) {
            Log.e("MapUpdate", "Error updating markers", e)
            errorMessage = "Failed to update markers: ${e.message}"
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        // Map view
        mapView?.let { map ->
            AndroidView(
                factory = { map },
                modifier = Modifier.fillMaxSize()
            ) { view ->
                scope.launch {
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
                        Log.e("Location", "Error getting location", e)
                        errorMessage = "Failed to get location: ${e.message}"
                    }
                }
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
        if (viewModelInitApp._paramatersAppsViewModelModel.fabsVisibility && mapView != null) {
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
                            Log.e("Navigation", "Error starting navigation", e)
                            errorMessage = "Failed to start navigation: ${e.message}"
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
