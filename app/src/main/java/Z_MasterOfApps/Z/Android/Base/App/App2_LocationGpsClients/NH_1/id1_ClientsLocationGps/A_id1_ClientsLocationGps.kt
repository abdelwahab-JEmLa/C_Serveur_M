package Z_MasterOfApps.Z.Android.Base.App.App2_LocationGpsClients.NH_1.id1_ClientsLocationGps

import Z_MasterOfApps.Kotlin.Model.B_ClientsDataBase
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import Z_MasterOfApps.Z.Android.Base.App.App2_LocationGpsClients.NH_1.id1_ClientsLocationGps.B.Dialogs.MapControls
import Z_MasterOfApps.Z.Android.Base.App.App2_LocationGpsClients.NH_1.id1_ClientsLocationGps.B.Dialogs.MarkerStatusDialog
import Z_MasterOfApps.Z.Android.Base.App.App2_LocationGpsClients.NH_1.id1_ClientsLocationGps.B.Dialogs.Utils.DEFAULT_LATITUDE
import Z_MasterOfApps.Z.Android.Base.App.App2_LocationGpsClients.NH_1.id1_ClientsLocationGps.B.Dialogs.Utils.DEFAULT_LONGITUDE
import Z_MasterOfApps.Z.Android.Base.App.App2_LocationGpsClients.NH_1.id1_ClientsLocationGps.B.Dialogs.Utils.getCurrentLocation
import Z_MasterOfApps.Z.Android.Base.App.App2_LocationGpsClients.NH_1.id1_ClientsLocationGps.ViewModel.Extension.ViewModelExtension_App2_F1
import Z_MasterOfApps.Z.Android.Main.Utils.XmlsFilesHandler.Companion.xmlResources
import android.content.Context
import android.widget.LinearLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.infowindow.MarkerInfoWindow

@Composable
fun A_id1_ClientsLocationGps(
    modifier: Modifier = Modifier,
    viewModel: ViewModelInitApp = viewModel(),
    clientEnCourDeVent: Long = 0,
    onUpdateLongAppSetting: () -> Unit = {},
    onClear: () -> Unit = {},
) {
    val extensionVM = ViewModelExtension_App2_F1(
        viewModel.viewModelScope,
        viewModel.produitsMainDataBase,
        viewModel.clientDataBaseSnapList,
        viewModel
    )

    val context = LocalContext.current
    val currentZoom by remember { mutableDoubleStateOf(18.2) }
    val mapView = remember { MapView(context) }
    var selectedMarker by remember { mutableStateOf<Marker?>(null) }
    var showMarkerDialog by remember { mutableStateOf(false) }
    val showMarkerDetails by remember { mutableStateOf(true) }
    var showNonAbsentClientsOnly by remember { mutableStateOf(true) }

    // Initialize map position with current location
    LaunchedEffect(Unit) {
        val location = getCurrentLocation(context)

        val initialPosition = if (location != null) {
            MapPosition(
                latitude = location.latitude,
                longitude = location.longitude,
                isInitialized = true
            )
        } else {
            MapPosition(
                latitude = DEFAULT_LATITUDE,
                longitude = DEFAULT_LONGITUDE,
                isInitialized = true
            )
        }

        mapView.apply {
            setMultiTouchControls(true)
            setTileSource(TileSourceFactory.MAPNIK)
            controller.setZoom(currentZoom)
            controller.animateTo(GeoPoint(initialPosition.latitude, initialPosition.longitude))
        }
    }

    // Initial map configuration
    DisposableEffect(context) {
        Configuration.getInstance()
            .load(context, context.getSharedPreferences("osmdroid", Context.MODE_PRIVATE))
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setMultiTouchControls(true)

        onDispose {
            mapView.overlays.clear()
        }
    }

    val clientDataBaseSnapList = viewModel.clientDataBaseSnapList

    LaunchedEffect(clientDataBaseSnapList.toList(), clientEnCourDeVent, showNonAbsentClientsOnly) {
        // Masquer toutes les info-bulles existantes
        mapView.overlays.filterIsInstance<Marker>().forEach { it.closeInfoWindow() }

        // Supprimer les marqueurs existants
        val markersToRemove = mapView.overlays.filterIsInstance<Marker>()
            .filter { marker -> clientDataBaseSnapList.any { it.id.toString() == marker.id } }
        mapView.overlays.removeAll(markersToRemove)

        // Filtrer les clients
        val clientsToShow = if (showNonAbsentClientsOnly) {
            clientDataBaseSnapList.filter {
                it.gpsLocation.actuelleEtat != B_ClientsDataBase.GpsLocation.DernierEtatAAffiche.CLIENT_ABSENT
            }
        } else {
            clientDataBaseSnapList
        }

        // Créer et ajouter les nouveaux marqueurs
        clientsToShow.forEach { client ->
            val actuelleEtat =
                if (client.id == clientEnCourDeVent)
                    B_ClientsDataBase.GpsLocation.DernierEtatAAffiche.ON_MODE_COMMEND_ACTUELLEMENT
                else client.gpsLocation.actuelleEtat

            val marker = Marker(mapView).apply {
                id = client.id.toString()
                position = GeoPoint(
                    client.gpsLocation.latitude.takeIf { it != 0.0 } ?: DEFAULT_LATITUDE,
                    client.gpsLocation.longitude
                )
                title = client.nom
                snippet = if (client.statueDeBase.cUnClientTemporaire)
                    "Client temporaire" else "Client permanent"
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)

                val markerInfoWindowLayout = xmlResources
                    .find { it.first == "marker_info_window" }?.second
                    ?: throw IllegalStateException("marker_info_window layout not found")

                infoWindow = MarkerInfoWindow(markerInfoWindowLayout, mapView)

                val containerResourceId = xmlResources
                    .find { it.first == "info_window_container" }?.second
                    ?: throw IllegalStateException("info_window_container ID not found")

                val container = infoWindow.view.findViewById<LinearLayout>(containerResourceId)
                container?.let {
                    val backgroundColor = actuelleEtat?.let { statue ->
                        ContextCompat.getColor(context, statue.color)
                    } ?: ContextCompat.getColor(context, android.R.color.white)
                    it.setBackgroundColor(backgroundColor)
                }

                setOnMarkerClickListener { clickedMarker, _ ->
                    selectedMarker = clickedMarker
                    showMarkerDialog = true
                    if (showMarkerDetails) clickedMarker.showInfoWindow()
                    true
                }
            }

            mapView.overlays.add(marker)

            // Afficher l'info-bulle seulement si le marqueur est visible et showMarkerDetails est true
            if (showMarkerDetails && (!showNonAbsentClientsOnly ||
                        client.gpsLocation.actuelleEtat != B_ClientsDataBase.GpsLocation.DernierEtatAAffiche.CLIENT_ABSENT)) {
                marker.showInfoWindow()
            }
        }

        mapView.invalidate()
    }

    Box(modifier = modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { mapView }
        )

        Box(
            modifier = Modifier
                .size(8.dp)
                .background(Color.Red, CircleShape)
                .align(Alignment.Center)
        )

        if (viewModel._paramatersAppsViewModelModel.fabsVisibility) {
            MapControls(
                extensionVM = extensionVM,
                mapView = mapView,
                viewModelInitApp = viewModel,
                onClear = onClear,
                onFilterMarkers = {
                    // Fermer toutes les info-bulles avant de changer le filtre
                    mapView.overlays.filterIsInstance<Marker>().forEach { it.closeInfoWindow() }
                    showNonAbsentClientsOnly = !showNonAbsentClientsOnly
                }
            )
        }

        if (showMarkerDialog && selectedMarker != null) {
            MarkerStatusDialog(
                extensionVM = extensionVM,
                viewModel = viewModel,
                selectedMarker = selectedMarker,
                onDismiss = { showMarkerDialog = false },
                onUpdateLongAppSetting = onUpdateLongAppSetting,
                onRemoveMark = { marker ->
                    marker?.let {
                        mapView.overlays.remove(it)
                        mapView.invalidate()
                    }
                }
            )
        }
    }
}

private data class MapPosition(
    val latitude: Double,
    val longitude: Double,
    val isInitialized: Boolean
)
