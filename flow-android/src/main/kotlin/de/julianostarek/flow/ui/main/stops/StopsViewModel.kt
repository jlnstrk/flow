package de.julianostarek.flow.ui.main.stops

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.orhanobut.logger.Logger
import de.julianostarek.flow.util.viewmodel.launchWithIndicator
import de.julianostarek.flow.util.viewmodel.onAll
import de.julianostarek.flow.util.viewmodel.orNow
import de.julianostarek.flow.viewmodel.*
import de.jlnstrk.transit.common.extensions.require
import de.jlnstrk.transit.common.model.*
import de.jlnstrk.transit.common.response.base.ServiceResult
import de.jlnstrk.transit.common.response.base.ServiceResult.Success
import de.jlnstrk.transit.common.service.*
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

@SuppressLint("MissingPermission")
class StopsViewModel(application: Application) : LocationViewModel(application) {
    // Profile
    val supportedLocationTypes: Set<Location.Type>
        get() = requireProvider().require<StationBoardService>().supportedLocationTypes

    // Backdrop
    val location = MutableLiveData<Location?>()
    val showArrivals = MutableLiveData(false)
    val time = MutableLiveData<LocalDateTime?>(null)
    val intervalDuration = MutableLiveData(INTERVAL_DURATION_STEPS[3])

    // Nearby
    val nearbyConfigurationChanged = ChangeLiveData()
        .triggerOn(showArrivals, time, productFilter)
    val nearbyLocationsLoading = LoadingLiveData()
    val nearbyLocationsRefreshing = LoadingLiveData()
    val nearbyDeparturesLoading = LoadingLiveData()
    val nearbyDeparturesRefreshing = LoadingLiveData()
    val nearbyViewMode = MutableLiveData(NearbyViewMode.DEPARTURES)
    val nearbyLocations = MediatorLiveData<NearbyLocationsResult?>()
    val nearbyDepartures = MediatorLiveData<StationBoardResult?>()

    // Station board
    val stationBoardConfigurationChanged = ChangeLiveData()
        .triggerOn(showArrivals, time, productFilter, intervalDuration)
    val stationBoardLoading = LoadingLiveData()
    val stationBoardRefreshing = LoadingLiveData()
    val stationBoardViewMode = MutableLiveData(StationBoardViewMode.CHRONOLOGIC)
    val stationBoard = MediatorLiveData<StationBoardResult?>()

    // Journey details
    val journeyDetailsLoading = LoadingLiveData()
    val journeyDetailsRefreshing = LoadingLiveData()
    val journeyDetailsSubject = MutableLiveData<Journey?>()
    val journeyDetails = MutableLiveData<JourneyDetailsResult?>()

    init {
        nearbyLocations.onAll(
            nearbyViewMode, deviceLocation,
            onChanged = ::fetchOrClearNearbyLocations
        )
        nearbyDepartures.onAll(
            nearbyViewMode, deviceLocation, timeTick,
            onChanged = ::fetchOrClearNearbyDepartures
        )
        stationBoard.onAll(
            location, timeTick,
            onChanged = ::fetchOrClearStationBoard
        )

        nearbyConfigurationChanged.reset()
        stationBoardConfigurationChanged.reset()
    }

    fun toggleStationBoardViewMode() {
        stationBoardViewMode.value = when (stationBoardViewMode.value) {
            StationBoardViewMode.CHRONOLOGIC -> StationBoardViewMode.MERGED
            StationBoardViewMode.MERGED -> StationBoardViewMode.GROUPED
            StationBoardViewMode.GROUPED, null -> StationBoardViewMode.CHRONOLOGIC
        }
    }

    fun toggleNearbyViewMode() {
        nearbyViewMode.value = when (nearbyViewMode.value) {
            NearbyViewMode.LOCATIONS -> NearbyViewMode.DEPARTURES
            NearbyViewMode.DEPARTURES, null -> NearbyViewMode.LOCATIONS
        }
    }

    fun refreshNearby() {
        when (nearbyViewMode.value) {
            NearbyViewMode.LOCATIONS -> fetchOrClearNearbyLocations()
            NearbyViewMode.DEPARTURES -> fetchOrClearNearbyDepartures()
            else -> {}
        }
    }

    fun fetchOrClearNearbyLocations() {
        Log.d(null, "FETCH: nearby locations")
        deviceLocation.value ?: return

        if (nearbyViewMode.value != NearbyViewMode.LOCATIONS
            || deviceLocation.value == null) {
            return
        }

        val indicator = when (nearbyLocations.value) {
            null, !is Success -> nearbyLocationsLoading
            else -> nearbyLocationsRefreshing
        }

        viewModelScope.launchWithIndicator(indicator) {
            nearbyLocations.value = requireProvider()
                .require<NearbyLocationsService>()
                .nearbyLocations(
                    coordinates = Coordinates(
                        latitude = deviceLocation.value!!.latitude,
                        longitude = deviceLocation.value!!.longitude
                    ),
                    filterTypes = setOf(Location.Type.STATION),
                    filterProducts = productFilter.value
                )
        }
    }

    fun fetchOrClearNearbyDepartures() {
        Log.d(null, "FETCH: nearby departures")

        nearbyConfigurationChanged.reset()

        if (nearbyViewMode.value != NearbyViewMode.DEPARTURES
            || deviceLocation.value == null) {
            return
        }

        val indicator = when (nearbyDepartures.value) {
            null, !is ServiceResult.Success -> nearbyDeparturesLoading
            else -> nearbyDeparturesRefreshing
        }

        viewModelScope.launchWithIndicator(indicator) {
            nearbyDepartures.value = requireProvider()
                .require<StationBoardService>()
                .stationBoard(
                    mode = if (showArrivals.value == true) {
                        StationBoardService.Mode.ARRIVALS
                    } else {
                        StationBoardService.Mode.DEPARTURES
                    },
                    location = Location.Point(
                        coordinates = Coordinates(
                            latitude = deviceLocation.value!!.latitude,
                            longitude = deviceLocation.value!!.longitude
                        )
                    ),
                    dateTime = time.orNow().toInstant(TimeZone.currentSystemDefault()),
                    filterProducts = productFilter.value!!,
                    maxResults = NEARBY_DEPARTURES_MAX_JOURNEYS
                )
        }
    }

    fun refreshStationBoard() = fetchOrClearStationBoard()

    private fun fetchOrClearStationBoard() {
        Logger.d("FETCH: station board")
        stationBoardConfigurationChanged.reset()
        location.value ?: return

        val indicator = when (stationBoard.value) {
            null, !is Success -> stationBoardLoading
            else -> stationBoardRefreshing
        }

        viewModelScope.launchWithIndicator(indicator) {
            stationBoard.value = requireProvider()
                .require<StationBoardService>()
                .stationBoard(
                    mode = if (showArrivals.value == true) {
                        StationBoardService.Mode.ARRIVALS
                    } else {
                        StationBoardService.Mode.DEPARTURES
                    },
                    location = location.value!!,
                    dateTime = time.orNow().toInstant(TimeZone.currentSystemDefault()),
                    maxDuration = intervalDuration.value,
                    filterProducts = productFilter.value!!,
                    maxResults = STATION_BOARD_MAX_JOURNEYS
                )
        }
    }

    fun loadJourneyDetails(journey: Journey) {
        journeyDetailsSubject.value = journey
        fetchOrClearJourneyDetails()
    }

    fun refreshJourneyDetails() {
        fetchOrClearJourneyDetails()
    }

    private fun fetchOrClearJourneyDetails() {
        Logger.d("FETCH: journey detail")

        if (journeyDetailsSubject.value == null) {
            return
        }

        val indicator =
            if (journeyDetails.value is Success) journeyDetailsRefreshing else journeyDetailsLoading

        viewModelScope.launchWithIndicator(indicator) {
            journeyDetails.value = requireProvider()
                .require<JourneyDetailsService>()
                .journeyDetails(
                    journey = journeyDetailsSubject.value!!,
                    includeStops = true,
                    includePolyline = true
                )
        }
    }

    fun shiftIntervalDuration(decrease: Boolean) {
        val currentIndex = INTERVAL_DURATION_STEPS
            .indexOf(intervalDuration.value)
        if (decrease
            && currentIndex > 0
        ) {
            intervalDuration.value = INTERVAL_DURATION_STEPS[currentIndex - 1]
        } else if (!decrease
            && currentIndex < INTERVAL_DURATION_STEPS.lastIndex
        ) {
            intervalDuration.value = INTERVAL_DURATION_STEPS[currentIndex + 1]
        }
    }

    fun selectNearbyLocation(selected: Location) = viewModelScope.launch {
        if (requireLocationDao().persistOrUpdateLocation(selected) != -1L) {
            this@StopsViewModel.location.value = selected
        }
    }

    fun selectNearbyLocation(locationId: Long) = viewModelScope.launch {
        if (locationId >= 0L) {
            this@StopsViewModel.location.value = requireLocationDao().selectLocationById(locationId)
        } else {
            // TODO: handle current location
        }
    }

    enum class NearbyViewMode {
        LOCATIONS,
        DEPARTURES
    }

    enum class StationBoardViewMode {
        CHRONOLOGIC,
        MERGED,
        GROUPED
    }

    enum class Scene {
        NEARBY,
        STATION_BOARD,
        JOURNEY_DETAIL
    }

    companion object {
        private const val NEARBY_DEPARTURES_MAX_JOURNEYS = 15
        private const val STATION_BOARD_MAX_JOURNEYS = 15

        private val INTERVAL_DURATION_STEPS = arrayOf(
            40.minutes,
            1.hours,
            2.hours,
            6.hours,
            12.hours,
            1.days
        )
    }
}