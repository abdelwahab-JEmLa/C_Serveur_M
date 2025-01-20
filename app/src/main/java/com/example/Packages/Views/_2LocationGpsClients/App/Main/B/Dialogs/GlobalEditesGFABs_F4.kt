package com.example.Packages.Views._2LocationGpsClients.App.Main.B.Dialogs

import android.Manifest
import android.content.Context
import android.location.LocationManager
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
import com.example.c_serveur.R
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.infowindow.MarkerInfoWindow
import kotlin.math.roundToInt

@Composable
fun MapControls(
    mapView: MapView,
    markers: MutableList<Marker>,
    showMarkerDetails: Boolean,
    onShowMarkerDetailsChange: (Boolean) -> Unit,
    onMarkerSelected: (Marker) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var showMenu by remember { mutableStateOf(false) }
    var showLabels by remember { mutableStateOf(true) }

    // États pour le drag
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

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
            // Menu principal
            if (showMenu) {
                Column(
                    modifier = Modifier.align(Alignment.BottomStart),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Bouton Ajouter Marqueur
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        FloatingActionButton(
                            onClick = {
                                val center = mapView.mapCenter
                                // Création du marqueur
                                Marker(mapView).apply {
                                    position = GeoPoint(center.latitude, center.longitude)
                                    title = "Nouveau point"
                                    snippet = "Point ajouté"
                                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                    infoWindow = MarkerInfoWindow(R.layout.marker_info_window, mapView)
                                    setOnMarkerClickListener { marker, _ ->
                                        onMarkerSelected(marker)
                                        if (showMarkerDetails) marker.showInfoWindow()
                                        true
                                    }
                                    markers.add(this)
                                    mapView.overlays.add(this)
                                    if (showMarkerDetails) showInfoWindow()
                                    mapView.invalidate()
                                }
                            },
                            modifier = Modifier.size(40.dp),
                            containerColor = Color(0xFF2196F3)
                        ) {
                            Icon(Icons.Default.Add, "Ajouter un marqueur")
                        }
                        if (showLabels) {
                            Text(
                                "Ajouter",
                                modifier = Modifier.background(Color(0xFF2196F3)).padding(4.dp),
                                color = Color.White
                            )
                        }
                    }

                    // Bouton Position Actuelle
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        FloatingActionButton(
                            onClick = {
                                scope.launch {
                                    if (ContextCompat.checkSelfPermission(
                                            context,
                                            Manifest.permission.ACCESS_FINE_LOCATION
                                        ) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                                        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                                        val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                                            ?: locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

                                        location?.let { loc ->
                                            mapView.controller.animateTo(GeoPoint(loc.latitude, loc.longitude))
                                        }
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
                                modifier = Modifier.background(Color(0xFF9C27B0)).padding(4.dp),
                                color = Color.White
                            )
                        }
                    }

                    // Bouton Afficher/Masquer Détails
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        FloatingActionButton(
                            onClick = { onShowMarkerDetailsChange(!showMarkerDetails) },
                            modifier = Modifier.size(40.dp),
                            containerColor = Color(0xFF009688)
                        ) {
                            Icon(Icons.Default.Info, "Détails")
                        }
                        if (showLabels) {
                            Text(
                                if (showMarkerDetails) "Masquer détails" else "Afficher détails",
                                modifier = Modifier.background(Color(0xFF009688)).padding(4.dp),
                                color = Color.White
                            )
                        }
                    }
                }
            }

            Column(
                modifier = Modifier.align(Alignment.BottomStart),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Bouton Labels (maintenant séparé et toujours visible)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    FloatingActionButton(
                        onClick = { showLabels = !showLabels },
                        modifier = Modifier.size(40.dp),
                        containerColor = Color(0xFF3F51B5)
                    ) {
                        Icon(Icons.Default.Info, if (showLabels) "Masquer labels" else "Afficher labels")
                    }
                    if (showLabels) {
                        Text(
                            if (showLabels) "Masquer labels" else "Afficher labels",
                            modifier = Modifier.background(Color(0xFF3F51B5)).padding(4.dp),
                            color = Color.White
                        )
                    }
                }

                // Bouton Menu Principal
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
                            if (showMenu) "Masquer menu" else "Afficher menu"
                        )
                    }
                    if (showLabels) {
                        Text(
                            if (showMenu) "Masquer" else "Options",
                            modifier = Modifier.background(Color(0xFF3F51B5)).padding(4.dp),
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}
