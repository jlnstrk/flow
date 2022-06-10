package de.julianostarek.flow.ui.main.trips.results.timeline

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.color.MaterialColors
import de.julianostarek.flow.R
import de.julianostarek.flow.ui.common.decor.PositionItemDecoration
import de.julianostarek.flow.util.recyclerview.parentAdapterOfType
import de.julianostarek.flow.util.graphics.dp
import de.julianostarek.flow.util.floorDiv
import de.julianostarek.flow.util.floorMod
import de.julianostarek.flow.util.type.captionAppearanceResId
import kotlin.math.roundToInt
import de.jlnstrk.transit.common.model.Trip
import de.jlnstrk.transit.common.util.departureEffective
import de.julianostarek.flow.ui.common.time.util.toSystemLocal
import kotlinx.datetime.Instant

class TimelineItemDecoration(context: Context) : PositionItemDecoration() {
    private val textColor: Int = MaterialColors.getColor(context, R.attr.colorOnSurface, -1)
    private val eightDp = 8F.dp(context).roundToInt()
    private val itemStartOffset: Int = 56F.dp(context).roundToInt()
    var earliestTrip: Trip? = null
        set(value) {
            field = value
            zeroXTime = value?.departure?.departureEffective
        }
    private var zeroXTime: Instant? = null
    private val textPaint: Paint = AppCompatTextView(context)
        .also { it.setTextAppearance(context.captionAppearanceResId()) }
        .paint
        .apply {
            color = textColor
            textAlign = Paint.Align.LEFT
            isAntiAlias = true
        }
    private val linePaint: Paint = Paint().apply {
        strokeWidth = 4F.dp(context)
        color = ContextCompat.getColor(context, R.color.divider)
        isAntiAlias = true
    }

    private var _minuteScale: Int = -1
    private fun getMinuteScale(parent: RecyclerView): Int {
        val adapter = parent.adapter?.parentAdapterOfType<TimelineAdapter>()
        val adapterValue = adapter?.specs?.minuteScale ?: -1
        if (adapterValue != -1 && _minuteScale != adapterValue) {
            this._minuteScale = adapterValue
        }
        return this._minuteScale
    }


    private val time = LongArray(3)
    private val textBounds = Rect()

    private fun resetDrawTime() {
        val local = zeroXTime!!.toSystemLocal()
        time[0] = local.hour.toLong()
        time[1] = local.minute.toLong()
        time[2] = local.second.toLong()
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)
        val pixelsPerMinute = getMinuteScale(parent)
        if (pixelsPerMinute == -1) {
            return
        }
        val layoutManager = parent.layoutManager as? TimelineLayoutManager
        if (layoutManager != null && earliestTrip != null) {
            resetDrawTime()
            time.addPixelOffset(pixelsPerMinute, layoutManager.verticalScrollOffset)
            time.addPixelOffset(pixelsPerMinute, -itemStartOffset)

            val stepPixels = 15 * pixelsPerMinute
            var offset = time.getPixelOffsetToNextLine(pixelsPerMinute)
            time.nextLine(round = true)
            while (offset < parent.height) {
                val isBottomHalf = offset >= parent.height / 2
                val text = time.format()

                textPaint.getTextBounds(text, 0, text.length, textBounds)
                val lineOffsetHorizontal = 8F.dp(parent) + textBounds.width()
                val lineOffsetVertical = offset.toFloat()
                val textOffsetVertical =
                    offset.toFloat() + (textBounds.height() / 2 - textBounds.bottom)
                if (isBottomHalf) {
                    c.drawLine(
                        lineOffsetHorizontal,
                        lineOffsetVertical,
                        parent.width.toFloat(),
                        lineOffsetVertical,
                        linePaint
                    )
                    c.drawText(
                        text, 4F.dp(parent), textOffsetVertical,
                        textPaint
                    )
                } else {
                    c.drawLine(
                        0F, lineOffsetVertical, parent.width.toFloat() - lineOffsetHorizontal,
                        lineOffsetVertical, linePaint
                    )
                    c.drawText(
                        text, parent.width.toFloat() - textBounds.width() - 4F.dp(parent),
                        textOffsetVertical, textPaint
                    )
                }
                offset += stepPixels

                time.nextLine()
            }
        }
    }

    private fun LongArray.format(): String {
        return "%02d:%02d".format(this[0], this[1])
    }

    private fun LongArray.addPixelOffset(pixelsPerMinute: Int, pixels: Int) {
        // Add value in seconds
        this[2] += (pixels / (pixelsPerMinute / 60.0)).toLong()
        // Carry overflow to minutes
        this[1] += this[2] floorDiv 60L
        this[2] = this[2] floorMod 60L
        // Carry overflow to hours
        this[0] += this[1] floorDiv 60L
        this[1] = this[1] floorMod 60L

        // Correct 24-hour overflow
        this[0] = this[0] floorMod 24L
    }

    private fun LongArray.getPixelOffsetToNextLine(pixelsPerMinute: Int): Int {
        val minutesOffset = (15L - (this[1] % 15L)) * pixelsPerMinute
        val secondsOffset = ((60L - this[2]) * (pixelsPerMinute / 60.0)).toLong()
        return (minutesOffset + secondsOffset).toInt()
    }

    private fun LongArray.nextLine(round: Boolean = false) {
        // add at most 30 minutes
        if (round) {
            this[1] += 15L - this[1] % 15L
        } else {
            this[1] += 15L
        }

        // Carry overflow to hours
        this[0] += this[1] / 60L
        this[1] %= 60L

        // correct 24-hour overflow
        this[0] %= 24L
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State,
        position: Int
    ) {
        if (earliestTrip != null && position > 0) {
            val adapter = parent.adapter?.parentAdapterOfType<TimelineAdapter>()
            val minuteScale = getMinuteScale(parent)
            if (adapter != null && minuteScale != -1) {
                val trip = (parent.findContainingViewHolder(view) as TimelineTripViewHolder).data!!
                val thisDeparture = trip.departure.departureEffective
                val offsetMin = (thisDeparture - zeroXTime!!).inWholeMinutes
                outRect.top += offsetMin.toInt() * minuteScale
            }
        }
        if (position == 0) {
            outRect.left += 2 * eightDp
        }
        if (position + 1 == parent.adapter?.itemCount) {
            outRect.right += 2 * eightDp
        }
    }

}
