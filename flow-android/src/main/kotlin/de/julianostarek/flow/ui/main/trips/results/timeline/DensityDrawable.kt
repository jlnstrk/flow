package de.julianostarek.flow.ui.main.trips.results.timeline

import android.graphics.drawable.Drawable
import android.util.DisplayMetrics

abstract class DensityDrawable(
    displayMetrics: DisplayMetrics
) : Drawable() {
    protected val density: Float = displayMetrics.density
    protected val fontScale: Float = displayMetrics.scaledDensity

    protected inline val Float.dp: Float get() = this * density

    protected inline val Float.sp: Float get() = this * fontScale

}