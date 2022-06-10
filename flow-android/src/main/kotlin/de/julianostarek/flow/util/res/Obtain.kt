package de.julianostarek.flow.util.res

import android.content.Context

fun Context.resolveResId(vararg attrs: Int): IntArray {
    val styledAttrs = obtainStyledAttributes(attrs)
    val result = IntArray(attrs.size) {
        styledAttrs.getResourceId(it, 0)
    }
    styledAttrs.recycle()
    return result
}