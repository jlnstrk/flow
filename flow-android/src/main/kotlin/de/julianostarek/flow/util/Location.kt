package de.julianostarek.flow.util

import android.location.Location
import com.google.android.gms.maps.model.LatLng
import de.julianostarek.flow.persist.model.EmbeddedCoordinates
import de.jlnstrk.transit.common.model.Coordinates

typealias AndroidLocation = Location

inline fun AndroidLocation.asCommon(): Coordinates =
    Coordinates(latitude, longitude)

inline fun AndroidLocation.asLatLng(): LatLng =
    LatLng(latitude, longitude)

inline fun AndroidLocation.asTransit(): EmbeddedCoordinates =
    EmbeddedCoordinates(latitude, longitude)
