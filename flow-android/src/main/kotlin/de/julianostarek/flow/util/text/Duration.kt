package de.julianostarek.flow.util.text

import android.content.Context
import de.julianostarek.flow.R
import de.jlnstrk.transit.common.model.Leg
import de.jlnstrk.transit.common.model.Trip
import de.jlnstrk.transit.common.util.scheduledDuration
import kotlin.time.Duration

fun Duration.formatHrsMin(context: Context): CharSequence {
    val hours = this.inWholeHours
    val minutes = this.inWholeMinutes % 60L
    return if (hours > 0) {
        context.resources.getQuantityString(
            R.plurals.duration_hours_minutes,
            hours.toInt(), hours, minutes
        )
    } else {
        context.getString(R.string.duration_minutes, minutes)
    }
}

inline fun Leg.formatHrsMin(context: Context): CharSequence =
    scheduledDuration.formatHrsMin(context)

inline fun Trip.formatHrsMin(context: Context): CharSequence =
    scheduledDuration.formatHrsMin(context)