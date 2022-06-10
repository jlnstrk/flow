package de.julianostarek.flow.ui.common.map.display

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLngBounds
import de.julianostarek.flow.ui.common.map.SequenceMapView
import de.julianostarek.flow.util.context.styles
import de.julianostarek.flow.util.graphics.adjust
import de.julianostarek.flow.util.graphics.dp
import de.julianostarek.flow.util.transit.asMaps
import de.jlnstrk.transit.common.model.Coordinates
import de.jlnstrk.transit.common.response.JourneyDetailsData

class JourneyDisplay(private val journeyDetails: JourneyDetailsData) : MapDisplay() {

    override fun install(mapView: SequenceMapView, map: GoogleMap): LatLngBounds {
        val boundsBuilder = LatLngBounds.Builder()
        val firstStop = journeyDetails.journey.stops.first()
        val lastStop = journeyDetails.journey.stops.last()
        val polyline = journeyDetails.journey.polyline?.coordinates
            .orEmpty()
            .map(Coordinates::asMaps)
        if (polyline.isNotEmpty()) {
            polyline.forEach(boundsBuilder::include)
            val productStyle = mapView.context.styles.resolveProductStyle(journeyDetails.journey.line.product)
            val color = productStyle.productColor
            val borderLine = mapView.newPolyline()
                .addAll(polyline)
                .color(color.adjust(0.6F))
            val centerLine = mapView.newPolyline()
                .addAll(polyline)
                .color(color)
                .width(6F.dp(mapView))
            installPolyline(mapView, map, borderLine)
            installPolyline(mapView, map, centerLine)
            installChangeStop(mapView, map, firstStop, polyline.first())
            installChangeStop(mapView, map, lastStop, polyline.last())
        } else {
            installChangeStop(mapView, map, firstStop, firstStop.location.coordinates?.asMaps()!!)
            installChangeStop(mapView, map, lastStop, lastStop.location.coordinates?.asMaps()!!)
        }
        journeyDetails.journey.stops.mapIndexedNotNull { index, stop ->
            if (index > 0 && index < journeyDetails.journey.stops.lastIndex) {
                installIntermediateStop(mapView, map, stop)
            }
            stop.location.coordinates
        }
            .map(Coordinates::asMaps)
            .forEach(boundsBuilder::include)
        return boundsBuilder.build()
    }

}