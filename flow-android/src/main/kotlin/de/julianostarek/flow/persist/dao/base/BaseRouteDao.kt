package de.julianostarek.flow.persist.dao.base

import androidx.paging.DataSource
import androidx.room.*
import de.julianostarek.flow.persist.model.RouteEntity
import de.julianostarek.flow.persist.model.populated.PopulatedRoute

abstract class BaseRouteDao : BaseLocationDao() {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    protected abstract suspend fun insertRoute(route: RouteEntity): Long

    @Query("select id from routes where origin_id = :originId and destination_id = :destinationId")
    protected abstract suspend fun findExistingRoute(originId: Long, destinationId: Long): Long?

    @Update
    protected abstract suspend fun updateRoute(route: RouteEntity): Int

    @Query("update routes set favorite = :isFavorite where origin_id = :originId and destination_id = :destinationId")
    protected abstract suspend fun updateRouteIsFavorite(
        originId: Long,
        destinationId: Long,
        isFavorite: Boolean
    ): Int

    @Transaction
    @Query("select * from routes order by favorite desc, last_queried desc")
    protected abstract fun pageRoutesInternal(): DataSource.Factory<Int, PopulatedRoute>

    @Transaction
    @Query("select * from routes order by last_queried desc limit 1")
    protected abstract suspend fun selectMostRecentRouteInternal(): PopulatedRoute?
}