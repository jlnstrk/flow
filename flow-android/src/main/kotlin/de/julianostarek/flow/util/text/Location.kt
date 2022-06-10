package de.julianostarek.flow.util.text

import android.content.Context
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.widget.TextView
import de.jlnstrk.transit.common.model.Location

fun Location.formatName(
    context: Context,
    captionSpan: Any? = null,
    target: TextView? = null,
    nameNewline: Boolean = true,
    productsNewline: Boolean = false
): CharSequence {
    val builder = SpannableStringBuilder()
    if (place != null) {
        builder.append(place)
        if (nameNewline) {
            builder.append('\n')
        }
        builder.setSpan(
            captionSpan,
            0,
            builder.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        if (!nameNewline) {
            builder.append(' ')
        }
    }
    builder.append(name)
    builder.appendProducts(
        context,
        (this as? Location.Station)?.products,
        target,
        newline = productsNewline
    )
    return builder
}