package de.julianostarek.flow.ui.main.trips

import android.app.Application
import androidx.annotation.UiThread
import androidx.lifecycle.*
import androidx.paging.*
import de.julianostarek.flow.persist.model.RouteEntity
import de.julianostarek.flow.util.*
import de.julianostarek.flow.util.viewmodel.LivePagedList
import de.julianostarek.flow.util.viewmodel.orNow
import de.julianostarek.flow.viewmodel.*
import de.jlnstrk.transit.common.extensions.require
import de.jlnstrk.transit.common.extensions.toProductSet
import de.jlnstrk.transit.common.model.Coordinates
import de.jlnstrk.transit.common.model.Location
import de.jlnstrk.transit.common.model.Trip
import de.jlnstrk.transit.common.model.Via
import de.jlnstrk.transit.common.response.base.ServiceResult
import de.jlnstrk.transit.common.service.TripSearchResult
import de.jlnstrk.transit.common.service.TripSearchService
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlin.time.Duration.Companion.minutes

class TripsViewModel(application: Application) : LocationViewModel(application) {
    val allowedOriginTypes: Set<Location.Type>
        get() = requireConfig().provider
            .require<TripSearchService>()
            .supportedOriginTypes
    val allowedViaTypes: Set<Location.Type>
        get() = requireConfig().provider
            .require<TripSearchService>()
            .supportedViaTypes
    val allowedDestinationTypes: Set<Location.Type>
        get() = requireConfig().provider
            .require<TripSearchService>()
            .supportedDestinationTypes
    val allowedViaCount: Int
        get() = requireConfig().provider
            .require<TripSearchService>()
            .supportedViaCount

    /* trip search configuration */
    val origin = MediatorLiveData<Location?>()
    val via = MutableLiveData<List<Via>>(mutableListOf())
    val destination = MediatorLiveData<Location?>()
    val time = MutableLiveData<LocalDateTime?>(null)
    val isArrivalTime = MutableLiveData(false)

    /* ui state indicators */
    val isLoading = LoadingLiveData()
    val isLoadingMore = LoadingLiveData()
    val resultsViewMode = MutableLiveData(ResultsViewMode.TRIPS)

    /* result data sets */
    val routes: LivePagedList<RouteEntity>
    val trips: MutableLiveData<TripSearchResult?> = MutableLiveData()
    val selectedTrip: MutableLiveData<Trip> = MutableLiveData()

    /* configuration change observers */
    val connectionResultsConfigurationChange: ChangeLiveData
    val originEqualsDestination = LiveEvent.Simple()

    private val originLocationUpdate = locationUpdateObserver(origin)
    private val destinationLocationUpdate = locationUpdateObserver(destination)

    init {
        profileConfig.observeForever {
            viewModelScope.launch {
                val route = requireRouteDao().selectMostRecentRoute()
                if (route != null) {
                    setRoute(route)
                }
            }
        }

        connectionResultsConfigurationChange = ChangeLiveData()
            .triggerOn(origin, via, destination, time, isArrivalTime, productFilter)

        routes = profileConfig
            .map(ProfileConfig::routeDao)
            .switchMap {
                val source = it.pageRoutes()
                LivePagedListBuilder(
                    source,
                    ROUTES_PAGED_LIST_CONFIG
                ).build()
            }
    }

    @UiThread
    fun setOrigin(id: Long) = viewModelScope.launch {
        origin.removeSource(deviceLocation)
        if (id >= 0) {
            val station = requireLocationDao().selectLocationById(id)
            origin.value = station
        } else {
            origin.value = Location.Point(coordinates = Coordinates(0.0, 0.0))
            origin.addSource(deviceLocation, originLocationUpdate)
        }
    }

    @UiThread
    fun setOrigin(location: Location?) = viewModelScope.launch {
        origin.removeSource(this@TripsViewModel.deviceLocation)
        origin.value = location
        if (location != null && location is Location.Point) {
            origin.addSource(this@TripsViewModel.deviceLocation, originLocationUpdate)
        }
    }

    @UiThread
    fun setDestination(id: Long) = viewModelScope.launch {
        destination.removeSource(deviceLocation)
        if (id >= 0) {
            val newDestination = requireLocationDao().selectLocationById(id)
            destination.value = newDestination
        } else {
            destination.value = Location.Point(coordinates = Coordinates(0.0, 0.0))
            destination.addSource(deviceLocation, destinationLocationUpdate)
        }
    }

    @UiThread
    fun setDestination(location: Location?) = viewModelScope.launch {
        destination.removeSource(this@TripsViewModel.deviceLocation)
        destination.value = location
        if (location != null && location is Location.Point) {
            destination.addSource(this@TripsViewModel.deviceLocation, destinationLocationUpdate)
        }
    }

    @UiThread
    fun reverseRoute() {
        val oldOrigin = origin.value
        val oldDestination = destination.value
        setOrigin(oldDestination)
        setDestination(oldOrigin)
        via.value = via.value?.reversed()
    }

    fun addVia(id: Long) = viewModelScope.launch {
        val copy = via.value.orEmpty().toMutableList()
        val location = requireLocationDao().selectLocationById(id) ?: return@launch
        val entry = Via(location = location, period = WAIT_TIME_10_MIN)
        copy.add(entry)
        via.value = copy
    }

    fun toggleViaWaitTime(position: Int) {
        val copy = via.value.orEmpty().toMutableList()
        copy[position] = copy[position]
            .copy(
                period = when (copy[position].period) {
                    WAIT_TIME_O_MIN -> WAIT_TIME_10_MIN
                    WAIT_TIME_10_MIN -> WAIT_TIME_20_MIN
                    WAIT_TIME_20_MIN -> WAIT_TIME_30_MIN
                    WAIT_TIME_30_MIN -> WAIT_TIME_45_MIN
                    WAIT_TIME_45_MIN -> WAIT_TIME_60_MIN
                    WAIT_TIME_60_MIN -> WAIT_TIME_O_MIN
                    else -> WAIT_TIME_10_MIN
                }
            )
        via.value = copy
    }

    fun removeVia(position: Int) {
        val copy = via.value.orEmpty()
            .toMutableList()
        copy.removeAt(position)
        via.value = copy
    }

    fun swapVia(fromPosition: Int, toPosition: Int) {
        val copy = via.value!!.toMutableList()
        val temp = copy[toPosition]
        copy[toPosition] = copy[fromPosition]
        copy[fromPosition] = temp
        via.value = copy
    }

    @UiThread
    fun setRoute(route: RouteEntity) {
        origin.value = route.origin
        via.value = route.via
        destination.value = route.destination
    }

    @UiThread
    fun toggleRouteFavorite(route: RouteEntity) = viewModelScope.launch {
        requireRouteDao().setRouteIsFavorite(route, !route.isFavorite)
    }

    @UiThread
    fun searchTrips(): Boolean {
        connectionResultsConfigurationChange.reset()
        if (origin.value == destination.value) {
            originEqualsDestination.invoke()
            return false
        }
        trips.value = null

        viewModelScope.launch {
            isLoading.pushLoading()
            trips.value = requireProvider()
                .require<TripSearchService>()
                .tripSearch(
                    origin = origin.value!!,
                    destination = destination.value!!,
                    via = via.value.orEmpty(),
                    dateTime = time.orNow().toInstant(TimeZone.currentSystemDefault()),
                    dateTimeIsArrival = isArrivalTime.value,
                    filterProducts = productFilter.value?.toProductSet(),
                    includePolylines = true,
                    includeStops = true,
                    maxResults = null
                )
            isLoading.popLoading()
            if (origin.value !is Location.Point && destination.value !is Location.Point) {
                requireRouteDao()
                    .persistRoute(origin.value!!, via.value!!, destination.value!!)
            }
        }
        return true
    }

    @UiThread
    fun postScrollBack() = viewModelScope.launch {
        val context =
            (trips.value as? ServiceResult.Success?)?.result?.scrollContext ?: return@launch
        if (!context.canScrollBackward) {
            return@launch
        }
        isLoadingMore.pushLoading()
        var scrollResult = requireProvider()
            .require<TripSearchService>()
            .tripSearchScroll(context, scrollBackward = true)
        if (scrollResult is ServiceResult.Success) {
            val merged = (trips.value as ServiceResult.Success).result.trips
                .toMutableList()
            merged.addAll(0, scrollResult.result.trips)
            scrollResult = scrollResult.copy(
                result = scrollResult.result.copy(
                    trips = merged
                )
            )
        }
        trips.value = scrollResult
        isLoadingMore.popLoading()
    }

    @UiThread
    fun postScrollForward() = viewModelScope.launch {
        val context =
            (trips.value as? ServiceResult.Success?)?.result?.scrollContext ?: return@launch
        if (!context.canScrollForward) {
            return@launch
        }
        isLoadingMore.pushLoading()
        var scrollResult = requireProvider()
            .require<TripSearchService>()
            .tripSearchScroll(context, scrollBackward = false)
        if (scrollResult is ServiceResult.Success) {
            val merged = (trips.value as ServiceResult.Success).result.trips
                .toMutableList()
            merged.addAll(scrollResult.result.trips)
            scrollResult = scrollResult.copy(
                result = scrollResult.result.copy(
                    trips = merged
                )
            )
        }
        trips.value = scrollResult
        isLoadingMore.popLoading()
    }

    @UiThread
    fun toggleResultsViewMode() {
        resultsViewMode.value = when (resultsViewMode.value) {
            ResultsViewMode.TRIPS -> ResultsViewMode.TRIPS_TIMELINE
            ResultsViewMode.TRIPS_TIMELINE -> ResultsViewMode.TRIPS
            else -> throw IllegalStateException()
        }
    }

    private fun locationUpdateObserver(destination: MediatorLiveData<Location?>): Observer<AndroidLocation> {
        return Observer {
            if (destination.value?.coordinates == null) {
                destination.value = Location.Point(it.asCommon())
            } else {
                destination.removeSource(deviceLocation)
            }
        }
    }

    companion object {
        private val WAIT_TIME_O_MIN = 0.minutes
        private val WAIT_TIME_10_MIN = 10.minutes
        private val WAIT_TIME_20_MIN = 20.minutes
        private val WAIT_TIME_30_MIN = 30.minutes
        private val WAIT_TIME_45_MIN = 45.minutes
        private val WAIT_TIME_60_MIN = 60.minutes

        private const val PAGE_SIZE_ROUTES = 10
        private const val PAGE_SIZE_TRIPS = 5

        private val ROUTES_PAGED_LIST_CONFIG = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setPageSize(PAGE_SIZE_ROUTES)
            .build()
    }

    enum class TripFeature(val order: Int) {
        FASTEST(0),
        LEAST_TRANSFERS(1),
        LEAST_WALKING(2),
        LEAST_WAITING(3)
    }

    enum class ResultsViewMode {
        TRIPS, TRIPS_TIMELINE
    }

    sealed class Event {

        object ConfigurationChanged : Event()

        object OriginEqualsDestination : Event()
    }
}