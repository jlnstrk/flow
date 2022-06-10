package de.julianostarek.flow.ui.common.time.impl

import android.content.Context
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.*
import androidx.appcompat.content.res.AppCompatResources
import de.julianostarek.flow.R
import de.julianostarek.flow.ui.common.span.base.ImageSpanCompat
import de.julianostarek.flow.ui.common.time.TimeDisplay
import de.julianostarek.flow.ui.common.time.util.toSystemLocal
import de.julianostarek.flow.util.text.appendStringRes
import de.julianostarek.flow.util.text.boldInColor
import de.julianostarek.flow.util.datetime.TIME_FORMAT_HH_MM
import de.julianostarek.flow.util.datetime.colorRelativeTo
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlin.math.abs

object RelativeDisplay : TimeDisplay {

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
        val effectiveOffset = realtimeOffset ?: scheduledOffset
        if (effectiveOffset == 0L) {
            if (realtime != null) {
                val color = realtime.colorRelativeTo(context, scheduled)
                boldInColor(color) {
                    append(".", ImageSpan(AppCompatResources.getDrawable(context, R.drawable.ic_signal_24dp)!!.mutate().apply { setTint(color) }, ImageSpan.ALIGN_BASELINE), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    appendStringRes(context, R.string.time_now)
                }
            } else {
                appendStringRes(context, R.string.time_now)
            }
        } else if (!isDistant) {
            val formatArg = abs(effectiveOffset).toString()
            val stringRes = when {
                effectiveOffset < 0L && !isMultiline -> R.string.time_min_ago_space
                effectiveOffset < 0L && isMultiline -> R.string.time_min_ago_break
                !isMultiline -> R.string.time_in_min_space
                else -> R.string.time_in_min_break
            }

            var signalIcon: ImageSpanCompat? = null
            if (false && realtime != null) {
                signalIcon = ImageSpanCompat(context, R.drawable.ic_signal_24dp, verticalAlignment = ImageSpanCompat.ALIGN_BASELINE)
                append(".", signalIcon, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }

            appendStringRes(context, stringRes, formatArg)
            // Find our format arg in the final string, apply the appearance spans
            val argStart = indexOf(formatArg)
            val argEnd = argStart + formatArg.length

            if (isMultiline && captionAppearanceSpan != null) {
                // Set the text span on the whole string
                if (argStart > 0) {
                    val clone = TextAppearanceSpan.wrap(captionAppearanceSpan as CharacterStyle)
                    setSpan(clone,0, argStart, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
                setSpan(captionAppearanceSpan, argEnd, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }

            // Set the minutes span on the arg portion
            setSpan(relativeAppearanceSpan, argStart, argEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            if (realtime != null) {
                val color = realtime.colorRelativeTo(context, scheduled)
                signalIcon?.tintColor = color
                setSpan(
                    ForegroundColorSpan(color),
                    0,
                    length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                setSpan(
                    StyleSpan(Typeface.BOLD),
                    if (isMultiline) argStart else 0,
                    if (isMultiline) argEnd else length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
        } else {
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
        }

        if (isCancelled) {
            setSpan(StrikethroughSpan(), 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
    }

}