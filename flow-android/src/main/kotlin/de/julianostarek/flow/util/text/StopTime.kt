package de.julianostarek.flow.util.text

import android.content.Context
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import de.julianostarek.flow.util.datetime.TIME_FORMAT_HH_MM
import de.julianostarek.flow.util.res.colorDelayed
import de.julianostarek.flow.util.res.colorEarly
import de.julianostarek.flow.util.res.colorOnTime
import de.jlnstrk.transit.common.model.stop.Stop
import de.julianostarek.flow.ui.common.time.util.toSystemLocal
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toLocalDateTime

fun Stop.formatAbsoluteTimes(context: Context): CharSequence {
    val spannableBuilder = SpannableStringBuilder()
    when (this) {
        is Stop.Arrival -> spannableBuilder.appendAbsoluteTime(
            context,
            arrivalScheduled,
            arrivalRealtime
        )
        is Stop.Departure -> spannableBuilder.appendAbsoluteTime(
            context,
            departureScheduled,
            departureRealtime
        )
        is Stop.Intermediate -> {
            spannableBuilder.appendAbsoluteTime(context, arrivalScheduled, arrivalRealtime)
                .appendLineBreak()
                .appendAbsoluteTime(context, departureScheduled, departureRealtime)
        }
        else -> throw IllegalStateException()
    }
    return spannableBuilder
}

private fun SpannableStringBuilder.appendAbsoluteTime(
    context: Context,
    scheduled: Instant,
    realtime: Instant?
): SpannableStringBuilder {
    val scheduledString = TIME_FORMAT_HH_MM.formatDateTime(scheduled.toSystemLocal())
    val isEqual = realtime != null && realtime == scheduled
    if (isEqual) {
        val colorSpan = ForegroundColorSpan(context.resources.colorOnTime)
        append(scheduledString, colorSpan, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    } else {
        append(scheduledString)
    }
    if (realtime != null && !isEqual) {
        appendSpace()
        val offset = (realtime - scheduled).inWholeMinutes
        val colorSpan =
            ForegroundColorSpan(if (offset < 0) context.resources.colorEarly else context.resources.colorDelayed)
        append(
            if (offset < 0) offset.toString() else "+$offset",
            colorSpan,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }
    return this
}