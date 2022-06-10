package de.julianostarek.flow.util.text

import android.content.Context
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.widget.TextView
import androidx.core.text.bold
import androidx.core.text.color
import de.jlnstrk.transit.common.model.ProductClass
import de.jlnstrk.transit.common.model.stop.BaseArrival
import de.jlnstrk.transit.common.model.stop.BaseDeparture
import de.jlnstrk.transit.common.model.stop.Stop
import de.jlnstrk.transit.common.util.arrivalEffective
import de.jlnstrk.transit.common.util.departureEffective
import de.julianostarek.flow.profile.StyledProfile
import de.julianostarek.flow.ui.common.span.ProductIconSpan
import de.julianostarek.flow.ui.common.time.util.toSystemLocal
import de.julianostarek.flow.util.context.styles
import de.julianostarek.flow.util.datetime.TIME_FORMAT_HH_MM
import de.julianostarek.flow.util.iconResId
import de.julianostarek.flow.util.transit.realtimeColor
import java.util.*

inline fun SpannableStringBuilder.appendActualTime(
    context: Context,
    stop: Stop
): SpannableStringBuilder {
    when (stop) {
        is Stop.Arrival -> return appendActualArrivalTime(context, stop)
        is Stop.Departure -> return appendActualDepartureTime(context, stop)
        else -> throw IllegalStateException()
    }
    return this
}

inline fun SpannableStringBuilder.appendActualDepartureTime(
    context: Context,
    stop: BaseDeparture
): SpannableStringBuilder {
    val formatted = TIME_FORMAT_HH_MM.formatDateTime(stop.departureEffective.toSystemLocal())
    if (stop.departureRealtime != null) {
        val color = stop.realtimeColor(context)
        append(
            formatted, ForegroundColorSpan(color),
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    } else append(formatted)
    return this
}

inline fun SpannableStringBuilder.appendActualArrivalTime(
    context: Context,
    stop: BaseArrival
): SpannableStringBuilder {
    val formatted = TIME_FORMAT_HH_MM.formatDateTime(stop.arrivalEffective.toSystemLocal())
    if (stop.arrivalRealtime != null) {
        val color = stop.realtimeColor(context)
        append(
            formatted, ForegroundColorSpan(color),
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    } else append(formatted)
    return this
}

fun SpannableStringBuilder.prependProducts(
    context: Context,
    products: Set<ProductClass>?,
    target: TextView
): SpannableStringBuilder {
    products
        ?.forEachIndexed { index, category ->
            val productStyle = context.styles.resolveProductStyle(category)
            if (index > 0) {
                append('\u00A0')
            }
            append(
                ".",
                ProductIconSpan(
                    context,
                    productStyle.iconResId(context),
                    target.paint?.fontMetrics
                ),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
    if (!products.isNullOrEmpty()) {
        append(' ')
    }
    return this
}

fun SpannableStringBuilder.appendProducts(
    context: Context,
    products: Set<ProductClass>?,
    target: TextView? = null,
    newline: Boolean = target == null
): SpannableStringBuilder {
    val styles = products
        ?.map(context.styles::resolveProductStyle)
        ?.distinct()
        ?.sortedBy(StyledProfile.ProductStyle::isFallback)
        ?.toMutableList()
    styles?.remove(StyledProfile.PRODUCT_UNKNOWN)
    styles?.forEachIndexed { index, style ->
        if (index == 0) {
            if (newline) {
                append('\n')
            } else {
                append('\u00A0')
            }
        } else {
            append('\u00A0')
        }
        append(
            ".",
            ProductIconSpan(context, style.iconResId(context), target?.paint?.fontMetrics),
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }
    return this
}

inline fun SpannableStringBuilder.boldInColor(
    color: Int,
    builderAction: SpannableStringBuilder.() -> Unit
) = color(color) { bold(builderAction) }

inline fun SpannableStringBuilder.appendStringRes(
    context: Context,
    stringRes: Int,
    vararg args: Any,
    lowerCase: Boolean = false
): SpannableStringBuilder {
    var string = if (args.isNotEmpty()) {
        context.getString(stringRes, *args)
    } else context.getString(stringRes)
    if (lowerCase) {
        string = string.lowercase(Locale.ROOT)
    }
    return append(string)
}

fun SpannableStringBuilder.appendPluralRes(
    context: Context,
    pluralRes: Int,
    quantity: Int,
    vararg args: Any,
    lowerCase: Boolean = false
): SpannableStringBuilder {
    var string = context.resources.getQuantityString(pluralRes, quantity, *args)
    if (lowerCase) {
        string = string.lowercase(Locale.ROOT)
    }
    return append(string)
}

inline fun SpannableStringBuilder.appendSeparator(): SpannableStringBuilder {
    return append(" •\u00A0")
}

inline fun SpannableStringBuilder.appendSpace(): SpannableStringBuilder {
    return append(' ')
}

inline fun SpannableStringBuilder.appendLineBreak(thenInclusive: Any? = null): SpannableStringBuilder {
    return if (thenInclusive != null) append(
        "\n",
        thenInclusive,
        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
    ) else append('\n')
}