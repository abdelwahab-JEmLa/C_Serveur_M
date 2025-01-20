package com.example.Packages.Views._2LocationGpsClients.App.Main.B.Dialogs

import android.Manifest
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.c_serveur.R
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.infowindow.MarkerInfoWindow

@Composable
fun SimpleMapControls(
    mapView: MapView,
    markers: MutableList<Marker>,
    showMarkerDetails: Boolean,
    onShowMarkerDetailsChange: (Boolean) -> Unit,
    onMarkerSelected: (Marker) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var showOptions by remember { mutableStateOf(false) }
    var showLabels by remember { mutableStateOf(true) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.BottomStart
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            if (showOptions) {
                // Actions des boutons définies comme des fonctions lambda typées
                val actions: List<Pair<Triple<androidx.compose.ui.graphics.vector.ImageVector, String, Color>, () -> Unit>> = listOf(
                    Triple(Icons.Default.Add, "Ajouter", Color(0xFF2196F3)) to {
                        val center = mapView.mapCenter
                        val marker = Marker(mapView).apply {
                            position = GeoPoint(center.latitude, center.longitude)
                            title = "Nouveau point"
                            snippet = "Point ajouté"
                            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                            infoWindow = MarkerInfoWindow(R.layout.marker_info_window, mapView)
                            setOnMarkerClickListener { m, _ ->
                                onMarkerSelected(m)
                                if (showMarkerDetails) m.showInfoWindow()
                                true
                            }
                            if (showMarkerDetails) showInfoWindow()
                        }
                        markers.add(marker)
                        mapView.invalidate()
                    },
                    Triple(Icons.Default.LocationOn, "Position", Color(0xFF9C27B0)) to {
                        scope.launch {
                            if (ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.ACCESS_FINE_LOCATION
                                ) == android.content.pm.PackageManager.PERMISSION_GRANTED
                            ) {
                                val locationManager =
                                    context.getSystemService(Context.LOCATION_SERVICE) as android.location.LocationManager
                                val location =
                                    locationManager.getLastKnownLocation(android.location.LocationManager.GPS_PROVIDER)
                                location?.let {
                                    mapView.controller.animateTo(GeoPoint(it.latitude, it.longitude))
                                }
                            }
                        }
                    },
                    Triple(
                        Icons.Default.Info,
                        if (showMarkerDetails) "Masquer" else "Afficher",
                        Color(0xFF009688)
                    ) to {
                        onShowMarkerDetailsChange(!showMarkerDetails)
                    },
                    Triple(
                        Icons.Default.Info,
                        if (showLabels) "Labels off" else "Labels on",
                        Color(0xFF3F51B5)
                    ) to {
                        showLabels = !showLabels
                    }
                )

                actions.forEach { (button, action) ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        FloatingActionButton(
                            onClick = action,
                            modifier = Modifier.size(40.dp),
                            containerColor = button.third
                        ) {
                            Icon(button.first, button.second)
                        }
                        if (showLabels) {
                            Text(
                                button.second,
                                modifier = Modifier
                                    .background(button.third)
                                    .padding(4.dp),
                                color = Color.White
                            )
                        }
                    }
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                FloatingActionButton(
                    onClick = { showOptions = !showOptions },
                    modifier = Modifier.size(40.dp),
                    containerColor = Color(0xFF3F51B5)
                ) {
                    Icon(
                        if (showOptions) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        if (showOptions) "Masquer" else "Options"
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
