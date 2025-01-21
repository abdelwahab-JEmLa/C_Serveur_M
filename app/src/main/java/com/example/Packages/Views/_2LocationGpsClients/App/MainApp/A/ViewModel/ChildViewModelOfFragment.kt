// ParamatersAppsViewModel.kt
package com.example.Packages.Views._2LocationGpsClients.App.MainApp.A.ViewModel

import Z_MasterOfApps.Kotlin.Model.Extension.clientsDisponible
import Z_MasterOfApps.Kotlin.Model._ModelAppsFather
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.c_serveur.R
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.infowindow.MarkerInfoWindow

class ChildViewModelOfFragment(val viewModelHeadOfAll: ViewModelInitApp) : ViewModel() {
    val _modelAppsFather = viewModelHeadOfAll._modelAppsFather
    val produitsMainDataBase = viewModelHeadOfAll._modelAppsFather.produitsMainDataBase

    fun onClickAddMarkerButton(
        mapView: MapView,
        onMarkerSelected: (Marker) -> Unit,
        showMarkerDetails: Boolean,
        markers: MutableList<Marker>
    ) {
        val center = mapView.mapCenter
        val newID = _modelAppsFather.clientsDisponible
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

        val product = produitsMainDataBase.find { it.id == 0L }
            ?: _ModelAppsFather.ProduitModel(id = 0L).also {
                produitsMainDataBase.add(it)
            }

        product.bonsVentDeCetteCota.add(newBonVent)

        newClient.gpsLocation.locationGpsMark?.let { marker ->
            markers.add(marker)
            mapView.overlays.add(marker)
            if (showMarkerDetails) marker.showInfoWindow()
        }
        mapView.invalidate()

        _ModelAppsFather.updateProduit(product, viewModelProduits = viewModelHeadOfAll)
    }
}
