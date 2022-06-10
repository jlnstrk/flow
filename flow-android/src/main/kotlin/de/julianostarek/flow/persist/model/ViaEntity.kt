package de.julianostarek.flow.persist.model

import androidx.annotation.NonNull
import androidx.room.*
import de.jlnstrk.transit.common.model.Location
import kotlin.time.Duration

@Entity(
    tableName = "route_vias",
    primaryKeys = ["route_id", "route_index"],
    indices = [
        // Require location uniqueness in a route's via
        Index("route_id", "location_id", unique = true),
        Index("location_id", unique = false)
    ],
    foreignKeys = [
        ForeignKey(
            entity = RouteEntity::class,
            parentColumns = ["id"],
            childColumns = ["route_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = LocationEntity::class,
            parentColumns = ["id"],
            childColumns = ["location_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ViaEntity(
    @NonNull
    @ColumnInfo(name = "route_id")
    val routeId: Long,

    @NonNull
    @ColumnInfo(name = "route_index")
    val routeIndex: Int,

    @NonNull
    @ColumnInfo(name = "location_id")
    val locationId: Long,

    @NonNull
    @ColumnInfo(name = "wait_time")
    val waitTime: Long
) {
    @Ignore
    lateinit var location: Location
}