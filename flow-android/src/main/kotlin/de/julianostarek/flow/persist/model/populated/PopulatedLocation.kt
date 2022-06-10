package de.julianostarek.flow.persist.model.populated

import androidx.room.Embedded
import androidx.room.Relation
import de.julianostarek.flow.persist.model.LineEntity
import de.julianostarek.flow.persist.model.LocationEntity

data class PopulatedLocation(
    @Embedded val location: LocationEntity,
    @Relation(
        entity = LineEntity::class,
        parentColumn = "id",
        entityColumn = "location_id"
    ) val lines: Set<LineEntity>
)