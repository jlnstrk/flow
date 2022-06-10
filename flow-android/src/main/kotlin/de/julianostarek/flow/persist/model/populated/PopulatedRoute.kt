package de.julianostarek.flow.persist.model.populated

import androidx.room.Embedded
import androidx.room.Relation
import de.julianostarek.flow.persist.model.LocationEntity
import de.julianostarek.flow.persist.model.RouteEntity
import de.julianostarek.flow.persist.model.ViaEntity

data class PopulatedRoute(
    @Embedded
    val route: RouteEntity,

    @Relation(
        entity = LocationEntity::class,
        parentColumn = "origin_id",
        entityColumn = "id"
    )
    val origin: PopulatedLocation,

    @Relation(
        entity = LocationEntity::class,
        parentColumn = "destination_id",
        entityColumn = "id"
    )
    val destination: PopulatedLocation,

    @Relation(
        entity = ViaEntity::class,
        parentColumn = "id",
        entityColumn = "route_id"
    )
    val via: List<PopulatedVia>
)