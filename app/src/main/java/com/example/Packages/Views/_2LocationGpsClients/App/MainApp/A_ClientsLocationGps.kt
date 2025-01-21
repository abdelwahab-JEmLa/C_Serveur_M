package com.example.Packages.Views._2LocationGpsClients.App.MainApp

import Z_MasterOfApps.Kotlin.Model.Extension.clientsDisponible
import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import com.example.Packages.Views._2LocationGpsClients.App.MainApp.A.ViewModel.ChildViewModelOfFragment
import com.example.Packages.Views._2LocationGpsClients.App.MainApp.B.Dialogs.MapControls
import com.example.c_serveur.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.infowindow.MarkerInfoWindow

@Composable
fun A_ClientsLocationGps(
    modifier: Modifier = Modifier,
    viewModel: ChildViewModelOfFragment = viewModel(),
) {
    val viewModelInitApp = viewModel.viewModelHeadOfAll
    val context = LocalContext.current
    val currentZoom by remember { mutableStateOf(18.2) }
    val mapView = remember { MapView(context) }
    val markers = remember { mutableStateListOf<Marker>() }
    var selectedMarker by remember { mutableStateOf<Marker?>(null) }
    var showNavigationDialog by remember { mutableStateOf(false) }
    var showMarkerDetails by remember { mutableStateOf(true) }

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

        // Animate to position immediately
        mapView.controller.apply {
            setZoom(currentZoom)
            animateTo(GeoPoint(initialPosition.latitude, initialPosition.longitude))
        }

        // Configure map settings
        mapView.apply {
            setMultiTouchControls(true)
            setTileSource(TileSourceFactory.MAPNIK)
            controller.setCenter(GeoPoint(initialPosition.latitude, initialPosition.longitude))
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

    // Fonction d'extension pour créer un marqueur personnalisé
    fun createCustomMarkerDrawable(context: Context, color: Int): android.graphics.drawable.Drawable {
        // Créer un LayerDrawable pour combiner le cercle et l'icône
        val layers = arrayOf(
            // Cercle noir en arrière-plan
            android.graphics.drawable.GradientDrawable().apply {
                shape = android.graphics.drawable.GradientDrawable.OVAL
                setColor(android.graphics.Color.WHITE)
                setSize(40, 40) // Taille du cercle en pixels
            },
            // Icône du marqueur
            ContextCompat.getDrawable(context, R.drawable.ic_location_on)?.mutate()?.apply {
                setBounds(8, 8, 32, 32) // Position et taille de l'icône à l'intérieur du cercle
            }
        )

        return android.graphics.drawable.LayerDrawable(layers).apply {
            setLayerInset(0, 0, 0, 0, 0) // Pas d'inset pour le cercle
            setLayerInset(1, 4, 4, 4, 4) // Insets pour centrer l'icône
        }
    }

    LaunchedEffect(viewModelInitApp._modelAppsFather.clientsDisponible) {
        markers.clear()
        mapView.overlays.clear()

        viewModelInitApp._modelAppsFather.clientsDisponible.forEach { client ->
            client.gpsLocation.locationGpsMark?.let { existingMarker ->
                // Mise à jour du marqueur existant
                existingMarker.position = GeoPoint(
                    client.gpsLocation.latitude,
                    client.gpsLocation.longitude
                )
                existingMarker.title = client.nom
                existingMarker.snippet = if (client.statueDeBase.cUnClientTemporaire)
                    "Client temporaire" else "Client permanent"

                // Mise à jour de l'icône avec le cercle en arrière-plan
                val markerColor = Color(android.graphics.Color.parseColor(client.gpsLocation.couleur)).toArgb()
                existingMarker.icon = createCustomMarkerDrawable(context, markerColor)

                markers.add(existingMarker)
                mapView.overlays.add(existingMarker)
            } ?: run {
                // Création d'un nouveau marqueur
                Marker(mapView).apply {
                    position = GeoPoint(
                        client.gpsLocation.latitude,
                        client.gpsLocation.longitude
                    )
                    title = client.nom
                    snippet = if (client.statueDeBase.cUnClientTemporaire)
                        "Client temporaire" else "Client permanent"
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    infoWindow = MarkerInfoWindow(R.layout.marker_info_window, mapView)

                    // Application du nouveau style avec cercle en arrière-plan
                    val markerColor = Color(android.graphics.Color.parseColor(client.gpsLocation.couleur)).toArgb()
                    icon = createCustomMarkerDrawable(context, markerColor)

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

        if (viewModelInitApp._paramatersAppsViewModelModel.fabsVisibility) {
            MapControls(
                viewModelInitApp = viewModelInitApp,
                viewModel=viewModel,
                mapView = mapView,
                markers = markers,
                showMarkerDetails = showMarkerDetails,
                onShowMarkerDetailsChange = {
                    showMarkerDetails = it
                    markers.forEach { marker ->
                        if (showMarkerDetails) marker.showInfoWindow()
                        else marker.closeInfoWindow()
                    }
                    mapView.invalidate()
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
                    val uri = Uri.parse(
                        "google.navigation:q=${marker.position.latitude},${marker.position.longitude}&mode=d"
                    )
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

private data class MapPosition(
    val latitude: Double,
    val longitude: Double,
    val isInitialized: Boolean
)

private const val DEFAULT_LATITUDE = 36.7389350566438
private const val DEFAULT_LONGITUDE = 3.1720169070695476

// Update getCurrentLocation function to be suspend
private suspend fun getCurrentLocation(context: Context): Location? = withContext(Dispatchers.IO) {
    if (ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    ) {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return@withContext locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            ?: locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
    }
    return@withContext null
}
