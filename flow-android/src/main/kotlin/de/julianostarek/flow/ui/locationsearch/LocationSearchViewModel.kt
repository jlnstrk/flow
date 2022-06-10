package de.julianostarek.flow.ui.locationsearch

import android.app.Application
import androidx.lifecycle.*
import androidx.paging.*
import de.julianostarek.flow.persist.model.LocationEntity
import de.julianostarek.flow.util.AndroidLocation
import de.julianostarek.flow.util.asCommon
import de.julianostarek.flow.viewmodel.LoadingLiveData
import de.julianostarek.flow.viewmodel.LocationViewModel
import de.jlnstrk.transit.common.extensions.require
import de.jlnstrk.transit.common.model.Location
import de.jlnstrk.transit.common.response.base.ServiceResult
import de.jlnstrk.transit.common.service.LocationSearchResult
import de.jlnstrk.transit.common.service.LocationSearchService
import de.jlnstrk.transit.common.service.NearbyLocationsResult
import de.jlnstrk.transit.common.service.NearbyLocationsService
import kotlinx.coroutines.launch

class LocationSearchViewModel(application: Application) : LocationViewModel(application) {
    private val query = MutableLiveData<String>()
    val isLoadingResults = LoadingLiveData()
    val isLoadingNearby = LoadingLiveData()
    val locationType = MutableLiveData<Location.Type>()

    val searchResults: LiveData<LocationSearchResult> = locationType.switchMap { locationType ->
        query.switchMap { query ->
            liveData {
                emit(ServiceResult.noResult())
                val response = fetchSearchResults(locationType, query)
                emit(response)
            }
        }
    }

    val nearbyResults: LiveData<NearbyLocationsResult> = deviceLocation.switchMap { deviceLocation ->
        locationType.switchMap { locationType ->
            liveData {
                emit(ServiceResult.noResult())
                val response = fetchNearbyResults(deviceLocation, locationType)
                emit(response)
            }
        }
    }

    val offlineResults: LiveData<PagingData<Location>> = locationType.switchMap { locationType ->
        val types = when (locationType) {
            Location.Type.STATION -> setOf(LocationEntity.Type.STATION)
            Location.Type.ADDRESS -> setOf(LocationEntity.Type.ADDRESS)
            Location.Type.POI -> setOf(LocationEntity.Type.POI)
            else -> setOf(*LocationEntity.Type.values())
        }
        requireLocationDao().pageLocationsByRecentness(OFFLINE_PAGING_CONFIG, types)
            .cachedIn(viewModelScope)
            .asLiveData(viewModelScope.coroutineContext)
    }

    val locationPersisted: LiveEvent<Long> = LiveEvent()

    private suspend fun fetchSearchResults(
        type: Location.Type,
        query: String?
    ): LocationSearchResult {
        return try {
            if ((query?.length ?: 0) >= QUERY_STRING_MIN_LENGTH) {
                requireProvider()
                    .require<LocationSearchService>()
                    .locationSearch(
                        query!!,
                        maxResults = SEARCH_MAX_LOCATIONS,
                        filterTypes = setOf(type),
                        filterProducts = null
                    )
            } else ServiceResult.noResult()
        } finally {
            isLoadingResults.popLoading()
        }
    }

    private suspend fun fetchNearbyResults(
        from: AndroidLocation,
        type: Location.Type
    ): NearbyLocationsResult {
        isLoadingNearby.pushLoading()
        try {
            return requireProvider()
                .require<NearbyLocationsService>()
                .nearbyLocations(
                    coordinates = from.asCommon(),
                    maxResults = NEARBY_MAX_LOCATIONS,
                    filterTypes = setOf(type),
                )
        } finally {
            isLoadingNearby.popLoading()
        }
    }

    fun setLocationType(type: Location.Type) {
        this.locationType.value = type
    }

    fun postQuery(query: String) {
        this.query.value = query
    }

    fun persistLocation(location: Location) = viewModelScope.launch {
        val id = requireLocationDao().persistOrUpdateLocation(location)
        if (id >= 0) {
            locationPersisted.value = id
        }
    }

    companion object {
        private const val QUERY_STRING_MIN_LENGTH = 3
        private const val SEARCH_MAX_LOCATIONS = 15
        private const val NEARBY_MAX_LOCATIONS = 3
        private const val PAGED_LIST_PAGE_SIZE = 20
        private val OFFLINE_PAGING_CONFIG = PagingConfig(
            pageSize = PAGED_LIST_PAGE_SIZE,
            initialLoadSize = PAGED_LIST_PAGE_SIZE,
            enablePlaceholders = false
        )

    }

}