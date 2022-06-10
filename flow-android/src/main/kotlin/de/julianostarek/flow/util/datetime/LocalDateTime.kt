package de.julianostarek.flow.util.datetime

import android.content.Context
import androidx.core.content.ContextCompat
import de.julianostarek.flow.R
import kotlinx.datetime.Instant

fun Instant.colorRelativeTo(context: Context, other: Instant): Int {
    return ContextCompat.getColor(
        context, when {
            this < other -> R.color.realtime_early
            this > other -> R.color.realtime_delayed
            else -> R.color.realtime_on_time
        }
    )
}

