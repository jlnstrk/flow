package de.julianostarek.flow.util.text

import android.content.Context
import androidx.core.text.buildSpannedString
import de.jlnstrk.transit.common.model.Leg
import de.jlnstrk.transit.common.model.Trip
import de.jlnstrk.transit.common.model.stop.Stop
import de.julianostarek.flow.R
import de.julianostarek.flow.util.datetime.TIME_FORMAT_HH_MM
import de.julianostarek.flow.util.transit.realtimeColor
import kotlinx.datetime.Clock

fun Trip.formatContext(context: Context): CharSequence? {
    val reference = Clock.System.now()
    val firstTransportLeg = legs.find { it is Leg.Public } as Leg.Public? ?: return null
    val stationOriginOne = firstTransportLeg.departure
    val stationOriginTwo = firstTransportLeg.alternatives
        ?.firstOrNull()
        ?.stops
        ?.firstOrNull() as Stop.Departure?
    val originOneOffset = ((stationOriginOne.departureRealtime
        ?: stationOriginOne.departureScheduled) - reference).inWholeMinutes
    return buildSpannedString {
        if (originOneOffset in 0..59) {
            appendStringRes(context, R.string.product_in)
            append(' ')
            if (stationOriginOne.departureRealtime != null) {
                boldInColor(stationOriginOne.realtimeColor(context)) {
                    appendStringRes(
                        context,
                        R.string.time_in_min_space,
                        originOneOffset
                    )
                }
            } else {
                appendStringRes(
                    context,
                    R.string.time_in_min_space,
                    originOneOffset
                )
            }
            if (stationOriginTwo != null) {
                append(" & ")
                val originTwoOffset = (
                        (stationOriginTwo.departureRealtime
                            ?: stationOriginTwo.departureScheduled) - reference
                        ).inWholeMinutes
                if (originTwoOffset in 0..59) {
                    appendStringRes(
                        context,
                        R.string.product_in,
                        lowerCase = true
                    )
                    append(' ')
                    if (stationOriginTwo.departureRealtime != null) {
                        boldInColor(stationOriginTwo.realtimeColor(context)) {
                            appendStringRes(
                                context,
                                R.string.time_in_min_space,
                                originTwoOffset
                            )
                        }
                    } else {
                        appendStringRes(
                            context,
                            R.string.time_in_min_space,
                            originTwoOffset
                        )
                    }
                } else {
                    appendStringRes(
                        context,
                        R.string.product_at,
                        lowerCase = true
                    )
                    append(' ')
                    val secondDepartureTime = TIME_FORMAT_HH_MM.formatInstant(
                        stationOriginTwo.departureRealtime
                            ?: stationOriginTwo.departureScheduled
                    )
                    if (stationOriginTwo.departureRealtime != null) {
                        boldInColor(stationOriginTwo.realtimeColor(context)) {
                            append(secondDepartureTime)
                        }
                    } else {
                        append(secondDepartureTime)
                    }
                }
            }
        } else {
            appendStringRes(context, R.string.product_at)
            append(' ')
            val firstDepartureTime =
                TIME_FORMAT_HH_MM.formatInstant(
                    stationOriginOne.departureRealtime ?: stationOriginOne.departureScheduled
                )
            if (stationOriginOne.departureRealtime != null) {
                boldInColor(stationOriginOne.realtimeColor(context)) {
                    append(firstDepartureTime)
                }
            } else {
                append(firstDepartureTime)
            }
            if (stationOriginTwo != null) {
                append(" & ")
                appendStringRes(
                    context,
                    R.string.product_at,
                    lowerCase = true
                )
                append(' ')
                val secondDepartureTime = TIME_FORMAT_HH_MM.formatInstant(
                    stationOriginTwo.departureRealtime ?: stationOriginTwo.departureScheduled
                )
                if (stationOriginTwo.departureRealtime != null) {
                    boldInColor(stationOriginTwo.realtimeColor(context)) {
                        append(secondDepartureTime)
                    }
                } else {
                    append(secondDepartureTime)
                }
            }
        }
        append(' ')
        appendStringRes(context, R.string.product_from)
        append(' ')
        append(stationOriginOne.location.name)
    }
}