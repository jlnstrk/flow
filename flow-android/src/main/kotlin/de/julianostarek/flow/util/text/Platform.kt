package de.julianostarek.flow.util.text

import android.content.Context
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StrikethroughSpan
import de.julianostarek.flow.R
import de.jlnstrk.transit.common.model.TransportMode
import de.jlnstrk.transit.common.model.ProductClass
import de.jlnstrk.transit.common.model.stop.Stop

/**
 * Formats Stop platforms
 *
 * Case Arrival, Departure
 * Only scheduled, or realtime equals: "Platform X"
 * Realtime differs: "Platform <strike>X</strike> Y"
 *
 * Case HciColor
 * e.g. "Arr Platform X, Dep Platform <strike>X</strike> Y"
 *
 */
fun Stop.formatPlatforms(
    context: Context,
    product: ProductClass = TransportMode.TRAIN
): CharSequence {
    val spannableBuilder = SpannableStringBuilder()
    when (this) {
        is Stop.Arrival -> {
            spannableBuilder.appendPlatform(
                context,
                product,
                arrivalScheduledPlatform,
                arrivalRealtimePlatform
            )
        }
        is Stop.Departure -> spannableBuilder.appendPlatform(
            context,
            product,
            departureScheduledPlatform,
            departureRealtimePlatform
        )
        is Stop.Intermediate -> {
            if ((arrivalRealtimePlatform ?: arrivalScheduledPlatform) != (departureRealtimePlatform ?: departureScheduledPlatform)) {
                spannableBuilder.appendStringRes(context, R.string.mode_arrival_short)
                spannableBuilder.appendSpace()
                spannableBuilder.appendPlatform(
                    context,
                    product,
                    arrivalScheduledPlatform,
                    arrivalRealtimePlatform
                )
                spannableBuilder.appendSeparator()
                spannableBuilder.appendStringRes(context, R.string.mode_departure_short)
                spannableBuilder.appendSpace()
                spannableBuilder.appendPlatform(
                    context,
                    product,
                    departureScheduledPlatform,
                    departureRealtimePlatform
                )
            } else spannableBuilder.appendPlatform(
                context,
                product,
                departureScheduledPlatform,
                departureRealtimePlatform
            )
        }
        else -> {}
    }
    return spannableBuilder
}

private fun SpannableStringBuilder.appendPlatform(
    context: Context,
    product: ProductClass,
    scheduled: String?,
    realtime: String?
) {
    appendStringRes(
        context,
        when (product.mode) {
            TransportMode.BUS -> R.string.platform_bus
            else -> R.string.platform_rail
        },
    )
    appendSpace()
    if (realtime != null && realtime != scheduled) {
        val span = StrikethroughSpan()
        append(scheduled, span, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        appendSpace()
    }
    append(realtime ?: scheduled)
}