package com.example.Packages.Views._2LocationGpsClients.App.Main.B.Dialogs

import Z_MasterOfApps.Kotlin.Model._ModelAppsFather
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import android.Manifest
import android.content.Context
import android.location.LocationManager
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
    viewModelInitApp: ViewModelInitApp,
    markers: MutableList<Marker>,
    showMarkerDetails: Boolean,
    onShowMarkerDetailsChange: (Boolean) -> Unit,
    onMarkerSelected: (Marker) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var showMenu by remember { mutableStateOf(false) }
    var showLabels by remember { mutableStateOf(true) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomEnd
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
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (showMenu) {
                    // Bouton Ajouter Marqueur
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        FloatingActionButton(
                            onClick = {
                                val center = mapView.mapCenter
                                val newClient = _ModelAppsFather.ProduitModel.ClientBonVentModel.ClientInformations(
                                    id = System.currentTimeMillis(),
                                    nom = "Nouveau client"
                                ).apply {
                                    statueDeBase.cUnClientTemporaire = true
                                    gpsLocation.apply {
                                        latitude = center.latitude
                                        longitude = center.longitude
                                        title = "Nouveau client"
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

                                val product = viewModelInitApp.produitsMainDataBase.find { it.id == 0L }
                                    ?: _ModelAppsFather.ProduitModel(id = 0L).also {
                                        viewModelInitApp.produitsMainDataBase.add(it)
                                    }

                                product.bonsVentDeCetteCota.add(
                                    _ModelAppsFather.ProduitModel.ClientBonVentModel(
                                        vid = System.currentTimeMillis(),
                                        init_clientInformations = newClient
                                    )
                                )

                                newClient.gpsLocation.locationGpsMark?.let { marker ->
                                    markers.add(marker)
                                    mapView.overlays.add(marker)
                                    if (showMarkerDetails) marker.showInfoWindow()
                                }
                                mapView.invalidate()
                                _ModelAppsFather.updateProduit(product, viewModelInitApp)
                            },
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(Icons.Default.Add, "Ajouter")
                        }
                        if (showLabels) Text("Ajouter", modifier = Modifier.padding(4.dp))
                    }

                    // Bouton Position
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        FloatingActionButton(
                            onClick = {
                                scope.launch {
                                    if (ContextCompat.checkSelfPermission(
                                            context,
                                            Manifest.permission.ACCESS_FINE_LOCATION
                                        ) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                                        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                                        locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                                            ?.let { loc -> mapView.controller.animateTo(GeoPoint(loc.latitude, loc.longitude)) }
                                    }
                                }
                            },
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(Icons.Default.LocationOn, "Position")
                        }
                        if (showLabels) Text("Position", modifier = Modifier.padding(4.dp))
                    }

                    // Bouton Détails
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        FloatingActionButton(
                            onClick = { onShowMarkerDetailsChange(!showMarkerDetails) },
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(Icons.Default.Info, "Détails")
                        }
                        if (showLabels) Text(
                            if (showMarkerDetails) "Masquer détails" else "Afficher détails",
                            modifier = Modifier.padding(4.dp)
                        )
                    }
                }

                // Boutons toujours visibles
                Row(verticalAlignment = Alignment.CenterVertically) {
                    FloatingActionButton(
                        onClick = { showMenu = !showMenu },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            if (showMenu) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            "Menu"
                        )
                    }
                    if (showLabels) Text(
                        if (showMenu) "Masquer" else "Options",
                        modifier = Modifier.padding(4.dp)
                    )
                }
            }
        }
    }
}
