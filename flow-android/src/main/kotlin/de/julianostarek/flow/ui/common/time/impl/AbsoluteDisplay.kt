package de.julianostarek.flow.ui.common.time.impl

import android.content.Context
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StrikethroughSpan
import de.julianostarek.flow.ui.common.time.TimeDisplay
import de.julianostarek.flow.ui.common.time.util.toSystemLocal
import de.julianostarek.flow.util.text.boldInColor
import de.julianostarek.flow.util.datetime.TIME_FORMAT_HH_MM
import de.julianostarek.flow.util.datetime.colorRelativeTo
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime

object AbsoluteDisplay : TimeDisplay {

    override fun buildTimeDisplay(
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
    ): CharSequence = SpannableStringBuilder().apply {
        val formatted = TIME_FORMAT_HH_MM.formatDateTime((realtime ?: scheduled).toSystemLocal())
        if (realtime == null) {
            append(formatted)
        } else {
            val color = realtime.colorRelativeTo(context, scheduled)
            boldInColor(color) {
                append(formatted)
            }
        }

        if (absoluteAppearanceSpan != null) {
            setSpan(absoluteAppearanceSpan, 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        if (isCancelled) {
            setSpan(StrikethroughSpan(), 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
    }

}