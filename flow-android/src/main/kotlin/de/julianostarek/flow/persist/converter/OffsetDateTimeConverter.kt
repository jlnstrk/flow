package de.julianostarek.flow.persist.converter

import androidx.room.TypeConverter
import kotlinx.datetime.Instant

object OffsetDateTimeConverter {
    @TypeConverter
    @JvmStatic
    fun fromString(from: String): Instant = Instant.parse(from)

    @TypeConverter
    @JvmStatic
    fun toString(dateTime: Instant): String = dateTime.toString()
}