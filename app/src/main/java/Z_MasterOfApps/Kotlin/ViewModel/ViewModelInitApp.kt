package Z_MasterOfApps.Kotlin.ViewModel

import Z_MasterOfApps.Kotlin.Model.Extension.clientsDisponible
import Z_MasterOfApps.Kotlin.Model._ModelAppsFather
import Z_MasterOfApps.Kotlin.Model._ModelAppsFather.Companion.produitsFireBaseRef
import Z_MasterOfApps.Z_AppsFather.Kotlin._1.Model.ParamatersAppsModel
import Z_MasterOfApps.Z_AppsFather.Kotlin._3.Init.A_LoadFireBase.LoadFromFirebaseProduits
import Z_MasterOfApps.Z_AppsFather.Kotlin._3.Init.CreeDepuitAncienDataBases
import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.c_serveur.R
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.infowindow.MarkerInfoWindow

@SuppressLint("SuspiciousIndentation")
class ViewModelInitApp : ViewModel() {
    var _paramatersAppsViewModelModel by mutableStateOf(ParamatersAppsModel())
    var _modelAppsFather by mutableStateOf(_ModelAppsFather())
    var mapViewVM by mutableStateOf<MapView?>(null)
        private set

    val modelAppsFather: _ModelAppsFather get() = _modelAppsFather
    val produitsMainDataBase = _modelAppsFather.produitsMainDataBase

    var isLoading by mutableStateOf(false)
    var loadingProgress by mutableFloatStateOf(0f)

    fun initializeMapView(context: Context): MapView {
        return MapView(context).also {
            mapViewVM = it
        }
    }

    fun clearAllData(context: Context) {
        viewModelScope.launch {
            try {
                // 1. Clear UI elements first
                mapViewVM?.let { map ->
                    map.overlays.clear()
                    map.invalidate()
                }

                // 2. Clear local markers and data
                produitsMainDataBase.forEach { produit ->
                    produit.bonsVentDeCetteCota.forEach { bonVent ->
                        // Clear existing marker
                        bonVent.clientInformations?.gpsLocation?.locationGpsMark?.let { marker ->
                            marker.closeInfoWindow()
                            marker.remove(mapViewVM)
                        }
                        // Reset marker reference
                        bonVent.clientInformations?.gpsLocation?.locationGpsMark = null
                    }
                }

                // 3. Remove data from Firebase using removeValue()
                produitsMainDataBase.forEach { produit ->
                    produit.bonsVentDeCetteCota.forEach { bonVent ->
                        val clientRef = produitsFireBaseRef
                            .child(produit.id.toString())
                            .child("bonsVentDeCetteCota")
                            .child(bonVent.clientInformations?.id.toString())
                            .child("clientInformations")
                            .child("gpsLocation")

                        // Remove the entire gpsLocation node
                        clientRef.removeValue().await()
                    }
                }
                initializeMapView(context)
                Log.d(
                    "FirebaseCleanup",
                    "Successfully cleared all data from UI, local storage, and Firebase"
                )
            } catch (e: Exception) {
                Log.e("FirebaseCleanup", "Failed to clear data", e)
                throw e
            }
        }
    }

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

        _ModelAppsFather.updateProduit(product, this@ViewModelInitApp)
    }

    init {
        viewModelScope.launch {
            try {
                isLoading = true
                val nombre = 0
                if (nombre == 0) {
                    LoadFromFirebaseProduits.loadFromFirebase(this@ViewModelInitApp)
                } else {
                    CreeDepuitAncienDataBases(
                        _modelAppsFather,
                        this@ViewModelInitApp
                    )
                }

                isLoading = false
            } catch (e: Exception) {
                Log.e("ViewModelInitApp", "Init failed", e)
                isLoading = false
            }
        }
    }
}
