package de.julianostarek.flow.util.transit

import android.content.Context
import de.jlnstrk.transit.common.model.Line

inline fun Line.iconRes(context: Context): Int {
    return product.iconRes(context)
}

inline fun Line.iconRawRes(context: Context): Int {
    return product.iconRawRes(context)
}

inline fun Line.defaultColor(context: Context): Int {
    return product.defaultColor(context)
}