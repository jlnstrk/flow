package de.julianostarek.flow.util.text

import android.content.Context
import de.julianostarek.flow.R
import kotlin.math.roundToInt

fun Float.distanceFormatted(context: Context): String {
    return when {
        this > 100000 -> {
            val kilometers = this / 1000
            context.getString(
                R.string.station_distance_kilometers,
                kilometers.roundToInt().toString()
            )
        }
        this > 1000 -> {
            val kilometers = this / 1000
            val oneFloatingPoint = String.format("%.1f", kilometers)
            context.getString(R.string.station_distance_kilometers, oneFloatingPoint)
        }
        else -> context.getString(R.string.station_distance_meters, this.roundToInt())
    }
}