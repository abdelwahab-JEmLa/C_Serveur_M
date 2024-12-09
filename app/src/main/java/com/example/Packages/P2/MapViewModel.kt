package com.example.Packages.P2

import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class MapViewModel : ViewModel() {
    private val _currentLocation = MutableStateFlow<Location?>(null)
    val currentLocation: StateFlow<Location?> = _currentLocation.asStateFlow()

    private val _currentZoom = MutableStateFlow(18.2)
    val currentZoom: StateFlow<Double> = _currentZoom.asStateFlow()


    fun getCurrentLocation(context: Context): Location? {
        if (ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                ?: locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            
            viewModelScope.launch {
                _currentLocation.emit(location)
            }
            return location
        }
        return null
    }

    /** تحديث مستوى التكبير
     * @param zoom مستوى التكبير الجديد
     */
    fun updateZoom(zoom: Double) {
        viewModelScope.launch {
            _currentZoom.emit(zoom)
        }
    }

    /** الحصول على نقطة البداية الافتراضية
     * @return نقطة جغرافية افتراضية
     */
    fun getDefaultLocation(): Location {
        return Location("default").apply {
            latitude = -34.0
            longitude = 151.0
        }
    }
}
