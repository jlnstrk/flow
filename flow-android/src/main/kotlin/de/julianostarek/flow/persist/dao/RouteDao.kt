package de.julianostarek.flow.persist.dao

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Transaction
import de.julianostarek.flow.persist.dao.base.BaseRouteDao
import de.julianostarek.flow.persist.model.RouteEntity
import de.julianostarek.flow.persist.model.ViaEntity
import de.jlnstrk.transit.common.model.Location
import de.jlnstrk.transit.common.model.Via
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@Dao
abstract class RouteDao : BaseRouteDao() {

    fun pageRoutes(): DataSource.Factory<Int, RouteEntity> {
        return pageRoutesInternal().map {
            it.route.origin = deserializeLocation(it.origin)
            it.route.destination = deserializeLocation(it.destination)
            it.route.via = it.via.map { populated ->
                Via(
                    location = deserializeLocation(populated.location),
                    period = populated.via.waitTime.seconds
                )
            }
            it.route
        }
    }

    @Transaction
    open suspend fun persistRoute(
        origin: Location,
        via: List<Via>,
        destination: Location
    ): Boolean {
        val route = RouteEntity(
            originId = persistOrUpdateLocation(origin),
            destinationId = persistOrUpdateLocation(destination)
        )

        var routeId = findExistingRoute(route.originId, route.destinationId)

        if (routeId != null) {
            route.id = routeId
            updateRoute(route)
        } else {
            routeId = insertRoute(route)
        }

        via.mapIndexed { index, entry ->
            ViaEntity(
                routeId = routeId,
                routeIndex = index,
                locationId = persistOrUpdateLocation(entry.location),
                waitTime = (entry.period ?: Duration.ZERO).inWholeSeconds
            )
        }
            .forEach {
                insertVia(it)
            }
        return routeId >= 0L
    }

    suspend fun setRouteIsFavorite(route: RouteEntity, isFavorite: Boolean): Boolean {
        return updateRouteIsFavorite(route.originId, route.destinationId, isFavorite) == 1
    }

    @Transaction
    open suspend fun selectMostRecentRoute(): RouteEntity? {
        val route = selectMostRecentRouteInternal() ?: return null
        route.route.origin = deserializeLocation(route.origin)
        route.route.destination = deserializeLocation(route.destination)
        route.route.via = route.via.map {
            Via(
                location = deserializeLocation(it.location),
                period = it.via.waitTime.seconds
            )
        }
        return route.route
    }
}