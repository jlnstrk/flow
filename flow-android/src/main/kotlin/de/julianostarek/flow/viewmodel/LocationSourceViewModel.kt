package de.julianostarek.flow.viewmodel

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.location.Location
import android.os.Handler
import android.os.Looper
import androidx.core.app.ActivityCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.*
import de.julianostarek.flow.provider.util.await
import kotlinx.coroutines.launch

class LocationSourceViewModel(application: Application) : AndroidViewModel(application) {
    val handler: Handler = Handler(Looper.getMainLooper())
    val location: ActivityLiveData<Location> = ActivityLiveData()
    val locationAvailability: MutableLiveData<LocationAvailability> = MutableLiveData()
    private val locationProviderClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(application)
    private val locationRequest: LocationRequest = LocationRequest()
        .setInterval(LOCATION_UPDATE_INTERVAL_MS)
        .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
        .setSmallestDisplacement(MINIMUM_LOCATION_DISPLACEMENT)
    private val locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            if (location.value == null
                || result.lastLocation!!.distanceTo(location.value) >= MINIMUM_LOCATION_DISPLACEMENT) {
                location.value = result.lastLocation
            }
        }

        override fun onLocationAvailability(availability: LocationAvailability) {
            locationAvailability.value = availability
        }
    }

    fun requestLocationRefresh() {
        if (location.hasActiveObservers()) {
            deregisterLocationUpdates()
            registerLocationUpdates()
        }
    }

    init {
        location.doOnActive(::registerLocationUpdates)
        location.doOnInactive(::deregisterLocationUpdates)
    }

    private var delayedRegistration: Runnable? = null
    private fun registerLocationUpdates() = viewModelScope.launch {
        if (ActivityCompat.checkSelfPermission(
                getApplication(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                getApplication(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return@launch
        }
        val first = locationProviderClient.lastLocation?.await()
        if (first != null) {
            location.value = first
            delayedRegistration = Runnable {
                locationProviderClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    null
                )
            }
            handler.post(delayedRegistration!!)
        } else {
            locationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null)
        }
    }

    private fun deregisterLocationUpdates() {
        if (delayedRegistration != null) {
            handler.removeCallbacks(delayedRegistration!!)
            delayedRegistration = null
        }
        locationProviderClient.removeLocationUpdates(locationCallback)
    }

    companion object {
        private const val LOCATION_UPDATE_INTERVAL_MS = 30 * 1000L
        private const val MINIMUM_LOCATION_DISPLACEMENT = 50.0F
    }
}