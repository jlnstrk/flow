package de.julianostarek.flow.persist.model

import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.room.*
import de.jlnstrk.transit.common.model.ProductClass

@Entity(
    tableName = "locations",
    indices = [Index(value = ["provider_id"], unique = true)]
)
data class LocationEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,

    @NonNull
    @ColumnInfo(name = "provider_id")
    val providerId: String,

    @NonNull
    val type: Type,

    @NonNull
    val name: String,

    @Nullable
    var place: String?,

    @Embedded
    val coordinates: EmbeddedCoordinates,

    @Nullable
    val products: Set<ProductClass>?,

    @NonNull
    val weight: Long = 0
) {

    enum class Type {
        STATION,
        ADDRESS,
        POI
    }

}