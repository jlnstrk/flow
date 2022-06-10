package de.julianostarek.flow.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import de.julianostarek.flow.util.AndroidLocation

abstract class LocationViewModel(application: Application) : SequenceViewModel(application) {
    private val locationSource: MutableLiveData<LiveData<AndroidLocation>> = MutableLiveData()
    val deviceLocation: LiveData<AndroidLocation> = locationSource.switchMap { it }

    fun setLocationSource(source: LiveData<AndroidLocation>) {
        locationSource.value = source
    }
}