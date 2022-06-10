package de.julianostarek.flow.ui.common.time.util

import android.content.Context
import android.text.SpannableStringBuilder
import de.jlnstrk.transit.common.model.stop.Stop
import de.julianostarek.flow.ui.common.time.TimeDisplay
import de.julianostarek.flow.util.transit.isCancelled
import de.julianostarek.flow.util.transit.realtime
import de.julianostarek.flow.util.transit.scheduled
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

inline infix fun Instant.displayMinutesTo(other: Instant): Long {
    return ((other - this).inWholeMinutes + 59L) / 60L
}

inline fun Instant.toSystemLocal(): LocalDateTime = toLocalDateTime(TimeZone.currentSystemDefault())

inline fun <S : Stop> S.buildTimeDisplay(
    style: TimeDisplay.Style,
    context: Context,
    isMultiline: Boolean,
    nowAppearanceSpan: Any? = null,
    relativeAppearanceSpan: Any? = null,
    captionAppearanceSpan: Any? = null,
    absoluteAppearanceSpan: Any? = null
): CharSequence {
    if (this is Stop.Intermediate) {
        return SpannableStringBuilder()
            .append(
                style.buildTimeDisplay(
                    context,
                    arrivalScheduled,
                    arrivalRealtime,
                    isMultiline,
                    isCancelled,
                    nowAppearanceSpan,
                    relativeAppearanceSpan,
                    captionAppearanceSpan,
                    absoluteAppearanceSpan
                )
            )
            .append('\n')
            .append(
                style.buildTimeDisplay(
                    context,
                    departureScheduled,
                    departureRealtime,
                    isMultiline,
                    isCancelled,
                    nowAppearanceSpan,
                    relativeAppearanceSpan,
                    captionAppearanceSpan,
                    absoluteAppearanceSpan
                )
            )
    }
    return style.buildTimeDisplay(
        context,
        scheduled,
        realtime,
        isMultiline,
        isCancelled,
        nowAppearanceSpan,
        relativeAppearanceSpan,
        captionAppearanceSpan,
        absoluteAppearanceSpan
    )
}

inline fun <S : Stop> Collection<S>.composeTimeDisplay(
    context: Context,
    style: TimeDisplay.Style,
    separator: String = " •\u00A0"
): CharSequence {
    return SpannableStringBuilder().apply {
        forEachIndexed { index: Int, stop: S ->
            if (index > 0) {
                append(separator)
            }
            val display = stop.buildTimeDisplay(style, context, false)
            append(display)
        }
    }
}