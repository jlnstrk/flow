package de.julianostarek.flow.util.graphics

import android.content.Context
import android.graphics.Rect
import android.graphics.drawable.Drawable
import kotlin.math.min
import kotlin.math.roundToInt

private const val SYSTEM_ICON_SIZE_DP: Float = 24F
private const val SYSTEM_ICON_PADDING_DP: Float = 2F

fun Drawable.setBoundsScaledWidth(height: Int) {
    setBounds(0, 0, ((height.toFloat() / intrinsicHeight) * intrinsicWidth).roundToInt(), height)
}

fun Drawable.setBoundsScaledHeight(width: Int) {
    setBounds(0, 0, width, ((width.toFloat() / intrinsicWidth) * intrinsicHeight).roundToInt())
}

fun Drawable.findBalancedIconScale(context: Context): Rect {
    val height = (SYSTEM_ICON_SIZE_DP - 2 * SYSTEM_ICON_PADDING_DP).dp(context)
    val maxScale = height / intrinsicHeight
    val desiredScale = (height * height) / (intrinsicWidth * intrinsicHeight)
    val scale = min(maxScale, desiredScale)
    return Rect(
        0,
        0,
        (intrinsicWidth * scale).roundToInt(),
        (intrinsicHeight * scale).roundToInt()
    )
}