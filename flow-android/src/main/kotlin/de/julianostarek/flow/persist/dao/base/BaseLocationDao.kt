package de.julianostarek.flow.persist.dao.base

import androidx.room.*
import de.julianostarek.flow.persist.dao.SequenceDao
import de.julianostarek.flow.persist.model.EmbeddedCoordinates
import de.julianostarek.flow.persist.model.LineEntity
import de.julianostarek.flow.persist.model.LocationEntity
import de.julianostarek.flow.persist.model.populated.PopulatedLocation
import de.jlnstrk.transit.common.extensions.toLineSet
import de.jlnstrk.transit.common.extensions.toProductSet
import de.jlnstrk.transit.common.model.Coordinates
import de.jlnstrk.transit.common.model.Line
import de.jlnstrk.transit.common.model.Location

abstract class BaseLocationDao : SequenceDao() {

    @Query("select id from locations where provider_id = :id")
    protected abstract suspend fun findLocation(id: String): Long?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    protected abstract suspend fun insertLocation(location: LocationEntity): Long

    @Update
    protected abstract suspend fun updateLocation(location: LocationEntity): Int

    protected fun deserializeLocation(location: PopulatedLocation): Location {
        val persisted = location.location
        return when (persisted.type) {
            LocationEntity.Type.STATION -> Location.Station(
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
            LocationEntity.Type.ADDRESS -> Location.Address(
                id = persisted.providerId,
                name = persisted.name,
                place = persisted.place,
                coordinates = Coordinates(
                    latitude = persisted.coordinates.latitude,
                    longitude = persisted.coordinates.longitude
                )
            )
            LocationEntity.Type.POI -> Location.Poi(
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

    @Transaction
    open suspend fun persistOrUpdateLocation(location: Location): Long {
        val entity = LocationEntity(
            providerId = location.id ?: "",
            type = when (location) {
                is Location.Station -> LocationEntity.Type.STATION
                is Location.Address -> LocationEntity.Type.ADDRESS
                is Location.Poi -> LocationEntity.Type.POI
                else -> LocationEntity.Type.ADDRESS
            },
            name = location.name!!,
            place = location.place,
            coordinates = EmbeddedCoordinates(
                latitude = location.coordinates!!.latitude,
                longitude = location.coordinates!!.longitude
            ),
            products = (location as? Location.Station)?.products,
        )
        var id = findLocation(entity.providerId)
        if (id != null) {
            entity.id = id
            updateLocation(entity)
        } else {
            id = insertLocation(entity)
        }
        (location as? Location.Station)?.lines?.forEach {
            val line = LineEntity(
                locationId = id,
                product = it.product,
                label = it.label
            )
            insertLine(line)
        }
        return id
    }
}