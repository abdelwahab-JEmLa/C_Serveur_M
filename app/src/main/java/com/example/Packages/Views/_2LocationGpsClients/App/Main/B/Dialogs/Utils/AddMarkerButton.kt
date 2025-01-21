package com.example.Packages.Views._2LocationGpsClients.App.Main.B.Dialogs.Utils

import Z_MasterOfApps.Kotlin.Model.Extension.clientsDisponible
import Z_MasterOfApps.Kotlin.Model._ModelAppsFather
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.Packages.Views._2LocationGpsClients.App.Main.B.Dialogs.ControlButton
import com.example.c_serveur.R
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.infowindow.MarkerInfoWindow

@Composable
 fun AddMarkerButton(
    showLabels: Boolean,
    mapView: MapView,
    viewModelInitApp: ViewModelInitApp,
    markers: MutableList<Marker>,
    showMarkerDetails: Boolean,
    onMarkerSelected: (Marker) -> Unit
) {
    ControlButton(
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

            val product = viewModelInitApp.produitsMainDataBase.find { it.id == 0L }
                ?: _ModelAppsFather.ProduitModel(id = 0L).also {
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
        icon = Icons.Default.Add,
        contentDescription = "Add marker",
        showLabels = showLabels,
        labelText = "Add",
        containerColor = Color(0xFF2196F3)
    )
}
