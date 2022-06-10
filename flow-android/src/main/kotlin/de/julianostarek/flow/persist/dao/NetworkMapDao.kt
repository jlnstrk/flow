package de.julianostarek.flow.persist.dao

import android.content.Context
import android.net.Uri
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import de.julianostarek.flow.persist.model.NetworkMapEntity
import java.io.File

@Dao
abstract class NetworkMapDao : SequenceDao() {

    @Insert
    protected abstract suspend fun insertNetworkMap(entity: NetworkMapEntity): Long

    @Update
    protected abstract suspend fun updateNetworkMap(entity: NetworkMapEntity): Int

    @Query("update network_maps set local_uri = :uri where id = :id")
    protected abstract suspend fun updateNetworkMapLocalUri(id: Long, uri: Uri)

    // @Query("select * from network_maps order by ")

    fun getDownloadDestination(context: Context, networkMap: NetworkMapEntity): Uri {
        val file = File(
            context.filesDir,
            DIR_NETWORK_MAPS + File.separator + networkMap.id.toString()
        )
        return Uri.fromFile(file)
    }

    suspend fun notifyDownloadSuccessful(networkMap: NetworkMapEntity, uri: Uri) {
        return updateNetworkMapLocalUri(networkMap.id, uri)
    }

    companion object {
        private const val DIR_NETWORK_MAPS = "network-maps"
    }
}