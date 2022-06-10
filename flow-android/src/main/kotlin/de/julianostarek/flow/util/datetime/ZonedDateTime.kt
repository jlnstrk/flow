package de.julianostarek.flow.util.datetime

import android.content.Context
import androidx.core.content.ContextCompat
import de.julianostarek.flow.R
import java.time.ZonedDateTime

fun ZonedDateTime.colorRelativeTo(context: Context, other: ZonedDateTime): Int {
    return ContextCompat.getColor(context, when {
        isBefore(other) -> R.color.realtime_early
        isAfter(other) -> R.color.realtime_delayed
        else -> R.color.realtime_on_time
    })
}