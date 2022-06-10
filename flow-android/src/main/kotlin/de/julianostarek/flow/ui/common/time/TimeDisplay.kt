package de.julianostarek.flow.ui.common.time

import android.content.Context
import de.julianostarek.flow.ui.common.time.impl.*
import de.julianostarek.flow.ui.common.time.util.displayMinutesTo
import de.julianostarek.flow.ui.common.time.util.toSystemLocal
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlin.math.abs

fun interface TimeDisplay {

    fun buildTimeDisplay(
        context: Context,
        scheduled: Instant,
        realtime: Instant?,
        scheduledOffset: Long,
        realtimeOffset: Long?,
        isDistant: Boolean,
        isMultiline: Boolean,
        isCancelled: Boolean,

        nowAppearanceSpan: Any?,
        relativeAppearanceSpan: Any?,
        captionAppearanceSpan: Any?,
        absoluteAppearanceSpan: Any?
    ): CharSequence

    fun buildTimeDisplay(
        context: Context,
        scheduled: Instant,
        realtime: Instant?,
        isMultiline: Boolean,
        isCancelled: Boolean,
        nowAppearanceSpan: Any? = null,
        relativeAppearanceSpan: Any? = null,
        captionAppearanceSpan: Any? = null,
        absoluteAppearanceSpan: Any? = null
    ): CharSequence {
        val reference = Clock.System.now()
        val scheduledOffset = reference displayMinutesTo scheduled
        val realtimeOffset = realtime?.let { reference displayMinutesTo it }
        val isDistant = abs(realtimeOffset ?: scheduledOffset) >= 60L
        return buildTimeDisplay(
            context,
            scheduled,
            realtime,
            scheduledOffset,
            realtimeOffset,
            isDistant,
            isMultiline,
            isCancelled,
            nowAppearanceSpan,
            relativeAppearanceSpan,
            captionAppearanceSpan,
            absoluteAppearanceSpan
        )
    }

    enum class Style(private val impl: TimeDisplay) : TimeDisplay by impl {
        RELATIVE(RelativeDisplay),
        ABSOLUTE(AbsoluteDisplay),
        ABSOLUTE_RELATIVE(AbsoluteRelativeDisplay),
        ABSOLUTE_ABSOLUTE(AbsoluteAbsoluteDisplay),
        ABSOLUTE_ABSOLUTE_OPT(AbsoluteAbsoluteOptDisplay)
    }
}

