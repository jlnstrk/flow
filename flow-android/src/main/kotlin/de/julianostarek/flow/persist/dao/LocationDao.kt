package de.julianostarek.flow.persist.dao

import androidx.paging.*
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import de.julianostarek.flow.persist.dao.base.BaseLocationDao
import de.julianostarek.flow.persist.model.LocationEntity
import de.julianostarek.flow.persist.model.populated.PopulatedLocation
import de.jlnstrk.transit.common.model.Location
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Dao
abstract class LocationDao : BaseLocationDao() {

    suspend fun selectLocationById(id: Long): Location? {
        return selectLocationByIdInternal(id)?.let(::deserializeLocation)
    }

    @Transaction
    @Query("select * from locations where id = :id")
    abstract suspend fun selectLocationByIdInternal(id: Long): PopulatedLocation?

    @Transaction
    @Query("select * from locations where type in (:types) order by id desc")
    protected abstract fun pageLocationsByRecentnessInternal(types: Set<LocationEntity.Type>): PagingSource<Int, PopulatedLocation>

    @Transaction
    @Query("select * from locations where type in (:types) order by weight desc")
    protected abstract fun pageLocationsByWeightInternal(types: Set<LocationEntity.Type>): PagingSource<Int, PopulatedLocation>

    fun pageLocationsByRecentness(
        pagingConfig: PagingConfig,
        types: Set<LocationEntity.Type>
    ): Flow<PagingData<Location>> {
        val pager =
            Pager(pagingConfig, pagingSourceFactory = { pageLocationsByRecentnessInternal(types) })
        return pager.flow
            .map { it.map(::deserializeLocation) }
    }

    fun pageLocationsByWeight(
        pagingConfig: PagingConfig,
        types: Set<LocationEntity.Type>
    ): Flow<PagingData<Location>> {
        val pager =
            Pager(pagingConfig, pagingSourceFactory = { pageLocationsByWeightInternal(types) })
        return pager.flow
            .map { it.map(::deserializeLocation) }
    }
}