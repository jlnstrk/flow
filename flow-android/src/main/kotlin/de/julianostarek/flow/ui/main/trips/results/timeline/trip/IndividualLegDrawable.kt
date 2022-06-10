package de.julianostarek.flow.ui.main.trips.results.timeline.trip

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import de.julianostarek.flow.R
import de.jlnstrk.transit.common.model.Leg

class IndividualLegDrawable(
    context: Context,
    offsetDuration: Long,
    segmentDuration: Long,
    private val isFirst: Boolean,
    private val isLast: Boolean,
    type: Leg.Individual.Type
) : TimelineTripDrawable(
    context.resources.displayMetrics,
    offsetDuration,
    segmentDuration
) {
    private val icon: Drawable = ContextCompat.getDrawable(
        context, when (type) {
            Leg.Individual.Type.WALK -> R.drawable.ic_individual_walk_24dp
            Leg.Individual.Type.BIKE -> R.drawable.ic_individual_bike_24dp
            Leg.Individual.Type.CAR -> R.drawable.ic_individual_car_24dp
            Leg.Individual.Type.TAXI -> R.drawable.ic_individual_taxi_24dp
        }
    )!!

    private inline val textHeight: Float get() = -textPaint.fontMetrics.top + textPaint.fontMetrics.bottom

    // (6 + 2) * 4 + textHeight
    private inline val textHeightRequirement: Float get() = 32F.dp + textHeight
    private inline val iconHeightRequirement: Float get() = textHeightRequirement + icon.bounds.height()

    init {
        fillPaint.color = 0xFF606060.toInt()
        fillPaint.style = Paint.Style.FILL
        icon.setBounds(0, 0, icon.intrinsicWidth, icon.intrinsicHeight)
    }

    override fun setBounds(left: Int, top: Int, right: Int, bottom: Int) {
        super.setBounds(left, top, right, bottom)
        buildSegmentPath(isFirst, isLast)
    }

    override fun draw(canvas: Canvas) {
        canvas.save()
        canvas.translate(0F, offsetHeight)
        canvas.drawPath(segmentPath, fillPaint)
        if (segmentHeight >= textHeightRequirement) {
            val tooFitsIcon = segmentHeight >= iconHeightRequirement
            if (tooFitsIcon) {
                canvas.translate(
                    0F,
                    segmentHeight / 2F - icon.bounds.height() / 2F - textHeight / 2F - 4F.dp
                )
                canvas.save()
                canvas.translate(bounds.width() / 2F - icon.bounds.width() / 2F, 0F)
                icon.draw(canvas)
                canvas.restore()
                canvas.translate(0F, icon.bounds.height() + 8F.dp)
            } else {
                canvas.translate(0F, segmentHeight / 2F)
            }
            canvas.translate(bounds.width() / 2F, 0F)
            canvas.drawText("$segmentDuration min", 0F, -textPaint.fontMetrics.top / 2F, textPaint)
        }

        canvas.restore()
    }

}