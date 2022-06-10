package de.julianostarek.flow.util.transit

import android.content.Context
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import androidx.core.content.ContextCompat
import de.julianostarek.flow.R
import de.julianostarek.flow.util.datetime.TIME_FORMAT_HH_MM
import de.julianostarek.flow.util.text.appendLineBreak
import de.jlnstrk.transit.common.model.stop.BaseArrival
import de.jlnstrk.transit.common.model.stop.BaseDeparture
import de.jlnstrk.transit.common.model.stop.Stop
import de.jlnstrk.transit.common.util.isArrivalDelayed
import de.jlnstrk.transit.common.util.isArrivalEarly
import de.jlnstrk.transit.common.util.isDepartureDelayed
import de.jlnstrk.transit.common.util.isDepartureEarly
import kotlinx.datetime.Instant

val Stop.scheduled: Instant
    get() = when (this) {
        is Stop.Arrival -> arrivalScheduled
        is Stop.Departure -> departureScheduled
        else -> throw IllegalArgumentException()
    }

val Stop.realtime: Instant?
    get() = when (this) {
        is Stop.Arrival -> arrivalRealtime
        is Stop.Departure -> departureRealtime
        else -> throw IllegalArgumentException()
    }

val Stop.isCancelled: Boolean
    get() = when (this) {
        is Stop.Arrival -> arrivalCancelled
        is Stop.Departure -> departureCancelled
        is Stop.Passing -> false
        is Stop.Intermediate -> arrivalCancelled && departureCancelled
    }

fun Stop.Arrival.formatTimeMultiline(context: Context): CharSequence {
    val time = TIME_FORMAT_HH_MM.formatInstant(arrivalScheduled)
    return if (arrivalRealtime != null) {
        val rtTime = TIME_FORMAT_HH_MM.formatInstant(arrivalRealtime!!)
        val span = ForegroundColorSpan(realtimeColor(context))
        SpannableStringBuilder()
            .append(time)
            .appendLineBreak()
            .append(rtTime, span, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    } else time
}

fun Stop.Departure.formatTimeMultiline(context: Context, includeHyphen: Boolean = true): CharSequence {
    val time = TIME_FORMAT_HH_MM.formatInstant(departureScheduled)
    val string = if (includeHyphen) "$time  -  " else time
    return if (departureRealtime != null) {
        val rtTime = TIME_FORMAT_HH_MM.formatInstant(departureRealtime!!)
        val span = ForegroundColorSpan(realtimeColor(context))
        SpannableStringBuilder()
            .append(string)
            .appendLineBreak()
            .append(rtTime, span, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    } else string
}

inline fun BaseArrival.realtimeColor(context: Context): Int {
    return ContextCompat.getColor(
        context, when {
            isArrivalEarly -> R.color.realtime_early
            isArrivalDelayed -> R.color.realtime_delayed
            else -> R.color.realtime_on_time
        }
    )
}

inline fun BaseDeparture.realtimeColor(context: Context): Int {
    return ContextCompat.getColor(
        context, when {
            isDepartureEarly -> R.color.realtime_early
            isDepartureDelayed -> R.color.realtime_delayed
            else -> R.color.realtime_on_time
        }
    )
}

inline fun Stop.Intermediate.realtimeArrivalColor(context: Context): Int {
    return ContextCompat.getColor(
        context, when {
            isArrivalEarly -> R.color.realtime_early
            isArrivalDelayed -> R.color.realtime_delayed
            else -> R.color.realtime_on_time
        }
    )
}

inline fun Stop.Intermediate.realtimeDepartureColor(context: Context): Int {
    return ContextCompat.getColor(
        context, when {
            isDepartureEarly -> R.color.realtime_early
            isDepartureDelayed -> R.color.realtime_delayed
            else -> R.color.realtime_on_time
        }
    )
}