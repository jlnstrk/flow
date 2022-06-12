package de.julianostarek.flow.database

import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import de.jlnstrk.transit.common.extensions.toLineSet
import de.jlnstrk.transit.common.extensions.toProductSet
import de.jlnstrk.transit.common.model.Coordinates
import de.jlnstrk.transit.common.model.Line
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Single

@Single
class DatabaseRepository(
    private val database: FlowDatabase
) {

    /*private fun deserializeLocation(location: PopulatedLocation): de.jlnstrk.transit.common.model.Location {
        val persisted = location.location
        return when (persisted.type) {
            LocationEntity.Type.STATION -> de.jlnstrk.transit.common.model.Location.Station(
                id = persisted.providerId,
                name = persisted.name,
                place = persisted.place,
                coordinates = Coordinates(
                    latitude = persisted.coordinates.latitude,
                    longitude = persisted.coordinates.longitude
                ),
                products = persisted.products?.toProductSet(),
                lines = location.lines.map {
                    Line(
                        product = it.product,
                        label = it.label
                    )
                }.toLineSet()
            )
            LocationEntity.Type.ADDRESS -> de.jlnstrk.transit.common.model.Location.Address(
                id = persisted.providerId,
                name = persisted.name,
                place = persisted.place,
                coordinates = Coordinates(
                    latitude = persisted.coordinates.latitude,
                    longitude = persisted.coordinates.longitude
                )
            )
            LocationEntity.Type.POI -> de.jlnstrk.transit.common.model.Location.Poi(
                id = persisted.providerId,
                name = persisted.name,
                place = persisted.place,
                coordinates = Coordinates(
                    latitude = persisted.coordinates.latitude,
                    longitude = persisted.coordinates.longitude
                )
            )
        }
    }

    fun persistOrUpdateLocation(location: de.jlnstrk.transit.common.model.Location): Long {
        val entity = LocationEntity(
            providerId = location.id ?: "",
            type = when (location) {
                is de.jlnstrk.transit.common.model.Location.Station -> LocationEntity.Type.STATION
                is de.jlnstrk.transit.common.model.Location.Address -> LocationEntity.Type.ADDRESS
                is de.jlnstrk.transit.common.model.Location.Poi -> LocationEntity.Type.POI
                else -> LocationEntity.Type.ADDRESS
            },
            name = location.name!!,
            place = location.place,
            coordinates = EmbeddedCoordinates(
                latitude = location.coordinates!!.latitude,
                longitude = location.coordinates!!.longitude
            ),
            products = (location as? de.jlnstrk.transit.common.model.Location.Station)?.products,
        )
        var id = findLocation(entity.providerId)
        if (id != null) {
            entity.id = id
            updateLocation(entity)
        } else {
            id = insertLocation(entity)
        }
        (location as? de.jlnstrk.transit.common.model.Location.Station)?.lines?.forEach {
            val line = LineEntity(
                locationId = id,
                product = it.product,
                label = it.label
            )
            insertLine(line)
        }
        return id
    }*/

    /*fun locationsByType(type: LocationType): Flow<List<Location>> {
        return database.locationQueries.selectLocationsByType(type)
            .asFlow()
            .mapToList()
            .map {
                it.map {
                    LocationWithProducts
                }
            }
    }*/
}