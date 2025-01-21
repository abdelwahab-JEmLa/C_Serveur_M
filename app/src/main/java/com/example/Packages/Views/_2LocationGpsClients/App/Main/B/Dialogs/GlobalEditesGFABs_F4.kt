package com.example.Packages.Views._2LocationGpsClients.App.Main.B.Dialogs

import Z_MasterOfApps.Kotlin.Model.Extension.clientsDisponible
import Z_MasterOfApps.Kotlin.Model._ModelAppsFather
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import android.Manifest
import android.content.Context
import android.location.Location
import android.location.LocationManager
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.Packages.Views._2LocationGpsClients.App.Main.Utils.rememberLocationTracker
import com.example.c_serveur.R
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.infowindow.MarkerInfoWindow
import kotlin.math.roundToInt


@Composable
fun NearbyMarkersDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    markers: List<Marker>,
    currentLocation: Location?,
    proxim: Double,
    mapView: MapView,
    viewModelInitApp: ViewModelInitApp
) {
    if (showDialog) {
        val referenceLocation = if (currentLocation != null) {
            currentLocation
        } else {
            val mapCenter = mapView.mapCenter
            Location("map_center").apply {
                latitude = mapCenter.latitude
                longitude = mapCenter.longitude
            }
        }

        val nearbyMarkers = markers.filter { marker ->
            val markerLocation = Location("marker").apply {
                latitude = marker.position.latitude
                longitude = marker.position.longitude
            }
            referenceLocation.distanceTo(markerLocation) <= proxim
        }

        var showEditDialog by remember { mutableStateOf(false) }
        var selectedMarker by remember { mutableStateOf<Marker?>(null) }
        var editedName by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Markers Within $proxim Meters") },
            text = {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 300.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(nearbyMarkers) { marker ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Row(
                                modifier = Modifier.padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = marker.title ?: "Unnamed Location",
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = marker.snippet ?: "",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                                IconButton(
                                    onClick = {
                                        selectedMarker = marker
                                        editedName = marker.title ?: ""
                                        showEditDialog = true
                                    }
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit name")
                                }
                            }
                        }
                    }
                    if (nearbyMarkers.isEmpty()) {
                        item {
                            Text("No markers found within $proxim meters")
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = onDismiss) {
                    Text("Close")
                }
            }
        )

        if (showEditDialog && selectedMarker != null) {
            AlertDialog(
                onDismissRequest = { showEditDialog = false },
                title = { Text("Edit Name") },
                text = {
                    OutlinedTextField(
                        value = editedName,
                        onValueChange = { editedName = it },
                        label = { Text("Location Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            selectedMarker?.let { marker ->
                                // Update marker title
                                marker.title = editedName

                                // Find and update the corresponding client in the database
                                val client = viewModelInitApp._modelAppsFather.clientsDisponible
                                    .find { it.gpsLocation.locationGpsMark == marker }

                                client?.let { foundClient ->
                                    // Update client name
                                    foundClient.nom = editedName

                                    // Find and update the product containing this client
                                    val product = viewModelInitApp.produitsMainDataBase.find { produit ->
                                        produit.bonsVentDeCetteCota.any { bonVent ->
                                            bonVent.clientInformations?.id == foundClient.id
                                        }
                                    }

                                    product?.let { foundProduct ->
                                        // Update the product in the database
                                        _ModelAppsFather.updateProduit(foundProduct, viewModelInitApp)
                                    }
                                }

                                mapView.invalidate()
                            }
                            showEditDialog = false
                        }
                    ) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showEditDialog = false }
                    ) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

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
                    // Nearby markers button
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        FloatingActionButton(
                            onClick = { showNearbyMarkersDialog = true },
                            modifier = Modifier.size(40.dp),
                            containerColor = Color(0xFFFF5722)
                        ) {
                            Icon(Icons.Default.NearMe, "Show nearby markers")
                        }
                        if (showLabels) {
                            Text(
                                "Nearby",
                                modifier = Modifier.background(Color(0xFFFF5722)).padding(4.dp),
                                color = Color.White
                            )
                        }
                    }

                    // Add marker button
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        FloatingActionButton(
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
                            modifier = Modifier.size(40.dp),
                            containerColor = Color(0xFF2196F3)
                        ) {
                            Icon(Icons.Default.Add, "Add marker")
                        }
                        if (showLabels) {
                            Text(
                                "Add",
                                modifier = Modifier
                                    .background(Color(0xFF2196F3))
                                    .padding(4.dp),
                                color = Color.White
                            )
                        }
                    }

                    // Location tracking toggle
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        var isTracking by remember { mutableStateOf(false) }
                        val locationTracker = rememberLocationTracker(mapView, proximiteMeter)

                        FloatingActionButton(
                            onClick = {
                                isTracking = !isTracking
                                if (isTracking) {
                                    locationTracker.startTracking()
                                } else {
                                    locationTracker.stopTracking()
                                }
                            },
                            modifier = Modifier.size(40.dp),
                            containerColor = if (isTracking) Color(0xFF4CAF50) else Color(0xFF9C27B0)
                        ) {
                            Icon(
                                Icons.Default.LocationOn,
                                contentDescription = if (isTracking) "Stop tracking" else "Start tracking"
                            )
                        }
                        if (showLabels) {
                            Text(
                                if (isTracking) "Stop tracking" else "Start tracking",
                                modifier = Modifier
                                    .background(if (isTracking) Color(0xFF4CAF50) else Color(0xFF9C27B0))
                                    .padding(4.dp),
                                color = Color.White
                            )
                        }
                    }

                    // Show/Hide Details button
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        FloatingActionButton(
                            onClick = { onShowMarkerDetailsChange(!showMarkerDetails) },
                            modifier = Modifier.size(40.dp),
                            containerColor = Color(0xFF009688)
                        ) {
                            Icon(Icons.Default.Info, "Details")
                        }
                        if (showLabels) {
                            Text(
                                if (showMarkerDetails) "Hide details" else "Show details",
                                modifier = Modifier.background(Color(0xFF009688)).padding(4.dp),
                                color = Color.White
                            )
                        }
                    }
                }

                // Always visible controls
                // Labels button
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    FloatingActionButton(
                        onClick = { showLabels = !showLabels },
                        modifier = Modifier.size(40.dp),
                        containerColor = Color(0xFF3F51B5)
                    ) {
                        Icon(Icons.Default.Info, if (showLabels) "Hide labels" else "Show labels")
                    }
                    if (showLabels) {
                        Text(
                            if (showLabels) "Hide labels" else "Show labels",
                            modifier = Modifier.background(Color(0xFF3F51B5)).padding(4.dp),
                            color = Color.White
                        )
                    }
                }

                // Main menu button
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    FloatingActionButton(
                        onClick = { showMenu = !showMenu },
                        modifier = Modifier.size(40.dp),
                        containerColor = Color(0xFF3F51B5)
                    ) {
                        Icon(
                            if (showMenu) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            if (showMenu) "Hide menu" else "Show menu"
                        )
                    }
                    if (showLabels) {
                        Text(
                            if (showMenu) "Hide" else "Options",
                            modifier = Modifier.background(Color(0xFF3F51B5)).padding(4.dp),
                            color = Color.White
                        )
                    }
                }
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
