package de.julianostarek.flow.util.view

import com.google.android.material.R
import com.google.android.material.snackbar.Snackbar

fun Snackbar.adjustTheme(): Snackbar {
    val attrs = context.theme.obtainStyledAttributes(
        intArrayOf(
            R.attr.colorPrimary,
        R.attr.colorSurface)
    )
    val colorPrimary = attrs.getColor(0, 0)
    val colorSurface = attrs.getColor(1, 0)
    setTextColor(colorPrimary)
    setActionTextColor(colorPrimary)
    setBackgroundTint(colorSurface)
    return this
}