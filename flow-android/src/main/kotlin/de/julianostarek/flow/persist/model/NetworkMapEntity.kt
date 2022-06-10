package de.julianostarek.flow.persist.model

import android.net.Uri
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import de.jlnstrk.transit.common.model.ProductClass
import kotlinx.datetime.Instant

@Entity(
    tableName = "network_maps"
)
data class NetworkMapEntity(

    @PrimaryKey(autoGenerate = false)
    val id: Long = -1L,

    @NonNull
    val title: String,

    @NonNull
    val author: String,

    @Nullable
    val products: Set<ProductClass>?,

    @Nullable
    @ColumnInfo(name = "thumbnail_url")
    val thumbnailUri: Uri?,

    @Nullable
    @ColumnInfo(name = "remote_uri")
    val remoteUri: Uri?,

    @Nullable
    @ColumnInfo(name = "local_uri")
    val localUri: Uri?,

    @NonNull
    val created: Instant,

    @NonNull
    val modified: Instant
)