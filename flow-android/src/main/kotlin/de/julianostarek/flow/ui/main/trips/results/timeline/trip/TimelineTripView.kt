package de.julianostarek.flow.ui.main.trips.results.timeline.trip

import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.text.TextPaint
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import androidx.appcompat.view.ContextThemeWrapper
import de.jlnstrk.transit.common.model.Leg
import de.jlnstrk.transit.common.model.Trip
import de.jlnstrk.transit.common.util.*
import de.julianostarek.flow.R
import kotlinx.datetime.Instant
import kotlin.math.*

class TimelineTripView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(ContextThemeWrapper(context, R.style.ThemeOverlay_Sequence_Dark), attrs, defStyleAttr) {
    private var trip: Trip? = null
    private var drawSegments = mutableListOf<TimelineTripDrawable>()
    private var minuteScale: Int = -1
    private val typeface: Typeface = Typeface.DEFAULT!!
    private val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)

    init {
        textPaint.color = Color.WHITE
        textPaint.typeface = this@TimelineTripView.typeface
        textPaint.textSize = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            14F,
            context.resources.displayMetrics
        )
        textPaint.textAlign = Paint.Align.CENTER

        Resources.getSystem()
    }

    fun setTrip(trip: Trip, minuteScale: Int) {
        this.trip = trip
        this.minuteScale = minuteScale
        val numSections = trip.legs.size * 2 - 1
        drawSegments = ArrayList(numSections)
        var index = 0
        while (index < numSections) {
            val legIndex = index ushr 1
            val leg = trip.legs[legIndex]

            if (index % 2 == 0) {
                val prepared = when (leg) {
                    is Leg.Public -> newPublicLegDrawable(
                        legIndex,
                        trip.departure.departureEffective,
                        leg
                    )
                    is Leg.Individual -> {
                        newIndividualLegDrawable(
                            legIndex,
                            trip.departure.departureEffective,
                            leg
                        )
                    }
                    else -> throw IllegalStateException()
                }
                drawSegments.add(prepared)
            } else {
                val section = newWaitDrawable(
                    legIndex,
                    trip.departure.departureEffective
                )
                section?.let(drawSegments::add)
            }

            index++
        }

        for (segment in drawSegments) {
            segment.setMinuteScale(minuteScale)
            segment.applyTextPaint(textPaint)
        }

        requestLayout()
    }

    private fun newIndividualLegDrawable(
        legIndex: Int,
        start: Instant,
        leg: Leg.Individual
    ): TimelineTripDrawable {
        val previous = trip!!.legs.getOrNull(legIndex - 1)
        val next = trip!!.legs.getOrNull(legIndex + 1)

        var offset = (leg.departure.departureEffective - start).inWholeMinutes
        var duration = leg.realtimeDuration.inWholeMinutes

        // Correct scheduled-time based individual offset/length
        if (previous is Leg.Public
            && leg.departure.departureEffective < previous.arrival.arrivalEffective
        ) {
            offset += previous.arrival.arrivalDelay.inWholeMinutes
            duration -= previous.arrival.arrivalDelay.inWholeMinutes
        }

        return IndividualLegDrawable(
            context, offset, duration, legIndex == 0,
            legIndex == trip!!.legs.lastIndex, leg.type
        )
    }

    private fun newPublicLegDrawable(
        index: Int,
        start: Instant,
        leg: Leg.Public
    ): TimelineTripDrawable {
        val offset = (leg.departure.departureEffective - start).inWholeMinutes
        val height = leg.realtimeDuration.inWholeMinutes
        return PublicLegDrawable(
            context,
            offset,
            height,
            index == 0,
            leg === trip!!.legs.last(),
            leg.journey.line
        )
    }

    private fun newWaitDrawable(
        legIndex: Int,
        start: Instant
    ): TimelineTripDrawable? {
        val current = trip!!.legs.getOrNull(legIndex)
        val next = trip!!.legs.getOrNull(legIndex + 1)
        if (current == null
            || next == null
            || current.arrival.arrivalEffective == next.departure.departureEffective
        ) {
            return null
        }
        val offset = (current.arrival.arrivalEffective - start).inWholeMinutes
        val duration =
            (next.departure.departureEffective - current.arrival.arrivalEffective).inWholeMinutes
        return WaitDrawable(context, offset, duration)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        for (segment in drawSegments) {
            segment.draw(canvas)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val trip = this.trip
        if (trip != null) {
            val effectiveHeight = (trip.realtimeDuration.inWholeMinutes * minuteScale).toInt()
            setMeasuredDimension(measuredWidth, effectiveHeight)
            for (segment in drawSegments) {
                segment.setBounds(0, 0, measuredWidth, effectiveHeight)
            }
        }
    }

}