package de.julianostarek.flow.ui.main.trips.results.timeline.trip

import android.graphics.*
import android.text.TextPaint
import android.util.DisplayMetrics
import androidx.annotation.CallSuper
import de.julianostarek.flow.ui.main.trips.results.timeline.DensityDrawable
import kotlin.math.roundToInt

abstract class TimelineTripDrawable(
    displayMetrics: DisplayMetrics,
    protected val offsetDuration: Long,
    protected val segmentDuration: Long
) : DensityDrawable(displayMetrics) {
    protected val segmentPath = Path()
    protected val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    protected val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)

    protected var offsetHeight: Float = 0.0F
    protected var segmentHeight: Float = 0.0F
    private var totalHeight: Int = 0

    @CallSuper
    open fun setMinuteScale(minuteScale: Int) {
        offsetHeight = (minuteScale * offsetDuration).toFloat()
        segmentHeight = (minuteScale * segmentDuration).toFloat()
    }

    override fun getIntrinsicWidth(): Int = 0

    override fun getIntrinsicHeight(): Int = (offsetHeight + segmentHeight).roundToInt()

    @CallSuper
    override fun setAlpha(alpha: Int) {
        fillPaint.alpha = alpha
        textPaint.alpha = alpha
    }

    @CallSuper
    open fun applyTextPaint(textPaint: TextPaint) {
        this.textPaint.set(textPaint)
    }

    protected fun buildSegmentPath(
        roundedTop: Boolean,
        roundedBottom: Boolean
    ) = segmentPath.apply {
        reset()
        if (roundedTop) {
            moveTo(0F, 4F.dp)
            quadTo(0F, 0F, 4F.dp, 0F)
            lineTo(bounds.width() - 4F.dp, 0F)
            quadTo(bounds.width().toFloat(), 0F, bounds.width().toFloat(), 4F.dp)
        } else {
            lineTo(bounds.width().toFloat(), 0F)
        }
        if (roundedBottom) {
            lineTo(bounds.width().toFloat(), segmentHeight - 4F.dp)
            quadTo(
                bounds.width().toFloat(),
                segmentHeight,
                bounds.width().toFloat() - 4F.dp,
                segmentHeight
            )
            lineTo(4F.dp, segmentHeight)
            quadTo(0F, segmentHeight, 0F, segmentHeight - 4F.dp)
        } else {
            lineTo(bounds.width().toFloat(), segmentHeight)
            lineTo(0F, segmentHeight)
        }
        close()
    }

    override fun getPadding(padding: Rect): Boolean {
        padding.set(0, 0, offsetHeight.roundToInt(), 0)
        return true
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        // ignore
    }

    override fun getOpacity(): Int = PixelFormat.TRANSLUCENT

}