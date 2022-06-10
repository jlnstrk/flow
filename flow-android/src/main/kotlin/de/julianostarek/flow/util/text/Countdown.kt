package de.julianostarek.flow.util.text

import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StrikethroughSpan
import de.jlnstrk.transit.common.model.Journey
import de.jlnstrk.transit.common.model.stop.Stop
import de.julianostarek.flow.R
import de.julianostarek.flow.ui.common.time.util.toSystemLocal
import de.julianostarek.flow.ui.main.stops.stationboard.merged.MergedJourney
import de.julianostarek.flow.util.datetime.TIME_FORMAT_HH_MM
import de.julianostarek.flow.util.datetime.colorRelativeTo
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.math.abs

fun MergedJourney.formatCountdownTimes(
    context: Context,
    minutesNumberSpan: Any,
    minutesTextSpan: Any,
    sizeUniform: Boolean = false,
    suppressNewline: Boolean = false
): CharSequence {
    val stops = journeys.map(Journey::stop).take(2)
    val scheduledTimes = stops.map {
        if (it is Stop.Arrival) it.arrivalScheduled else (it as Stop.Departure).departureScheduled
    }
    val realtimeTimes = stops.map {
        if (it is Stop.Arrival) it.arrivalRealtime else (it as Stop.Departure).departureRealtime
    }
    val cancelledStatuses = stops.map {
        if (it is Stop.Arrival) it.arrivalCancelled else (it as Stop.Departure).departureCancelled
    }.toBooleanArray()
    return formatCountdownTime(
        context,
        minutesNumberSpan,
        minutesTextSpan,
        scheduledTimes,
        realtimeTimes,
        cancelledStatuses,
        sizeUniform,
        suppressNewline
    )
}

fun Stop.formatCountdownTime(
    context: Context,
    minutesNumberSpan: Any?,
    minutesTextSpan: Any?,
    sizeUniform: Boolean = false,
    suppressNewline: Boolean = false
): CharSequence {
    return when (this) {
        is Stop.Arrival -> formatCountdownTime(
            context,
            minutesNumberSpan,
            minutesTextSpan,
            listOf(arrivalScheduled),
            listOf(arrivalRealtime),
            booleanArrayOf(arrivalCancelled),
            sizeUniform,
            suppressNewline
        )
        is Stop.Departure -> formatCountdownTime(
            context,
            minutesNumberSpan,
            minutesTextSpan,
            listOf(departureScheduled),
            listOf(departureRealtime),
            booleanArrayOf(departureCancelled),
            sizeUniform,
            suppressNewline
        )
        else -> throw IllegalArgumentException()
    }
}

fun formatCountdownTime(
    context: Context,
    minutesNumberSpan: Any?,
    minutesTextSpan: Any?,
    scheduledTimes: List<Instant>,
    realtimeTimes: List<Instant?>,
    cancelledStatuses: BooleanArray,
    sizeUniform: Boolean,
    suppressNewline: Boolean,
): CharSequence {
    val now = Clock.System.now()

    val preBuilder = SpannableStringBuilder()
    val postBuilder = SpannableStringBuilder()
    val absoluteBuilder = SpannableStringBuilder()

    var allCancelled = true

    for ((index, scheduled) in scheduledTimes.withIndex()) {
        val realtime = realtimeTimes[index]

        val cancelled = cancelledStatuses[index]
        if (!cancelled) {
            allCancelled = false
        }
        val relativeOffset = ((realtime ?: scheduled) - now).inWholeMinutes
        val absoluteOffset = abs(relativeOffset)
        val isWithinHour = absoluteOffset <= 60L

        if (!isWithinHour && index > 0) {
            break
        }

        when {
            relativeOffset == 0L -> {
                val oldLength = postBuilder.length
                if (oldLength > 0) {
                    postBuilder.appendSeparator()
                }
                realtime?.let {
                    postBuilder.boldInColor(it.colorRelativeTo(context, scheduled)) {
                        appendStringRes(context, R.string.time_now)
                    }
                } ?: postBuilder.appendStringRes(context, R.string.time_now)
                if (cancelled) {
                    postBuilder.setSpan(
                        StrikethroughSpan(),
                        oldLength,
                        postBuilder.length,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
                if (index == scheduledTimes.lastIndex) {
                    if (index == 0 && !sizeUniform) {
                        postBuilder.setSpan(
                            AbsoluteSizeSpan(18, true),
                            oldLength,
                            postBuilder.length,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                    }
                    return postBuilder
                }
            }
            isWithinHour -> {
                val builder = if (relativeOffset < 0L) preBuilder else postBuilder
                if (builder.isNotEmpty()) {
                    builder.appendSeparator()
                }
                val stringValue = absoluteOffset.toString()
                if (realtime == null) {
                    builder.append(stringValue)
                } else builder.boldInColor(realtime.colorRelativeTo(context, scheduled)) {
                    append(stringValue)
                }
                if (cancelled) {
                    builder.setSpan(
                        StrikethroughSpan(),
                        builder.length - stringValue.length,
                        builder.length,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
            }
            else -> {
                val effectiveAtSystem =
                    (realtime ?: scheduled).toSystemLocal()
                val effectiveString = TIME_FORMAT_HH_MM.formatDateTime(effectiveAtSystem)
                if (realtime == null) {
                    absoluteBuilder.append(effectiveString)
                } else absoluteBuilder.boldInColor(realtime.colorRelativeTo(context, scheduled)) {
                    append(effectiveString)
                }

                if (cancelled) {
                    absoluteBuilder.setSpan(
                        StrikethroughSpan(),
                        absoluteBuilder.length - effectiveString.length,
                        absoluteBuilder.length,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
            }
        }

        if (!isWithinHour) {
            break
        }
    }

    // If all are cancelled (we couldn't know before),
    // remove any substring strike-through spans added up until now.
    // We'll add an embracing one later
    if (allCancelled) {
        absoluteBuilder.clearSpans()
        absoluteBuilder.setSpan(
            StrikethroughSpan(),
            0,
            absoluteBuilder.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }

    // We can only show either "in X minutes" OR "X minutes ago" OR an absolute time like "12:00",
    // This is our order of choosing
    val effective: Spannable

    if (absoluteBuilder.isEmpty()) {
        val chosenBuilder = if (postBuilder.isEmpty()) preBuilder else postBuilder
        val stringRes = when {
            suppressNewline && postBuilder.isEmpty() -> R.string.time_min_ago_space
            postBuilder.isEmpty() -> R.string.time_min_ago_break
            suppressNewline -> R.string.time_in_min_space
            else -> R.string.time_in_min_break
        }


        effective = SpannableString(
            context.getString(stringRes, chosenBuilder)
        )
        // Find our format arg in the final string, apply the appearance spans
        val argStart = effective.indexOf(chosenBuilder.toString())
        val argEnd = argStart + chosenBuilder.length

        // Set the text span on the whole string
        effective.setSpan(minutesTextSpan, 0, effective.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        // Set the minutes span on the arg portion
        effective.setSpan(minutesNumberSpan, argStart, argEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        // Copy our previous spans over
        chosenBuilder.getSpans(0, chosenBuilder.length, Object::class.java)
            .forEach {
                val oldStart = chosenBuilder.getSpanStart(it)
                val oldEnd = chosenBuilder.getSpanEnd(it)

                // If this is the one and only color span, apply it to everything
                if (scheduledTimes.size == 1 && it is ForegroundColorSpan) {
                    effective.setSpan(it, 0, effective.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                } else {
                    effective.setSpan(
                        it,
                        argStart + oldStart,
                        argStart + oldEnd,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
            }
    } else {
        // Absolute times aren't appearance-spanned.
        // We're inheriting the text views's appearance
        effective = absoluteBuilder
    }

    if (allCancelled) {
        effective.setSpan(
            StrikethroughSpan(),
            0,
            effective.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }

    return effective
}