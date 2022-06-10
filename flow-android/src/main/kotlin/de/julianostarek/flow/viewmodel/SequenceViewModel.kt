package de.julianostarek.flow.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import de.julianostarek.flow.FlowApp
import de.julianostarek.flow.persist.FlowDatabase
import de.julianostarek.flow.persist.dao.LocationDao
import de.julianostarek.flow.persist.dao.RouteDao
import de.julianostarek.flow.profile.FlowProfile
import de.jlnstrk.transit.common.Profile
import de.jlnstrk.transit.common.Provider

abstract class SequenceViewModel(application: Application) : AndroidViewModel(application) {
    val profileConfig: LiveData<ProfileConfig> = MutableLiveData()
    val productFilter: ProductFilterLiveData

    /* Time */
    val timeTick: TimeTickLiveEvent = TimeTickLiveEvent(application)

    protected inline fun requireConfig(): ProfileConfig = profileConfig.value!!

    protected inline fun requireProfile(): FlowProfile = requireConfig().constant

    protected inline fun requireProvider(): Provider = requireConfig().provider

    protected inline fun requireLocationDao(): LocationDao = requireConfig().locationDao

    protected inline fun requireRouteDao(): RouteDao = requireConfig().routeDao

    init {
        (application as FlowApp).onProviderChange {
            val database = FlowDatabase.getInstance(application, it)
            (profileConfig as MutableLiveData).value = ProfileConfig(
                constant = it,
                profile = it.profile,
                provider = it.provider,
                locationDao = database.locationDao(),
                routeDao = database.routeDao()
            )
        }

        val liveProfile = profileConfig.map(ProfileConfig::profile)
        productFilter = ProductFilterLiveData(liveProfile)
    }

    class ProfileConfig(
        val constant: FlowProfile,
        val profile: Profile,
        val provider: Provider,

        val locationDao: LocationDao,
        val routeDao: RouteDao
    )
}