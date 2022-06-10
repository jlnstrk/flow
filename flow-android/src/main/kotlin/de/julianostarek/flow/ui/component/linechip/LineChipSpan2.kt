package de.julianostarek.flow.ui.component.linechip

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.text.style.ReplacementSpan
import de.jlnstrk.transit.common.model.Line
import kotlin.math.max
import kotlin.math.roundToInt

class LineChipSpan2(
    context: Context,
    line: Line,
    private var mode: LineChipDrawable.Mode? = null
) : ReplacementSpan() {
    private val drawable = LineChipDrawable(context, line)

    override fun draw(
        canvas: Canvas,
        text: CharSequence,
        start: Int,
        end: Int,
        x: Float,
        top: Int,
        y: Int,
        bottom: Int,
        paint: Paint
    ) {
        canvas.save()
        canvas.translate(x, top + ((bottom - top) - drawable.intrinsicHeight) / 2F)
        drawable.draw(canvas)
        canvas.restore()
    }

    override fun getSize(
        paint: Paint,
        text: CharSequence,
        start: Int,
        end: Int,
        fm: Paint.FontMetricsInt?
    ): Int {
        drawable.adjustTextPaint(paint)
        drawable.mode = mode ?: when {
            start == 0 && end == text.length -> LineChipDrawable.Mode.ONLY
            start == 0 -> LineChipDrawable.Mode.START
            end == text.length -> LineChipDrawable.Mode.END
            else -> LineChipDrawable.Mode.MID
        }

        drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)

        val width = drawable.intrinsicWidth
        val height = drawable.intrinsicHeight.toFloat()

        if (fm != null) {
            val need = max(0F, height - (fm.bottom - fm.top))
            val claim = (need / 2F).roundToInt()
            fm.ascent -= claim
            fm.top -= claim
            fm.descent += claim
            fm.bottom += claim
        }

        return width - drawable.overlapTranslation
    }

}