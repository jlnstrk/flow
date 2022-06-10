package de.julianostarek.flow.util.graphics

import android.graphics.Color
import kotlin.math.min
import kotlin.math.roundToInt

fun Int.unblend(blend: Int): Int {
    val blendAlpha = Color.alpha(blend) / 255F

    val red = Color.red(this)
    val green = Color.green(this)
    val blue = Color.blue(this)

    val blendRed = Color.red(blend)
    val blendGreen = Color.green(blend)
    val blendBlue = Color.blue(blend)

    val unblendRed = (red - blendAlpha * blendRed) / (1F - blendAlpha)
    val unblendGreen = (green - blendAlpha * blendGreen) / (1F - blendAlpha)
    val unblendBlue = (blue - blendAlpha * blendBlue) / (1F - blendAlpha)
    return Color.argb(
        255,
        min(255, unblendRed.roundToInt()),
        min(255, unblendGreen.roundToInt()),
        min(255, unblendBlue.roundToInt())
    )
}

inline fun Float.intAlpha(): Int = (this * 255F).roundToInt()

inline fun Int.adjust(percent: Float): Int {
    return Color.rgb(
        (Color.red(this) * percent).roundToInt(),
        (Color.green(this) * percent).roundToInt(),
        (Color.blue(this) * percent).roundToInt()
    )
}

fun Int.adjustAlpha(percent: Float): Int {
    return Color.argb((percent * 255).toInt(), Color.red(this), Color.green(this), Color.blue(this))
}

fun Int.isLight(): Boolean {
    return 1.0 - (0.299 * Color.red(this).toDouble() + 0.587 * Color.green(this).toDouble()
            + 0.114 * Color.blue(this).toDouble()) / 255.0 < 0.4
}