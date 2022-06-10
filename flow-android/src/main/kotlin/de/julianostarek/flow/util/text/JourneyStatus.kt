package de.julianostarek.flow.util.text

import android.content.Context
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.StrikethroughSpan
import de.julianostarek.flow.R
import de.julianostarek.flow.ui.common.time.TimeDisplay
import de.julianostarek.flow.util.datetime.TIME_FORMAT_HH_MM
import de.julianostarek.flow.util.datetime.colorRelativeTo
import de.julianostarek.flow.util.res.colorCancelled
import de.julianostarek.flow.util.res.colorDelayed
import de.julianostarek.flow.util.res.colorEarly
import de.julianostarek.flow.util.res.colorOnTime
import de.jlnstrk.transit.common.model.ProductClass
import de.jlnstrk.transit.common.model.stop.BaseArrival
import de.jlnstrk.transit.common.model.stop.BaseDeparture
import de.jlnstrk.transit.common.model.stop.Stop
import de.julianostarek.flow.ui.common.time.util.toSystemLocal
import kotlinx.datetime.Instant
import kotlin.math.abs

fun Stop.formatContext(
    context: Context,
    product: ProductClass
): CharSequence {
    val spannableBuilder = SpannableStringBuilder()
    if ((this as? BaseArrival)?.arrivalScheduledPlatform != null
        || (this as? BaseDeparture)?.departureScheduledPlatform != null
    ) {
        spannableBuilder.append(formatPlatforms(context, product))
            .appendSeparator()
    }
    val scheduled = when (this) {
        is Stop.Arrival -> arrivalScheduled
        is Stop.Departure -> departureScheduled
        else -> throw IllegalArgumentException()
    }
    val realtime = when (this) {
        is Stop.Arrival -> arrivalRealtime
        is Stop.Departure -> departureRealtime
        else -> throw IllegalArgumentException()
    }
    val cancelled = when (this) {
        is Stop.Arrival -> arrivalCancelled
        is Stop.Departure -> departureCancelled
        else -> throw IllegalArgumentException()
    }
    spannableBuilder.appendState(context, scheduled, realtime, cancelled)
        .appendSeparator()
    val timeFormatted = formatTimeSingle(context, scheduled, realtime)
    if (cancelled) {
        spannableBuilder.append(
            timeFormatted,
            StrikethroughSpan(),
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    } else spannableBuilder.append(timeFormatted)
    return spannableBuilder
}

private fun SpannableStringBuilder.appendState(
    context: Context,
    scheduled: Instant,
    realtime: Instant?,
    cancelled: Boolean
): SpannableStringBuilder {
    return when {
        cancelled -> boldInColor(context.resources.colorCancelled) {
            appendStringRes(context, R.string.journey_cancelled)
        }
        realtime != null -> {
            val offsetMinutes = (realtime - scheduled).inWholeMinutes

            val color = when {
                offsetMinutes == 0L -> context.resources.colorOnTime
                offsetMinutes < 0 -> context.resources.colorEarly
                else -> context.resources.colorDelayed
            }
            boldInColor(color) {
                if (offsetMinutes == 0L) {
                    appendStringRes(context, R.string.journey_on_time)
                    return@boldInColor
                }
                val absOffsetMinutes = abs(offsetMinutes)

                if (absOffsetMinutes > 60L) {
                    val pluralRes =
                        if (offsetMinutes < 0) R.plurals.journey_early_hours_minutes else R.plurals.journey_delayed_hours_minutes
                    val hours = (absOffsetMinutes / 60).toInt()
                    val minutes = absOffsetMinutes % 60
                    appendPluralRes(context, pluralRes, hours, hours, minutes)
                } else {
                    val stringRes =
                        if (offsetMinutes < 0) R.string.journey_early_minutes else R.string.journey_delayed_minutes
                    appendStringRes(context, stringRes, absOffsetMinutes)
                }
            }
        }
        else -> appendStringRes(context, R.string.journey_scheduled)
    }
}

fun formatTimeSingle(
    context: Context,
    scheduled: Instant,
    realtime: Instant?
): CharSequence {
    return TimeDisplay.Style.ABSOLUTE_ABSOLUTE_OPT.buildTimeDisplay(
        context,
        scheduled,
        realtime,
        isMultiline = false,
        isCancelled = false
    )
}

fun formatTimeMultiple(
    context: Context,
    scheduledTimes: List<Instant>,
    realtimeTimes: List<Instant?>
): CharSequence {
    val spannableBuilder = SpannableStringBuilder()
    scheduledTimes.forEachIndexed { index, scheduled ->
        if (index > 0) {
            spannableBuilder.appendSeparator()
        }
        val realtime = realtimeTimes[index]
        val scheduledAtSystem = scheduled.toSystemLocal()
        val scheduledFormatted = TIME_FORMAT_HH_MM.formatDateTime(scheduledAtSystem)
        spannableBuilder.append(scheduledFormatted)
        if (realtime != null) {
            val offset = (realtime - scheduled).inWholeMinutes
            val color = realtime.colorRelativeTo(context, scheduled)
            spannableBuilder.appendSpace()
            spannableBuilder.append(
                if (offset >= 0) "+$offset" else offset.toString(),
                ForegroundColorSpan(color),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
    }
    return spannableBuilder
}