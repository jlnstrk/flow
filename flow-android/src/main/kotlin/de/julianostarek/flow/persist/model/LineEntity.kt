package de.julianostarek.flow.persist.model

import androidx.annotation.NonNull
import androidx.room.*
import de.jlnstrk.transit.common.model.ProductClass

@Entity(
    tableName = "lines",
    foreignKeys = [
        ForeignKey(
            entity = LocationEntity::class,
            parentColumns = ["id"],
            childColumns = ["location_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("location_id", unique = false)
    ]
)
data class LineEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @NonNull
    @ColumnInfo(name = "location_id")
    val locationId: Long,

    @NonNull
    val product: ProductClass,

    @NonNull
    val label: String
)