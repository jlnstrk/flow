package de.julianostarek.flow.util.transit

import android.content.Context
import de.jlnstrk.transit.common.model.ProductClass
import de.julianostarek.flow.util.context.styles
import de.julianostarek.flow.util.iconRawOrRegularResId
import de.julianostarek.flow.util.iconResId

inline fun ProductClass.iconRes(context: Context): Int {
    val style = context.styles.resolveProductStyle(this)
    return style.iconResId(context)
}

inline fun ProductClass.iconRawRes(context: Context): Int {
    val style = context.styles.resolveProductStyle(this)
    return style.iconRawOrRegularResId(context)
}

inline fun ProductClass.defaultColor(context: Context): Int =
    context.styles.resolveProductStyle(this).productColor