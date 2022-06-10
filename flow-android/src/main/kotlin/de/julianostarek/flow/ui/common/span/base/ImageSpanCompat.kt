package de.julianostarek.flow.ui.common.span.base

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Paint.FontMetricsInt
import android.graphics.drawable.Drawable
import android.text.style.DynamicDrawableSpan
import android.text.style.ImageSpan
import androidx.core.graphics.drawable.DrawableCompat
import java.lang.ref.WeakReference
import kotlin.math.max

open class ImageSpanCompat : ImageSpan {
    private var drawableRef: WeakReference<Drawable>? = null
    var tintColor: Int = 0

    override fun getDrawable(): Drawable {
        val drawable = super.getDrawable()
        if (tintColor != 0) {
            val mutated = drawable.mutate()
            DrawableCompat.setTint(mutated, tintColor)
            return mutated
        }
        return drawable
    }

    constructor(context: Context, drawableRes: Int, verticalAlignment: Int = ALIGN_BOTTOM, tintAttr: Int = 0) : super(
        context,
        drawableRes,
        verticalAlignment
    ) {
        if (tintAttr != 0) {
            loadTint(context, tintAttr)
        }
    }

    constructor(drawable: Drawable, verticalAlignment: Int = ALIGN_BOTTOM) : super(
        drawable,
        verticalAlignment
    )

    constructor(context: Context, bitmap: Bitmap, verticalAlignment: Int = ALIGN_BOTTOM, tintAttr: Int = 0) : super(
        context,
        bitmap,
        verticalAlignment
    ) {
        if (tintAttr != 0) {
            loadTint(context, tintAttr)
        }
    }

    private fun loadTint(context: Context, tintAttr: Int) {
        val typedArray = context.theme.obtainStyledAttributes(intArrayOf(tintAttr))
        tintColor = typedArray.getColor(0, 0)
        typedArray.recycle()
    }

    override fun getSize(
        paint: Paint, text: CharSequence?,
        start: Int, end: Int,
        fm: FontMetricsInt?
    ): Int {
        val d = getCachedDrawable()
        val rect = d!!.bounds
        if (fm != null) {
            when (ALIGN_BOTTOM) {
                ALIGN_BOTTOM,
                ALIGN_BASELINE -> {
                    fm.ascent = -rect.bottom
                    fm.descent = 0
                    fm.top = fm.ascent
                    fm.bottom = 0
                }
                ALIGN_CENTER -> {
                    // distribute out claims equally above and below the baseline
                    val given = fm.bottom - fm.top
                    val need = max(0, rect.bottom - given)
                    fm.top -= need / 2
                    fm.bottom += need / 2
                }
            }

        }
        return rect.right
    }

    override fun draw(
        canvas: Canvas, text: CharSequence?,
        start: Int, end: Int, x: Float,
        top: Int, y: Int, bottom: Int, paint: Paint
    ) {
        val b = getCachedDrawable()
        canvas.save()
        var transY = bottom - b!!.bounds.bottom
        if (mVerticalAlignment == DynamicDrawableSpan.ALIGN_BASELINE) {
            transY -= paint.fontMetricsInt.descent
        } else if (mVerticalAlignment == DynamicDrawableSpan.ALIGN_CENTER) {
            transY = top + (bottom - top) / 2 - b.bounds.height() / 2
        }
        canvas.translate(x, transY.toFloat())
        b.draw(canvas)
        canvas.restore()
    }

    protected fun getCachedDrawable(): Drawable? {
        val wr: WeakReference<Drawable>? = drawableRef
        var d: Drawable? = null
        if (wr != null) {
            d = wr.get()
        }
        if (d == null) {
            d = drawable
            drawableRef = WeakReference(d)
        }
        return d
    }

    companion object {
        const val ALIGN_BOTTOM = 0
        const val ALIGN_BASELINE = 1
        const val ALIGN_CENTER = 2
    }

}