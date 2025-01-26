package Z.WorkingOn.ViewModel.Extension

import Z_MasterOfApps.Kotlin.Model.ClientsDataBase
import Z_MasterOfApps.Kotlin.Model.ClientsDataBase.Companion.updateClientsDataBase
import Z_MasterOfApps.Kotlin.Model._ModelAppsFather.ProduitModel
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import kotlinx.coroutines.CoroutineScope
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

class ViewModelExtension_App1_F1(
    val viewModel: ViewModelInitApp,
    val produitsMainDataBase: MutableList<ProduitModel>,
    val clientDataBaseSnapList: SnapshotStateList<ClientsDataBase>,
    val viewModelScope: CoroutineScope,
) {
    var produitsAChoisireLeurClient: MutableList<ProduitModel> =
        emptyList<ProduitModel>().toMutableStateList()

    fun onClickAddMarkerButton(
        mapView: MapView,
    ) {
        val center = mapView.mapCenter
        require(center.latitude != 0.0) { "Invalid latitude value" }

        val newID = if (clientDataBaseSnapList.isEmpty()) {
            1L // Start with 1 if the list is empty
        } else {
            clientDataBaseSnapList.maxOf { it.id } + 1
        }
        val newnom = "Nouveau client #$newID"

        val newClient =
            ClientsDataBase(
                id = newID,
                nom = newnom
            ).apply {
                statueDeBase.cUnClientTemporaire = true
                gpsLocation.apply {
                    latitude = center.latitude
                    longitude = center.longitude
                    title = newnom
                    snippet = "Client temporaire"
                }
            }

        viewModel._modelAppsFather.clientDataBaseSnapList.add(newClient)

        ClientsDataBase.refClientsDataBase
            .child(newClient.id.toString())
            .setValue(newClient)
    }

    // ViewModelExtensionMapsHandler.kt
    fun updateStatueClient(
        selectedMarker: Marker?,
        statueVente: ClientsDataBase.GpsLocation.DernierEtatAAffiche
    ) {
        clientDataBaseSnapList.toMutableList().forEach { client ->
            if (client.id == selectedMarker?.id?.toLong()) {
                // Now works because gpsLocation is part of the data class
                val updatedClient = client.copy(
                    gpsLocation = client.gpsLocation.copy(
                        actuelleEtat = statueVente
                    )
                )
                updateClientsDataBase(updatedClient, viewModel)
            }
        }
    }
}


