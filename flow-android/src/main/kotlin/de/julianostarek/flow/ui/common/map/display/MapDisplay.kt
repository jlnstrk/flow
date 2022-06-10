package de.julianostarek.flow.ui.common.map.display

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import de.julianostarek.flow.ui.common.map.SequenceMapView
import de.julianostarek.flow.util.transit.asMaps
import de.jlnstrk.transit.common.model.stop.Stop

sealed class MapDisplay {
    val zoomedMarkers = mutableListOf<Marker>()
    val permanentMarkers = mutableListOf<Marker>()
    val polylines = mutableListOf<Polyline>()

    protected fun installMarker(
        mapView: SequenceMapView,
        map: GoogleMap,
        marker: MarkerOptions,
        tag: Any,
        zoomed: Boolean = true
    ) {
        val withVisibility = if (zoomed) {
            marker.visible(mapView.markersVisible)
        } else marker
        val added = map.addMarker(withVisibility) ?: return
        added.tag = tag
        if (zoomed) {
            zoomedMarkers.add(added)
        } else {
            permanentMarkers.add(added)
        }
    }

    protected fun installIntermediateStop(
        mapView: SequenceMapView,
        map: GoogleMap,
        stop: Stop
    ) {
        val markerOptions = mapView.newIntermediateStop()
            .title(stop.location.name)
            .position(stop.location.coordinates?.asMaps()!!)
        installMarker(mapView, map, markerOptions, stop, zoomed = true)
    }

    protected fun installChangeStop(
        mapView: SequenceMapView,
        map: GoogleMap,
        stop: Stop,
        position: LatLng,
    ) {
        val markerOptions = mapView.newChangeStop()
            .title(stop.location.name)
            .position(position)
        installMarker(mapView, map, markerOptions, stop, zoomed = false)
    }

    protected fun installPolyline(
        mapView: SequenceMapView,
        map: GoogleMap,
        polylineOptions: PolylineOptions
    ) {
        val polyline = map.addPolyline(polylineOptions)
        polylines.add(polyline)
    }

    abstract fun install(mapView: SequenceMapView, map: GoogleMap): LatLngBounds

    fun uninstall() {
        zoomedMarkers.forEach(Marker::remove)
        permanentMarkers.forEach(Marker::remove)
        polylines.forEach(Polyline::remove)
    }

}