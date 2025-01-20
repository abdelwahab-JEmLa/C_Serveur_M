package com.example.Packages.Views._2LocationGpsClients.App.Main.B.Dialogs

import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import Z_MasterOfApps.Z.Android.Actions._1.C_Serveur._1NavHost.Fragment_Id4.OnClickOn
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.KeyboardDoubleArrowUp
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
import com.example.Packages.Views._2LocationGpsClients.App.Main.Utils.LocationHandler
import com.example.c_serveur.R
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.infowindow.MarkerInfoWindow
import kotlin.math.roundToInt

// Updated GlobalEditesGFABs_F4.kt
@Composable
fun GroupedControleBoutons_F1(
    viewModelInitApp: ViewModelInitApp,
    mapView: MapView,
    markers: SnapshotStateList<Marker>,
    showMarkerDetails: Boolean,
    onShowMarkerDetailsChange: (Boolean) -> Unit,
    onMarkerSelected: (Marker) -> Unit,
    modifier: Modifier = Modifier,
) {
    var showOptions by remember { mutableStateOf(false) }
    var showLabels by remember { mutableStateOf(false) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }
    var clearDataClickCount by remember { mutableIntStateOf(0) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val locationHandler = remember { LocationHandler(context) }

    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier
                .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        offsetX += dragAmount.x
                        offsetY += dragAmount.y
                    }
                },
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Main FAB
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (showLabels) {
                    Text(
                        if (showOptions) "Masquer" else "Options",
                        modifier = Modifier
                            .background(Color(0xFF3F51B5))
                            .padding(4.dp),
                        color = Color.White
                    )
                }
                FloatingActionButton(
                    onClick = { showOptions = !showOptions; showLabels = true },
                    modifier = Modifier.size(40.dp),
                    containerColor = Color(0xFF3F51B5)
                ) {
                    Icon(
                        if (showOptions) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        null
                    )
                }
            }

            // Option FABs
            AnimatedVisibility(visible = showOptions) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    // Add Marker FAB
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        if (showLabels) {
                            Text(
                                "Ajouter",
                                modifier = Modifier
                                    .background(Color(0xFF2196F3))
                                    .padding(4.dp),
                                color = Color.White
                            )
                        }
                        FloatingActionButton(
                            onClick = {
                                val center = mapView.mapCenter
                                val marker = Marker(mapView).apply {
                                    position = GeoPoint(center.latitude, center.longitude)
                                    title = "Nouveau point"
                                    snippet = "Point ajouté"
                                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                    infoWindow =
                                        MarkerInfoWindow(R.layout.marker_info_window, mapView)
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
                    }

                    // Current Location FAB
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        if (showLabels) {
                            Text(
                                "Position",
                                modifier = Modifier
                                    .background(Color(0xFF9C27B0))
                                    .padding(4.dp),
                                color = Color.White
                            )
                        }
                        FloatingActionButton(
                            onClick = {
                                scope.launch {
                                    locationHandler.getCurrentLocation()?.let { loc ->
                                        mapView.controller.animateTo(
                                            GeoPoint(
                                                loc.latitude,
                                                loc.longitude
                                            )
                                        )
                                    }
                                }
                            },
                            modifier = Modifier.size(40.dp),
                            containerColor = Color(0xFF9C27B0)
                        ) {
                            Icon(Icons.Default.LocationOn, "Position actuelle")
                        }
                    }

                    // Show/Hide Details FAB
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        if (showLabels) {
                            Text(
                                if (showMarkerDetails) "Masquer détails" else "Afficher détails",
                                modifier = Modifier
                                    .background(Color(0xFF009688))
                                    .padding(4.dp),
                                color = Color.White
                            )
                        }
                        FloatingActionButton(
                            onClick = { onShowMarkerDetailsChange(!showMarkerDetails) },
                            modifier = Modifier.size(40.dp),
                            containerColor = Color(0xFF009688)
                        ) {
                            Icon(
                                Icons.Default.Info,
                                if (showMarkerDetails) "Masquer les détails" else "Afficher les détails"
                            )
                        }
                    }

                    // Clear Data FAB
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        if (showLabels) {
                            Text(
                                if (clearDataClickCount == 0) "Supprimer" else "Confirmer",
                                modifier = Modifier
                                    .background(Color(0xFF4CAF50))
                                    .padding(4.dp),
                                color = Color.White
                            )
                        }
                        FloatingActionButton(
                            onClick = {
                                if (clearDataClickCount == 0) clearDataClickCount++
                                else {
                                    OnClickOn(viewModelInitApp).onClickOnGlobalFABsButton_1()
                                    clearDataClickCount = 0
                                }
                            },
                            modifier = Modifier.size(40.dp),
                            containerColor = Color(0xFF4CAF50)
                        ) {
                            Icon(Icons.Default.Delete, null)
                        }
                    }

                    // Edit Position FAB
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        if (showLabels) {
                            Text(
                                "Position",
                                modifier = Modifier
                                    .background(Color(0xFFFF5722))
                                    .padding(4.dp),
                                color = Color.White
                            )
                        }
                        FloatingActionButton(
                            onClick = {
                                viewModelInitApp._paramatersAppsViewModelModel.visibilityClientEditePositionDialog =
                                    true
                            },
                            modifier = Modifier.size(40.dp),
                            containerColor = Color(0xFFFF5722)
                        ) {
                            Icon(Icons.Default.KeyboardDoubleArrowUp, null)
                        }
                    }
                }
            }
        }
    }
}
