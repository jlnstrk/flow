package de.julianostarek.flow.persist.model.populated

import androidx.room.Embedded
import androidx.room.Relation
import de.julianostarek.flow.persist.model.LocationEntity
import de.julianostarek.flow.persist.model.ViaEntity

data class PopulatedVia(
    @Embedded val via: ViaEntity,
    @Relation(
        entity = LocationEntity::class,
        parentColumn = "location_id",
        entityColumn = "id"
    ) val location: PopulatedLocation
)