package com.example.Packages.Views._2LocationGpsClients.App.Main

import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.c_serveur.R
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.infowindow.MarkerInfoWindow

private fun getCurrentLocation(context: Context): Location? {
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    return try {
        locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
    } catch (e: SecurityException) {
        null
    } ?: try {
        locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
    } catch (e: SecurityException) {
        null
    }
}
@Composable
fun A_ClientsLocationGps(
    modifier: Modifier = Modifier,
    viewModelInitApp: ViewModelInitApp = viewModel(),
) {
    val context = LocalContext.current
    val currentZoom by remember { mutableStateOf(18.2) }

    val mapView = remember { MapView(context) }
    val markers = remember { mutableStateListOf<Marker>() }
    var selectedMarker by remember { mutableStateOf<Marker?>(null) }
    var showNavigationDialog by remember { mutableStateOf(false) }
    var showMarkerDetails by remember { mutableStateOf(true) }

    // Configuration initiale
    DisposableEffect(context) {
        Configuration.getInstance().load(context, context.getSharedPreferences("osmdroid", Context.MODE_PRIVATE))
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setMultiTouchControls(true)

        onDispose {
            mapView.overlays.clear()
        }
    }

    // Fonction pour mettre à jour l'affichage des marqueurs
    fun updateMarkersVisibility() {
        markers.forEach { marker ->
            if (showMarkerDetails) marker.showInfoWindow() else marker.closeInfoWindow()
        }
        mapView.invalidate()
    }

    // Position initiale
    val initialLocation = remember { getCurrentLocation(context) ?:
    Location("default").apply {
        latitude = -34.0
        longitude = 151.0
    }
     }
    val geoPoint = GeoPoint(initialLocation.latitude, initialLocation.longitude)

    Box(modifier = modifier.fillMaxSize()) {
        // Carte
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { mapView }
        ) { view ->
            view.controller.apply {
                setCenter(geoPoint)
                setZoom(currentZoom)
            }
        }

        // Point central rouge
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(Color.Red, CircleShape)
                .align(Alignment.Center)
        )

        // Boutons de contrôle
        Column(
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.BottomEnd),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Ajouter marqueur
            FloatingActionButton(
                onClick = {
                    val center = mapView.mapCenter
                    val marker = Marker(mapView).apply {
                        position = GeoPoint(center.latitude, center.longitude)
                        title = "Nouveau point"
                        snippet = "Point ajouté"
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        infoWindow = MarkerInfoWindow(R.layout.marker_info_window, mapView)
                        setOnMarkerClickListener { clickedMarker, _ ->
                            selectedMarker = clickedMarker
                            showNavigationDialog = true
                            if (showMarkerDetails) clickedMarker.showInfoWindow()
                            true
                        }
                    }
                    markers.add(marker)
                    mapView.overlays.add(marker)
                    if (showMarkerDetails) marker.showInfoWindow()
                    mapView.invalidate()
                }
            ) {
                Icon(Icons.Default.Add, "Ajouter un marqueur")
            }

            // Position actuelle
            FloatingActionButton(
                onClick = {
                    getCurrentLocation(context)?.let { loc ->
                        mapView.controller.animateTo(GeoPoint(loc.latitude, loc.longitude))
                    }
                }
            ) {
                Icon(Icons.Default.LocationOn, "Position actuelle")
            }

            // Afficher/masquer détails
            FloatingActionButton(
                onClick = {
                    showMarkerDetails = !showMarkerDetails
                    updateMarkersVisibility()
                }
            ) {
                Icon(Icons.Default.Info, if (showMarkerDetails) "Masquer les détails" else "Afficher les détails")
            }
        }

        // Dialog de navigation
        if (showNavigationDialog && selectedMarker != null) {
            AlertDialog(
                onDismissRequest = { showNavigationDialog = false },
                title = { Text("Navigation") },
                text = { Text("Voulez-vous démarrer la navigation vers ce point ?") },
                confirmButton = {
                    Button(
                        onClick = {
                            selectedMarker?.let { marker ->
                                val uri = Uri.parse("google.navigation:q=${marker.position.latitude},${marker.position.longitude}&mode=d")
                                val mapIntent = Intent(Intent.ACTION_VIEW, uri).apply {
                                    setPackage("com.google.android.apps.maps")
                                }
                                context.startActivity(mapIntent)
                            }
                            showNavigationDialog = false
                        }
                    ) {
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
