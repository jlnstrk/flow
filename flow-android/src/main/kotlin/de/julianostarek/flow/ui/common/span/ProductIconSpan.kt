package de.julianostarek.flow.ui.common.span

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Paint.FontMetricsInt
import android.graphics.drawable.Drawable
import android.text.style.ReplacementSpan
import androidx.core.content.ContextCompat
import de.julianostarek.flow.util.graphics.dp
import kotlin.math.roundToInt
import java.lang.ref.WeakReference

class ProductIconSpan(
    private val context: Context,
    private val drawableRes: Int,
    private val fontMetrics: Paint.FontMetrics? = null
) :
    ReplacementSpan() {
    private var drawableRef: WeakReference<Drawable?>? = null

    private fun getDrawable(): Drawable {
        val drawable = ContextCompat.getDrawable(context, drawableRes)!!
        if (fontMetrics != null) {
            val height = -fontMetrics.ascent + fontMetrics.descent
            val width = height * (drawable.intrinsicWidth.toFloat() / drawable.intrinsicHeight)
            drawable.setBounds(0, 0, width.roundToInt(), height.roundToInt())
        } else {
            val height = 24F.dp(context)
            val width = (height / drawable.intrinsicHeight) * drawable.intrinsicWidth
            drawable.setBounds(0, 0, width.roundToInt(), height.roundToInt())
        }
        return drawable
    }

    override fun getSize(
        paint: Paint, text: CharSequence, start: Int, end: Int,
        fm: FontMetricsInt?
    ): Int {
        val drawable = cachedDrawable
        val rect = drawable.bounds
        if (fm != null && fontMetrics == null) {
            fm.descent = fm.descent + rect.bottom + fm.ascent
            fm.bottom = fm.descent
            fm.ascent = -rect.bottom
            fm.top = fm.ascent
           /* if (fontMetrics == null) {
                fm.ascent -= 4F.dp(context).roundToInt()
            }*/
        }
        return rect.right
    }

    override fun draw(
        canvas: Canvas, text: CharSequence,
        start: Int, end: Int, x: Float,
        top: Int, y: Int, bottom: Int, paint: Paint
    ) {
        val b = cachedDrawable
        canvas.save()
        var transY = top + ((bottom - top) - b.bounds.height()) / 2F
        /*if (fontMetrics == null) {
            transY += 2F.dp(context)
        }*/
        canvas.translate(x, transY)
        b.draw(canvas)
        canvas.restore()
    }

    private val cachedDrawable: Drawable
        get() {
            val wr = drawableRef
            var d: Drawable? = null
            if (wr != null) {
                d = wr.get()
            }
            if (d == null) {
                d = getDrawable()
                drawableRef = WeakReference(d)
            }
            return d
        }
}