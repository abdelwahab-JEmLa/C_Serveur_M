package com.example.Packages.Views._2LocationGpsClients.App.Main.Utils

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Looper
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.example.c_serveur.R
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import android.os.Handler

class LocationTracker(
    private val context: Context,
    private val mapView: MapView
) : SensorEventListener, LocationListener {

    private val mainHandler = Handler(Looper.getMainLooper())
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    private val rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)

    private var locationOverlay: MyLocationNewOverlay? = null
    private var directionMarker: Marker? = null
    private var isTracking = false
    private var followLocation = true

    var currentBearing by mutableStateOf(0f)
        private set

    var currentLocation by mutableStateOf<Location?>(null)
        private set

    override fun onLocationChanged(location: Location) {
        currentLocation = location
        val geoPoint = GeoPoint(location.latitude, location.longitude)

        // Ensure UI updates happen on the main thread
        mainHandler.post {
            // Update direction marker position
            directionMarker?.position = geoPoint

            // Update location overlay
            locationOverlay?.let { overlay ->
                if (!overlay.isFollowLocationEnabled) {
                    mapView.overlays.remove(overlay)
                    locationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(context), mapView).apply {
                        enableMyLocation()
                        runOnFirstFix {
                            // Ensure animation runs on main thread
                            mainHandler.post {
                                mapView.controller.animateTo(geoPoint)
                            }
                        }
                    }
                    locationOverlay?.let { mapView.overlays.add(it) }
                } else {
                    // Animate to new position on main thread
                    mapView.controller.animateTo(geoPoint)
                }
            }

            mapView.invalidate()
        }
    }
    fun startTracking() {
        if (!isTracking) {
            // Initialize direction marker with custom arrow drawable
            directionMarker = Marker(mapView).apply {
                icon = ContextCompat.getDrawable(context, R.drawable.location_arrow)
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
            }

            // Initialize location overlay with GPS provider
            locationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(context), mapView).apply {
                enableMyLocation()
                if (followLocation) {
                    enableFollowLocation()
                }
            }

            // Add overlays to map
            mapView.overlays.apply {
                locationOverlay?.let { add(it) }
                directionMarker?.let { add(it) }
            }

            // Start sensor updates
            sensorManager.registerListener(
                this,
                rotationSensor,
                SensorManager.SENSOR_DELAY_NORMAL
            )

            try {
                // Request location updates
                locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    LOCATION_UPDATE_INTERVAL,
                    LOCATION_UPDATE_DISTANCE,
                    this
                )
                
                // Fallback to network provider if GPS is unavailable
                locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    LOCATION_UPDATE_INTERVAL,
                    LOCATION_UPDATE_DISTANCE,
                    this
                )
            } catch (e: SecurityException) {
                // Handle permission not granted
                e.printStackTrace()
            }

            isTracking = true
        }
    }

    fun stopTracking() {
        if (isTracking) {
            // Unregister listeners
            sensorManager.unregisterListener(this)
            try {
                locationManager.removeUpdates(this)
            } catch (e: SecurityException) {
                e.printStackTrace()
            }

            // Clean up overlays
            locationOverlay?.let {
                it.disableMyLocation()
                it.disableFollowLocation()
                mapView.overlays.remove(it)
            }
            directionMarker?.let {
                mapView.overlays.remove(it)
            }

            // Reset states
            locationOverlay = null
            directionMarker = null
            isTracking = false
            currentLocation = null
        }
    }

    fun toggleFollowLocation() {
        followLocation = !followLocation
        locationOverlay?.let {
            if (followLocation) {
                it.enableFollowLocation()
            } else {
                it.disableFollowLocation()
            }
        }
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_ROTATION_VECTOR) {
            val rotationMatrix = FloatArray(9)
            val orientationValues = FloatArray(3)

            // Convert rotation vector to orientation
            SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
            SensorManager.getOrientation(rotationMatrix, orientationValues)

            // Convert radians to degrees and normalize
            val azimuth = Math.toDegrees(orientationValues[0].toDouble()).toFloat()
            currentBearing = (azimuth + 360) % 360

            // Update marker rotation and map
            directionMarker?.rotation = currentBearing
            mapView.invalidate()
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not needed for this implementation
    }

    companion object {
        private const val LOCATION_UPDATE_INTERVAL = 1000L // 1 second
        private const val LOCATION_UPDATE_DISTANCE = 1f    // 1 meter
    }
}    
@Composable
fun rememberLocationTracker(mapView: MapView): LocationTracker {
    val context = LocalContext.current
    val locationTracker = remember { LocationTracker(context, mapView) }

    DisposableEffect(locationTracker) {
        onDispose {
            locationTracker.stopTracking()
        }
    }

    return locationTracker
}
