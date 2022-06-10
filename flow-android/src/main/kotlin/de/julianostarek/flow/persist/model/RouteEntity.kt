package de.julianostarek.flow.persist.model

import androidx.annotation.NonNull
import androidx.room.*
import de.jlnstrk.transit.common.model.Location
import de.jlnstrk.transit.common.model.Via
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

@Entity(
    tableName = "routes",
    indices = [
        Index("origin_id", "destination_id", unique = true),
        Index("origin_id", unique = false),
        Index("destination_id", unique = false)
    ],
    foreignKeys = [ForeignKey(
        entity = LocationEntity::class,
        parentColumns = ["id"],
        childColumns = ["origin_id"],
        onDelete = ForeignKey.CASCADE
    ),
        ForeignKey(
            entity = LocationEntity::class,
            parentColumns = ["id"],
            childColumns = ["destination_id"],
            onDelete = ForeignKey.CASCADE
        )]
)
data class RouteEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L,

    @NonNull
    @ColumnInfo(name = "origin_id")
    var originId: Long = 0L,

    @NonNull
    @ColumnInfo(name = "destination_id")
    var destinationId: Long = 0L,

    @NonNull
    @ColumnInfo(name = "favorite")
    var isFavorite: Boolean = false,

    @NonNull
    @ColumnInfo(name = "last_queried")
    var lastQueried: Instant = Clock.System.now(),
) {

    @Ignore
    lateinit var origin: Location

    @Ignore
    lateinit var via: List<Via>

    @Ignore
    lateinit var destination: Location
}