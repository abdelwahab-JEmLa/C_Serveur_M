package com.example.Packages.Views._2LocationGpsClients.App.Main

import Z_MasterOfApps.Kotlin.Model.Extension.clientsDisponible
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import android.Manifest
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.util.Log
import android.widget.TextView
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
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
import com.example.c_serveur.R
import kotlinx.coroutines.CoroutineScope
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@Composable
fun A_ClientsLocationGps(
    modifier: Modifier = Modifier,
    viewModelInitApp: ViewModelInitApp = viewModel(),
) {
    // Loading state
    if (viewModelInitApp.isLoading) {
        LoadingIndicator(viewModelInitApp.loadingProgress)
        return
    }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val currentZoom by remember { mutableStateOf(18.2) }

    val mapView = remember { MapView(context) }
    val markers = remember { mutableStateListOf<Marker>() }
    var selectedMarker by remember { mutableStateOf<Marker?>(null) }
    var showNavigationDialog by remember { mutableStateOf(false) }
    var showMarkerDetails by remember { mutableStateOf(true) }
    var currentLocation by remember { mutableStateOf(getDefaultLocation()) }

    // Initialize map configuration
    InitializeMap(context, mapView)

    // Load client markers
    ClientMarkers(
        viewModelInitApp = viewModelInitApp,
        mapView = mapView,
        markers = markers,
        showMarkerDetails = showMarkerDetails,
        onMarkerSelected = { marker ->
            selectedMarker = marker
            showNavigationDialog = true
        }
    )

    // Track location
    LaunchedEffect(Unit) {
        getCurrentLocation(context)?.let { location ->
            currentLocation = location
        }
    }

    // Main map UI
    MapContent(
        modifier = modifier,
        mapView = mapView,
        currentLocation = currentLocation,
        currentZoom = currentZoom,
        scope = scope,
        viewModelInitApp = viewModelInitApp,
        showMarkerDetails = showMarkerDetails,
        onShowMarkerDetailsChange = { show ->
            showMarkerDetails = show
            updateMarkersVisibility(markers, show)
        },
        onMarkerSelected = { marker ->
            selectedMarker = marker
            showNavigationDialog = true
        }
    )

    // Navigation dialog
    if (showNavigationDialog && selectedMarker != null) {
        NavigationDialog(
            onDismiss = { showNavigationDialog = false },
            onConfirm = { marker ->
                launchGoogleNavigation(context, marker)
            },
            marker = selectedMarker!!
        )
    }
}
private fun createOrUpdateMarker(
    client: Z_MasterOfApps.Kotlin.Model._ModelAppsFather.ProduitModel.ClientBonVentModel.ClientInformations,
    context: Context,
    mapView: MapView,
    markers: MutableList<Marker>,
    showMarkerDetails: Boolean,
    onMarkerClick: (Marker) -> Unit
) {
    val markerIcon = ContextCompat.getDrawable(context, com.example.c_serveur.R.drawable.ic_location_on)?.mutate()

    client.gpsLocation.locationGpsMark?.let { existingMarker ->
        updateExistingMarker(existingMarker, client, markerIcon, mapView)
        if (!markers.contains(existingMarker)) {
            markers.add(existingMarker)
            mapView.overlays.add(existingMarker)
        }
    } ?: run {
        createNewMarker(
            client = client,
            mapView = mapView,
            markerIcon = markerIcon,
            markers = markers,
            showMarkerDetails = showMarkerDetails,
            onMarkerClick = onMarkerClick
        )
    }
}

private fun updateExistingMarker(
    marker: Marker,
    client: Z_MasterOfApps.Kotlin.Model._ModelAppsFather.ProduitModel.ClientBonVentModel.ClientInformations,
    markerIcon: android.graphics.drawable.Drawable?,
    mapView: MapView
) {
    marker.apply {
        position = GeoPoint(client.gpsLocation.latitude, client.gpsLocation.longitude)
        title = client.nom
        snippet = if (client.statueDeBase.cUnClientTemporaire) "Client temporaire" else "Client permanent"

        // Configurer l'InfoWindow
        infoWindow = CustomInfoWindow(mapView, client)

        // Setup icon
        setupMarkerIcon(this, markerIcon, client.gpsLocation.couleur)
    }
}

private fun createNewMarker(
    client: Z_MasterOfApps.Kotlin.Model._ModelAppsFather.ProduitModel.ClientBonVentModel.ClientInformations,
    mapView: MapView,
    markerIcon: android.graphics.drawable.Drawable?,
    markers: MutableList<Marker>,
    showMarkerDetails: Boolean,
    onMarkerClick: (Marker) -> Unit
) {
    Marker(mapView).apply {
        position = GeoPoint(client.gpsLocation.latitude, client.gpsLocation.longitude)
        title = client.nom
        snippet = if (client.statueDeBase.cUnClientTemporaire) "Client temporaire" else "Client permanent"
        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)

        // Configurer l'InfoWindow
        infoWindow = CustomInfoWindow(mapView, client)

        // Setup icon
        setupMarkerIcon(this, markerIcon, client.gpsLocation.couleur)

        setOnMarkerClickListener { marker, _ ->
            onMarkerClick(marker)
            if (showMarkerDetails) {
                marker.showInfoWindow()
            }
            true
        }

        client.gpsLocation.locationGpsMark = this
        markers.add(this)
        mapView.overlays.add(this)
    }
}

private fun setupMarkerIcon(
    marker: Marker,
    markerIcon: android.graphics.drawable.Drawable?,
    color: String
) {
    markerIcon?.let { drawable ->
        val wrappedDrawable = androidx.core.graphics.drawable.DrawableCompat.wrap(drawable).mutate()
        androidx.core.graphics.drawable.DrawableCompat.setTint(
            wrappedDrawable,
            Color(android.graphics.Color.parseColor(color)).toArgb()
        )
        marker.icon = wrappedDrawable
    }
}

// Nouvelle classe CustomInfoWindow pour personnaliser l'apparence de l'InfoWindow
class CustomInfoWindow(
    mapView: MapView,
    private val client: Z_MasterOfApps.Kotlin.Model._ModelAppsFather.ProduitModel.ClientBonVentModel.ClientInformations
) : org.osmdroid.views.overlay.infowindow.InfoWindow(R.layout.marker_info_window, mapView) {

    override fun onOpen(item: Any) {
        val marker = item as Marker

        // Récupérer les vues de votre layout
        val titleTextView = mView.findViewById<TextView>(R.id.bubble_title)
        val descriptionTextView = mView.findViewById<TextView>(R.id.bubble_description)

        // Mettre à jour le contenu
        titleTextView?.text = client.nom
        descriptionTextView?.text = if (client.statueDeBase.cUnClientTemporaire) {
            "Client temporaire"
        } else {
            "Client permanent"
        }
    }

    override fun onClose() {
        // Rien à faire ici
    }
}
@Composable
private fun LoadingIndicator(progress: Float) {
    Box(modifier = Modifier.fillMaxSize()) {
        CircularProgressIndicator(
            progress = { progress },
            modifier = Modifier.align(Alignment.Center),
            trackColor = ProgressIndicatorDefaults.circularIndeterminateTrackColor,
        )
    }
}

@Composable
private fun InitializeMap(context: Context, mapView: MapView) {
    DisposableEffect(context) {
        Configuration.getInstance()
            .load(context, context.getSharedPreferences("osmdroid", Context.MODE_PRIVATE))
        mapView.apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
        }

        onDispose {
            mapView.overlays.clear()
        }
    }
}

@Composable
private fun ClientMarkers(
    viewModelInitApp: ViewModelInitApp,
    mapView: MapView,
    markers: SnapshotStateList<Marker>,
    showMarkerDetails: Boolean,
    onMarkerSelected: (Marker) -> Unit
) {
    val context = LocalContext.current

    LaunchedEffect(viewModelInitApp._modelAppsFather.clientsDisponible) {
        markers.clear()
        mapView.overlays.clear()

        viewModelInitApp._modelAppsFather.clientsDisponible
            .filter { client ->
                client.gpsLocation.latitude != 0.0 &&
                        client.gpsLocation.longitude != 0.0
            }
            .forEach { client ->
                createOrUpdateMarker(
                    client = client,
                    context = context,
                    mapView = mapView,
                    markers = markers,
                    showMarkerDetails = showMarkerDetails,
                    onMarkerClick = onMarkerSelected
                )
            }

        if (showMarkerDetails) {
            markers.forEach { it.showInfoWindow() }
        }
        mapView.invalidate()
    }
}

@Composable
private fun MapContent(
    modifier: Modifier,
    mapView: MapView,
    currentLocation: Location,
    currentZoom: Double,
    scope: CoroutineScope,
    viewModelInitApp: ViewModelInitApp,
    showMarkerDetails: Boolean,
    onShowMarkerDetailsChange: (Boolean) -> Unit,
    onMarkerSelected: (Marker) -> Unit
) {
    Box(modifier = modifier.fillMaxSize()) {
        // Map view
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { mapView }
        ) { view ->
            view.controller.apply {
                setCenter(GeoPoint(currentLocation.latitude, currentLocation.longitude))
                setZoom(currentZoom)
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
                onShowMarkerDetailsChange = onShowMarkerDetailsChange,
                onMarkerSelected = onMarkerSelected
            )
        }
    }
}


private fun setupMarkerIcon(
    marker: Marker,
    markerIcon: android.graphics.drawable.Drawable?,
    iconBackground: android.graphics.drawable.Drawable?,
    color: String
) {
    markerIcon?.let { drawable ->
        val wrappedDrawable = androidx.core.graphics.drawable.DrawableCompat.wrap(drawable).mutate()
        androidx.core.graphics.drawable.DrawableCompat.setTint(
            wrappedDrawable,
            Color(android.graphics.Color.parseColor(color)).toArgb()
        )
        marker.icon = wrappedDrawable
    }
}
private fun updateExistingMarker(
    marker: Marker,
    client: Z_MasterOfApps.Kotlin.Model._ModelAppsFather.ProduitModel.ClientBonVentModel.ClientInformations,
    markerIcon: android.graphics.drawable.Drawable?,
    iconBackground: android.graphics.drawable.Drawable?
) {
    marker.position = GeoPoint(client.gpsLocation.latitude, client.gpsLocation.longitude)
    marker.title = client.nom
    marker.snippet = if (client.statueDeBase.cUnClientTemporaire) "Client temporaire" else "Client permanent"

    // Set up layered marker icon with background
    setupMarkerIcon(marker, markerIcon, iconBackground, client.gpsLocation.couleur)
}

private fun createNewMarker(
    client: Z_MasterOfApps.Kotlin.Model._ModelAppsFather.ProduitModel.ClientBonVentModel.ClientInformations,
    mapView: MapView,
    markerIcon: android.graphics.drawable.Drawable?,
    iconBackground: android.graphics.drawable.Drawable?,
    markers: MutableList<Marker>,
    showMarkerDetails: Boolean,
    onMarkerClick: (Marker) -> Unit
) {
    Marker(mapView).apply {
        position = GeoPoint(client.gpsLocation.latitude, client.gpsLocation.longitude)
        title = client.nom
        snippet = if (client.statueDeBase.cUnClientTemporaire) "Client temporaire" else "Client permanent"
        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)

        // Set up layered marker icon with background
        setupMarkerIcon(this, markerIcon, iconBackground, client.gpsLocation.couleur)

        setOnMarkerClickListener { marker, _ ->
            onMarkerClick(marker)
            if (showMarkerDetails) marker.showInfoWindow()
            true
        }

        client.gpsLocation.locationGpsMark = this
        markers.add(this)
        mapView.overlays.add(this)
    }
}



private fun updateMarkersVisibility(markers: List<Marker>, showDetails: Boolean) {
    markers.forEach { marker ->
        if (showDetails) marker.showInfoWindow() else marker.closeInfoWindow()
    }
}

private fun launchGoogleNavigation(context: Context, marker: Marker) {
    val uri = Uri.parse(
        "google.navigation:q=${marker.position.latitude},${marker.position.longitude}&mode=d"
    )
    val mapIntent = Intent(Intent.ACTION_VIEW, uri).apply {
        setPackage("com.google.android.apps.maps")
    }
    context.startActivity(mapIntent)
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

private fun logLoadingState(isLoading: Boolean, progress: Float) {
    Log.d(TAG, "Loading State: isLoading=$isLoading, progress=${progress * 100}%")
}
