package com.example.Packages.Views._2LocationGpsClients.App.MainApp

import Z_MasterOfApps.Kotlin.Model.Extension.clientsDisponible
import Z_MasterOfApps.Kotlin.Model._ModelAppsFather
import Z_MasterOfApps.Kotlin.Model._ModelAppsFather.Companion.produitsFireBaseRef
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import android.util.Log
import com.example.c_serveur.R
import kotlinx.coroutines.tasks.await
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.infowindow.MarkerInfoWindow

class ViewModelExtensionMapsHandler(
    val viewModel: ViewModelInitApp,
    val produitsMainDataBase: MutableList<_ModelAppsFather.ProduitModel>,
    val modelAppsFather: _ModelAppsFather
) {
    suspend fun clearAllData(mapView: MapView?) {
        try {
            // 1. Clear UI
            mapView?.apply {
                overlays.clear()
                produitsMainDataBase.forEach { produit ->
                    produit.bonsVentDeCetteCota.forEach { bonVent ->
                        bonVent.clientInformations?.gpsLocation?.locationGpsMark?.also {
                            it.closeInfoWindow()
                            it.remove(this)
                            bonVent.clientInformations?.gpsLocation?.locationGpsMark = null
                        }
                    }
                }
                invalidate()
            }

            // 2. Clear Firebase data
            produitsFireBaseRef.get().await()?.children?.forEach { produitSnapshot ->
                try {
                    produitSnapshot
                        .child("historiqueBonsVents")
                        .children.forEach { bonVentSnapshot ->
                            bonVentSnapshot
                                .child("clientInformations/gpsLocation")
                                .ref
                                .removeValue()
                                .await()
                        }
                } catch (e: Exception) {
                    Log.e("FirebaseCleanup", "Error with product ${produitSnapshot.key}", e)
                }
            }
        } catch (e: Exception) {
            Log.e("FirebaseCleanup", "Cleanup failed", e)
            throw e
        }
    }
    fun onClickAddMarkerButton(
        mapView: MapView,
        onMarkerSelected: (Marker) -> Unit,
        showMarkerDetails: Boolean,
        markers: MutableList<Marker>,
    ) {
        val center = mapView.mapCenter
        val newID = modelAppsFather.clientsDisponible
            .maxOf { it.id } + 1
        val newnom = "Nouveau client *$newID"

        val newClient =
            _ModelAppsFather.ProduitModel.ClientBonVentModel.ClientInformations(
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

        product.historiqueBonsVents.add(newBonVent)

        newClient.gpsLocation.locationGpsMark?.let { marker ->
            markers.add(marker)
            mapView.overlays.add(marker)
            if (showMarkerDetails) marker.showInfoWindow()
        }
        mapView.invalidate()

        _ModelAppsFather.updateProduit(product, viewModel)
    }
}
