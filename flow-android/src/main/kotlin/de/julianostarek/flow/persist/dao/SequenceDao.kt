package de.julianostarek.flow.persist.dao

import androidx.room.Insert
import androidx.room.OnConflictStrategy
import de.julianostarek.flow.persist.model.LineEntity
import de.julianostarek.flow.persist.model.ViaEntity

abstract class SequenceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertVia(via: ViaEntity): Long

    @Insert
    abstract suspend fun insertLine(line: LineEntity): Long
}