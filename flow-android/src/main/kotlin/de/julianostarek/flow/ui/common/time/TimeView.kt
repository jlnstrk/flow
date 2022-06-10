package de.julianostarek.flow.ui.common.time

import android.content.Context
import android.util.AttributeSet
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import androidx.appcompat.widget.AppCompatTextView
import de.julianostarek.flow.R
import de.julianostarek.flow.ui.common.span.base.TextAppearanceSpanCompat
import de.julianostarek.flow.ui.common.time.util.displayMinutesTo
import de.julianostarek.flow.ui.common.time.util.toSystemLocal
import io.ktor.utils.io.bits.*
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.math.abs

class TimeView @JvmOverloads constructor(
    context: Context,
    private val attrs: AttributeSet? = null
) : AppCompatTextView(context, attrs) {
    private var isMultiline: Boolean = false
    private var nowAppearanceSpan: Any? = null
    private var relativeAppearanceSpan: Any? = null
    private var captionAppearanceSpan: Any? = null
    private var absoluteAppearanceSpan: Any? = null
    private var nearStyle: TimeDisplay.Style = TimeDisplay.Style.RELATIVE
    private var distantStyle: TimeDisplay.Style = TimeDisplay.Style.ABSOLUTE
        set(value) {
            field = if (value == TimeDisplay.Style.RELATIVE) {
                TimeDisplay.Style.ABSOLUTE
            } else value
        }

    private var scheduled: Instant? = null
    private var realtime: Instant? = null
    private var isCancelled: Boolean = false

    init {
        val styleValues = TimeDisplay.Style.values()
        val resolved = context.obtainStyledAttributes(
            attrs,
            R.styleable.TimeView
        )
        isMultiline = resolved.getBoolean(
            R.styleable.TimeView_displayMultiline,
            isMultiline
        )
        val nowAppearanceRes = resolved.getResourceId(
            R.styleable.TimeView_nowTextAppearance,
            0
        )

        val relativeAppearanceRes = resolved.getResourceId(
            R.styleable.TimeView_relativeTextAppearance,
            0
        )
        val captionAppearanceRes = resolved.getResourceId(
            R.styleable.TimeView_captionTextAppearance,
            0
        )
        val absoluteAppearanceRes = resolved.getResourceId(
            R.styleable.TimeView_absoluteTextAppearance,
            0
        )

        if (nowAppearanceRes != 0) {
            nowAppearanceSpan = TextAppearanceSpanCompat(context, nowAppearanceRes)
        }
        if (relativeAppearanceRes != 0) {
            relativeAppearanceSpan = TextAppearanceSpanCompat(context, relativeAppearanceRes)
        }
        if (captionAppearanceRes != 0) {
            captionAppearanceSpan = TextAppearanceSpanCompat(context, captionAppearanceRes)
        }
        if (absoluteAppearanceRes != 0) {
            absoluteAppearanceSpan = TextAppearanceSpanCompat(context, absoluteAppearanceRes)
        }

        nearStyle = styleValues[resolved.getInt(
            R.styleable.TimeView_displayNear,
            TimeDisplay.Style.RELATIVE.ordinal
        )]
        distantStyle = styleValues[resolved.getInt(
            R.styleable.TimeView_displayDistant,
            TimeDisplay.Style.ABSOLUTE.ordinal
        )]
        resolved.recycle()
    }

    fun clearDisplay() {
        clearAnimation()
        this.scheduled = null
        this.realtime = null
        this.isCancelled = false
        invalidateDisplay()
    }

    fun setDisplayTime(
        scheduled: Instant,
        realtime: Instant?,
        isCancelled: Boolean
    ) {
        this.scheduled = scheduled
        this.realtime = realtime
        this.isCancelled = isCancelled
        invalidateDisplay()
    }

    fun setDisplayStyle(
        nearStyle: TimeDisplay.Style,
        distantStyle: TimeDisplay.Style,
        multiline: Boolean
    ) {
        this.nearStyle = nearStyle
        this.distantStyle = distantStyle
        this.isMultiline = multiline
        invalidateDisplay()
    }

    private fun invalidateDisplay() {
        if (scheduled == null) {
            if (text != null) {
                text = null
            }
            return
        }

        val reference = Clock.System.now()

        val scheduledOffset = reference displayMinutesTo scheduled!!
        val realtimeOffset = realtime?.let { reference displayMinutesTo it }
        val isDistant = abs(realtimeOffset ?: scheduledOffset) >= 60L

        val displayStyle = if (isDistant) distantStyle else nearStyle
        val spanned = displayStyle.buildTimeDisplay(
            context,
            scheduled!!,
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
        setText(spanned, BufferType.SPANNABLE)

        if ((realtimeOffset ?: scheduledOffset) == 0L) {
            beginBlinkAnimation()
        } else {
            clearAnimation()
        }
    }

    private fun beginBlinkAnimation() {
        val animation = AlphaAnimation(1.0F, 0.0F)
        animation.interpolator = AccelerateDecelerateInterpolator()
        animation.fillAfter = true
        animation.repeatMode = Animation.REVERSE
        animation.repeatCount = Animation.INFINITE
        animation.duration = 750
        startAnimation(animation)
    }
}