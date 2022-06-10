package de.julianostarek.flow.ui.main.trips.results.timeline.trip

import android.content.Context
import android.content.res.Configuration
import android.graphics.*
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import de.julianostarek.flow.R
import kotlin.math.PI
import kotlin.math.sin
import kotlin.math.tan

class WaitDrawable(
    context: Context,
    offsetDuration: Long,
    segmentDuration: Long
) : TimelineTripDrawable(
    context.resources.displayMetrics,
    offsetDuration,
    segmentDuration
) {
    private val icon: Drawable = ContextCompat.getDrawable(context, R.drawable.ic_time_24dp)!!

    private inline val textHeight: Float get() = -textPaint.fontMetrics.top + textPaint.fontMetrics.bottom

    // 6 * 4 + 2 * 4 + textHeight
    private inline val textHeightRequirement: Float get() = 32F.dp + textHeight
    private inline val iconHeightRequirement: Float get() = textHeightRequirement + icon.bounds.height()


    init {
        fillPaint.style = Paint.Style.FILL
        icon.setBounds(0, 0, icon.intrinsicWidth, icon.intrinsicHeight)

        val lightStripeColor: Int
        val darkStripeColor: Int
        when (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> {
                lightStripeColor = Color.WHITE and 0x33FFFFFF.toInt()
                darkStripeColor = Color.WHITE and 0x1AFFFFFF.toInt()
            }
            else -> {
                lightStripeColor = Color.BLACK and 0x66FFFFFF.toInt()
                darkStripeColor = Color.BLACK and 0x4DFFFFFF.toInt()
            }
        }

        val stripeColors = intArrayOf(lightStripeColor, darkStripeColor)
        val stripeWidth = 24F.dp
        val stripeAngle = 45.0 / 180.0 * PI
        val colorCount = stripeColors.size
        val colors = IntArray(colorCount * 2)
        val stops = FloatArray(colorCount * 2)
        for (i in 0 until colorCount) {
            colors[i * 2] = stripeColors[i]
            colors[i * 2 + 1] = colors[i * 2]
            stops[i * 2] = i / colorCount.toFloat()
            stops[i * 2 + 1] = (i + 1) / colorCount.toFloat()
        }

        val y1 = (sin(2 * stripeAngle) / 2 * stripeWidth).toFloat()
        val x1 = (stripeWidth - tan(stripeAngle) * y1).toFloat()
        fillPaint.shader = LinearGradient(
            0f, 0f,
            x1, y1,
            colors, stops,
            Shader.TileMode.REPEAT
        )
    }

    override fun draw(canvas: Canvas) {
        canvas.save()
        canvas.translate(0F, offsetHeight)
        canvas.drawRect(0F, 0F, bounds.width().toFloat(), segmentHeight, fillPaint)

        if (segmentHeight >= textHeightRequirement) {
            val alsoFitsIcon = segmentHeight >= iconHeightRequirement
            if (alsoFitsIcon) {
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