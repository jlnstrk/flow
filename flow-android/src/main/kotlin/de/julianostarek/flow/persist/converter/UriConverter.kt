package de.julianostarek.flow.persist.converter

import android.net.Uri
import androidx.room.TypeConverter

object UriConverter {
    @TypeConverter
    fun fromString(value: String): Uri = Uri.parse(value)

    @TypeConverter
    fun toString(value: Uri): String = value.toString()
}