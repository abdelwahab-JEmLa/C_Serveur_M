package com.example.Packages.Views._2LocationGpsClients.App.Main.B.Dialogs

import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import android.Manifest
import android.content.Context
import android.location.Location
import android.location.LocationManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.c_serveur.R
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.infowindow.MarkerInfoWindow
import kotlin.coroutines.resume
import kotlin.math.roundToInt

class LocationHandler(private val context: Context) {
    private val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    suspend fun getCurrentLocationDev(): Location? = suspendCancellableCoroutine { continuation ->
        try {
            if (!hasLocationPermission()) {
                continuation.resume(null)
                return@suspendCancellableCoroutine
            }

            // Try GPS provider first
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                try {
                    val lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    if (lastKnownLocation != null) {
                        continuation.resume(lastKnownLocation)
                        return@suspendCancellableCoroutine
                    }
                } catch (e: SecurityException) {
                    // Fall through to network provider
                }
            }

            // Try network provider as fallback
            if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                try {
                    val lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                    continuation.resume(lastKnownLocation)
                    return@suspendCancellableCoroutine
                } catch (e: SecurityException) {
                    continuation.resume(null)
                    return@suspendCancellableCoroutine
                }
            }

            continuation.resume(null)
        } catch (e: Exception) {
            continuation.resume(null)
        }
    }

    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == android.content.pm.PackageManager.PERMISSION_GRANTED
    }
}
// Updated GlobalEditesGFABs_F4.kt
@Composable
fun OptionesControleBoutons_F1(
    viewModelInitApp: ViewModelInitApp,
    mapView: MapView,
    markers: SnapshotStateList<Marker>,
    showMarkerDetails: Boolean,
    onShowMarkerDetailsChange: (Boolean) -> Unit,
    onMarkerSelected: (Marker) -> Unit,
    modifier: Modifier = Modifier,
) {
    var showOptions by remember { mutableStateOf(false) }
    var showLabels by remember { mutableStateOf(true) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }
    var clearDataClickCount by remember { mutableIntStateOf(0) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val locationHandler = remember { LocationHandler(context) }

    Box(
        modifier = modifier.fillMaxSize(),
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
            // Les boutons animés
            AnimatedVisibility(
                visible = showOptions,
                enter = slideInVertically(initialOffsetY = { 0 }) + expandVertically(expandFrom = Alignment.Bottom),
                exit = slideOutVertically(targetOffsetY = { 0 }) + shrinkVertically(shrinkTowards = Alignment.Bottom),
                modifier = Modifier.align(Alignment.BottomStart)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier.padding(bottom = 48.dp) // Espace pour le bouton fixe
                ) {
                    // Add Marker FAB
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
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
                                        onMarkerSelected(clickedMarker)
                                        if (showMarkerDetails) clickedMarker.showInfoWindow()
                                        true
                                    }
                                }
                                markers.add(marker)
                                mapView.overlays.add(marker)
                                if (showMarkerDetails) marker.showInfoWindow()
                                mapView.invalidate()
                            },
                            modifier = Modifier.size(40.dp),
                            containerColor = Color(0xFF2196F3)
                        ) {
                            Icon(Icons.Default.Add, "Ajouter un marqueur")
                        }
                        if (showLabels) {
                            Text(
                                "Ajouter",
                                modifier = Modifier
                                    .background(Color(0xFF2196F3))
                                    .padding(4.dp),
                                color = Color.White
                            )
                        }
                    }

                    // Current Location FAB
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        FloatingActionButton(
                            onClick = {
                                scope.launch {
                                    locationHandler.getCurrentLocationDev()?.let { loc ->
                                        mapView.controller.animateTo(
                                            GeoPoint(loc.latitude, loc.longitude)
                                        )
                                    }
                                }
                            },
                            modifier = Modifier.size(40.dp),
                            containerColor = Color(0xFF9C27B0)
                        ) {
                            Icon(Icons.Default.LocationOn, "Position actuelle")
                        }
                        if (showLabels) {
                            Text(
                                "Position",
                                modifier = Modifier
                                    .background(Color(0xFF9C27B0))
                                    .padding(4.dp),
                                color = Color.White
                            )
                        }
                    }

                    // Show/Hide Details FAB
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        FloatingActionButton(
                            onClick = { onShowMarkerDetailsChange(!showMarkerDetails) },
                            modifier = Modifier.size(40.dp),
                            containerColor = Color(0xFF009688)
                        ) {
                            Icon(Icons.Default.Info, null)
                        }
                        if (showLabels) {
                            Text(
                                if (showMarkerDetails) "Masquer détails" else "Afficher détails",
                                modifier = Modifier
                                    .background(Color(0xFF009688))
                                    .padding(4.dp),
                                color = Color.White
                            )
                        }
                    }

                    // Labels toggle button
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        FloatingActionButton(
                            onClick = { showLabels = !showLabels },
                            modifier = Modifier.size(40.dp),
                            containerColor = Color(0xFF3F51B5)
                        ) {
                            Icon(
                                if (showLabels) Icons.Default.Info else Icons.Default.Info,
                                contentDescription = if (showLabels) "Hide labels" else "Show labels"
                            )
                        }
                        if (showLabels) {
                            Text(
                                if (showLabels) "Masquer labels" else "Afficher labels",
                                modifier = Modifier
                                    .background(Color(0xFF3F51B5))
                                    .padding(4.dp),
                                color = Color.White
                            )
                        }
                    }

                    // Clear Data FAB
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        FloatingActionButton(
                            onClick = {
                                if (clearDataClickCount == 0) clearDataClickCount++
                                else {
                                    // Clear data logic here
                                }
                            },
                            modifier = Modifier.size(40.dp),
                            containerColor = Color(0xFF4CAF50)
                        ) {
                            Icon(Icons.Default.Delete, null)
                        }
                        if (showLabels) {
                            Text(
                                if (clearDataClickCount == 0) "Supprimer" else "Confirmer",
                                modifier = Modifier
                                    .background(Color(0xFF4CAF50))
                                    .padding(4.dp),
                                color = Color.White
                            )
                        }
                    }
                }
            }

            // Bouton d'options fixe
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.align(Alignment.BottomStart)
            ) {
                FloatingActionButton(
                    onClick = { showOptions = !showOptions },
                    modifier = Modifier.size(40.dp),
                    containerColor = Color(0xFF3F51B5)
                ) {
                    Icon(
                        if (showOptions) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (showOptions) "Hide options" else "Show options"
                    )
                }
                if (showLabels) {
                    Text(
                        if (showOptions) "Masquer" else "Options",
                        modifier = Modifier
                            .background(Color(0xFF3F51B5))
                            .padding(4.dp),
                        color = Color.White
                    )
                }
            }
        }
    }
}
private fun getCurrentLocationDev(context: Context): Location? {
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
