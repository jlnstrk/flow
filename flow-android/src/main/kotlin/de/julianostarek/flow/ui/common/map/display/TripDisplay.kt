package de.julianostarek.flow.ui.common.map.display

import android.graphics.Color
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLngBounds
import de.julianostarek.flow.ui.common.map.SequenceMapView
import de.julianostarek.flow.util.context.styles
import de.julianostarek.flow.util.graphics.adjust
import de.julianostarek.flow.util.graphics.dp
import de.julianostarek.flow.util.transit.asMaps
import de.jlnstrk.transit.common.model.*

data class TripDisplay(val trip: Trip) : MapDisplay() {

    override fun install(mapView: SequenceMapView, map: GoogleMap): LatLngBounds {
        val boundsBuilder = LatLngBounds.builder()
        for (leg in trip.legs) {
            boundsBuilder.include(leg.departure.location.coordinates!!.asMaps())
                .include(leg.arrival.location.coordinates!!.asMaps())
            when (leg) {
                is Leg.Public -> {
                    val color = mapView.context.styles
                        .resolveProductStyle(leg.journey.line.product)
                        .productColor
                    val polylineOptions = leg.journey.polyline?.coordinates
                        .orEmpty()
                        .map(Coordinates::asMaps)
                    if (!polylineOptions.isNullOrEmpty()) {
                        val borderLine = mapView.newPolyline()
                            .addAll(polylineOptions)
                            .color(color.adjust(0.6F))
                        val innerLine = mapView.newPolyline()
                            .addAll(polylineOptions)
                            .color(color)
                            .width(6F.dp(mapView))
                        installChangeStop(mapView, map, leg.departure, polylineOptions.first())
                        installChangeStop(mapView, map, leg.arrival, polylineOptions.last())
                        installPolyline(mapView, map, borderLine)
                        installPolyline(mapView, map, innerLine)
                    }
                    if ((leg.journey.stops?.size ?: 0) >= 3) {
                        val intermediateStops = leg.journey.stops!!
                            .subList(1, leg.journey.stops!!.size - 1)
                        for (stop in intermediateStops) {
                            installIntermediateStop(mapView, map, stop)
                        }
                    }
                }
                is Leg.Individual -> {
                    val fromPosition = leg.departure.location.coordinates!!.asMaps()
                    val toPosition = leg.arrival.location.coordinates!!.asMaps()
                    val polylineOptions = mapView.newPolyline()
                        .add(fromPosition)
                        .add(toPosition)
                        .color(Color.GRAY)
                    installPolyline(mapView, map, polylineOptions)
                    installChangeStop(mapView, map, leg.departure, fromPosition)
                    installChangeStop(mapView, map, leg.arrival, toPosition)
                }
                else -> {}
            }
        }
        trip.legs
            .asSequence()
            .filterIsInstance<Leg.Public>()
            .mapNotNull(Leg.Public::journey)
            .mapNotNull(Journey::polyline)
            .mapNotNull(Polyline::coordinates)
            .flatten()
            .map(Coordinates::asMaps)
            .toList()
            .forEach(boundsBuilder::include)
        return boundsBuilder.build()
    }

}