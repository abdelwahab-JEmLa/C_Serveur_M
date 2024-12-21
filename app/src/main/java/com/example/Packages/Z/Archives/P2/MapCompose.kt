package com.example.Packages.Z.Archives.P2

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.TextView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
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

@Preview()
@Composable
private fun PreviewMapComposeLight() {
    MaterialTheme(
        colorScheme = lightColorScheme()
    ) {
        MapCompose()
    }
}
/** Fonctions principales de l'application
 * Afficher la carte et gérer la position
 */

@Composable
fun MapCompose(
    modifier: Modifier = Modifier,
    viewModel: MapViewModel = viewModel()
) {
    // Configuration initiale de la carte et des états
    val context = LocalContext.current
    val mapView = remember { MapView(context) }
    val currentZoom by viewModel.currentZoom.collectAsState()
    var showMarkerDetails by remember { mutableStateOf(true) }
    val markers = remember { mutableStateListOf<Marker>() }
    var showNavigationDialog by remember { mutableStateOf(false) }
    var selectedMarker by remember { mutableStateOf<Marker?>(null) }

    // Fonction pour ouvrir Google Maps navigation
    fun openGoogleMapsNavigation(marker: Marker) {
        val uri = Uri.parse("google.navigation:q=${marker.position.latitude},${marker.position.longitude}&mode=d")
        val mapIntent = Intent(Intent.ACTION_VIEW, uri)
        mapIntent.setPackage("com.google.android.apps.maps")
        context.startActivity(mapIntent)
    }

    DisposableEffect(Unit) {
        onDispose {
            markers.forEach { it.closeInfoWindow() }
            mapView.overlays.clear()
        }
    }

    // Fonction pour mettre à jour l'affichage des marqueurs
    fun updateMarkersVisibility() {
        markers.forEach { currentMarker ->
            currentMarker.setOnMarkerClickListener { clickedMarker, _ ->
                selectedMarker = clickedMarker
                showNavigationDialog = true
                true
            }
            if (showMarkerDetails) {
                currentMarker.closeInfoWindow() // Ferme d'abord pour éviter les doublons
                currentMarker.showInfoWindow()
            } else {
                currentMarker.closeInfoWindow()
            }
        }
        mapView.invalidate()
    }

    // Obtenir la position initiale
    val initialLocation = remember {
        viewModel.getDefaultLocation()
    }

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        OpenStreetMapView(
            mapView = mapView,
            latitude = initialLocation.latitude,
            longitude = initialLocation.longitude,
            onZoomChanged = { zoom ->
                viewModel.updateZoom(zoom)
            }
        )

        // Point central
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(Color.Red, shape = CircleShape)
                .align(Alignment.Center)
        )

        // Affichage du niveau de zoom
        Surface(
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.TopEnd),
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
            shape = RoundedCornerShape(8.dp),
            tonalElevation = 4.dp
        ) {
            Text(
                text = "Zoom: %.1f".format(currentZoom),
                modifier = Modifier.padding(8.dp),
                style = MaterialTheme.typography.bodyMedium
            )
        }

        // Boutons de contrôle
        Column(
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.BottomEnd),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FloatingActionButton(
                onClick = { 
                    // Ajouter un marqueur à la position du point central
                    val center = mapView.mapCenter
                    val marker = Marker(mapView).apply {
                        position = GeoPoint(center.latitude, center.longitude)
                        title = "Nouveau point"
                        snippet = "Ce point a été ajouté"
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        infoWindow = CustomMarkerInfoWindow(mapView)
                        setOnMarkerClickListener { clickedMarker, _ ->
                            selectedMarker = clickedMarker
                            showNavigationDialog = true
                            if (showMarkerDetails) {
                                clickedMarker.showInfoWindow()
                            }
                            true
                        }
                    }
                    markers.add(marker)
                    mapView.overlays.add(marker)
                    if (showMarkerDetails) {
                        marker.showInfoWindow()
                    }
                    mapView.invalidate()
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Ajouter un marqueur"
                )
            }
            
            FloatingActionButton(
                onClick = {
                    viewModel.getDefaultLocation().let { loc ->
                        val geoPoint = GeoPoint(loc.latitude, loc.longitude)
                        mapView.controller.animateTo(geoPoint)
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Position actuelle"
                )
            }

            // Bouton pour afficher/masquer les détails des marqueurs
            FloatingActionButton(
                onClick = {
                    showMarkerDetails = !showMarkerDetails
                    updateMarkersVisibility()
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = if (showMarkerDetails) "Masquer les détails" else "Afficher les détails"
                )
            }
        }

        if (showNavigationDialog && selectedMarker != null) {
            AlertDialog(
                onDismissRequest = { showNavigationDialog = false },
                title = { Text("Navigation") },
                text = { Text("Voulez-vous démarrer la navigation vers ce point ?") },
                confirmButton = {
                    Button(
                        onClick = {
                            selectedMarker?.let { marker ->
                                openGoogleMapsNavigation(marker)
                            }
                            showNavigationDialog = false
                        }
                    ) {
                        Text("Démarrer la navigation")
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

@Composable
fun OpenStreetMapView(
    mapView: MapView,
    latitude: Double,
    longitude: Double,
    onZoomChanged: (Double) -> Unit,
    modifier: Modifier = Modifier
) {

    val context = LocalContext.current
    val customInfoWindow = remember { CustomMarkerInfoWindow(mapView) }

    DisposableEffect(context) {
        Configuration.getInstance().load(
            context,
            context.getSharedPreferences("osmdroid", Context.MODE_PRIVATE)
        )
        onDispose {
            customInfoWindow.close()
        }
    }

    mapView.setTileSource(TileSourceFactory.MAPNIK)
    mapView.setMultiTouchControls(true)

    AndroidView(
        modifier = modifier.fillMaxSize(),
        factory = { mapView }
    ) { view ->
        val mapController = view.controller

        // Définir le zoom et le centre initial
        val geoPoint = GeoPoint(latitude, longitude)
        mapController.setCenter(geoPoint)
        mapController.setZoom(18.2)

        // Définir l'écouteur de changement de zoom
        view.addMapListener(object : org.osmdroid.events.MapListener {
            override fun onScroll(event: org.osmdroid.events.ScrollEvent?): Boolean {
                return true
            }

            override fun onZoom(event: org.osmdroid.events.ZoomEvent?): Boolean {
                event?.let {
                    onZoomChanged(view.zoomLevelDouble)
                }
                return true
            }
        })

        // Ajouter un marqueur à la position actuelle
        val marker = Marker(view)
        marker.position = geoPoint
        marker.title = "Ma position actuelle"
        marker.snippet = "Ma position actuelle"
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        marker.isDraggable = false
        marker.setVisible(true)
        
        // Définir l'écouteur pour afficher la fenêtre d'information
        marker.setOnMarkerClickListener { clickedMarker, _ ->
            customInfoWindow.onOpen(clickedMarker)
            true
        }

        view.overlays.clear()
        view.overlays.add(marker)
        view.invalidate()
    }
}

// Fenêtre d'information personnalisée avec Jetpack Compose
class CustomMarkerInfoWindow(
    mapView: MapView
) : MarkerInfoWindow(R.layout.marker_info_window, mapView) {

    override fun onOpen(item: Any?) {
        super.onOpen(item)
        if (item is Marker) {
            // Définir le titre et la description dans la fenêtre d'information
            val titleView = mView.findViewById<TextView>(R.id.bubble_title)
            val descriptionView = mView.findViewById<TextView>(R.id.bubble_subdescription)
            
            titleView?.text = item.title
            descriptionView?.text = item.snippet
            
            // Mettre à jour la position de la fenêtre d'information
            val point = mMapView.projection.toPixels(item.position, null)
            val x = point.x - (mView.width / 2)
            val markerHeight = item.icon?.intrinsicHeight ?: 40 // Utiliser une valeur par défaut si l'icône n'est pas disponible
            val y = point.y - mView.height - markerHeight
            
            mView.x = x.toFloat()
            mView.y = y.toFloat()
        }
    }

    override fun close() {
        super.close()
    }
}
