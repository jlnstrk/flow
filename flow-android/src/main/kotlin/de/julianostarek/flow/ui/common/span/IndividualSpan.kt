package de.julianostarek.flow.ui.common.span

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.text.TextPaint
import de.julianostarek.flow.R
import de.julianostarek.flow.ui.common.span.base.ImageSpanCompat
import de.julianostarek.flow.util.graphics.dp
import de.jlnstrk.transit.common.model.Leg
import kotlin.math.roundToInt

class IndividualSpan(context: Context, leg: Leg.Individual) :
    ImageSpanCompat(
        context,
        when (leg.type) {
            Leg.Individual.Type.WALK -> R.drawable.ic_individual_walk_24dp
            Leg.Individual.Type.BIKE -> R.drawable.ic_individual_bike_24dp
            Leg.Individual.Type.CAR -> R.drawable.ic_individual_car_24dp
            Leg.Individual.Type.TAXI -> R.drawable.ic_individual_taxi_24dp
        },
        ALIGN_CENTER,
        tintAttr = com.google.android.material.R.attr.colorOnSurface
    ) {
    private val xIconOffset: Float =
        (if (leg.type == Leg.Individual.Type.WALK) -4F else 0F).dp(context)
    private val xTextOffset: Float =
        (if (leg.type == Leg.Individual.Type.WALK) -4F else 2F).dp(context)
    private val duration: String = leg.gis.duration.inWholeMinutes.toString()

    override fun draw(
        canvas: Canvas,
        text: CharSequence?,
        start: Int,
        end: Int,
        x: Float,
        top: Int,
        y: Int,
        bottom: Int,
        paint: Paint
    ) {
        canvas.save()
        canvas.translate(xIconOffset, 0F)
        super.draw(canvas, text, start, end, x, top, y, bottom, paint)
        val textPaint = TextPaint()
        textPaint.set(paint)
        canvas.translate(x + getCachedDrawable()!!.intrinsicWidth + xTextOffset, top.toFloat())
        canvas.drawText(duration, 0F, (bottom - top) - paint.fontMetrics.descent, textPaint)
        canvas.restore()
    }

    override fun getSize(
        paint: Paint,
        text: CharSequence?,
        start: Int,
        end: Int,
        fm: Paint.FontMetricsInt?
    ): Int {
        return super.getSize(
            paint,
            text,
            start,
            end,
            fm
        ) + (paint.measureText(duration) + 2F * xIconOffset).roundToInt()
    }

}