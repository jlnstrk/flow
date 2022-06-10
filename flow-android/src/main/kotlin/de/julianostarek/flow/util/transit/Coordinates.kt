package de.julianostarek.flow.util.transit

import com.google.android.gms.maps.model.LatLng
import de.julianostarek.flow.persist.model.EmbeddedCoordinates
import de.julianostarek.flow.util.AndroidLocation
import de.jlnstrk.transit.common.model.Coordinates

inline fun Coordinates.asAndroid(): AndroidLocation =
    AndroidLocation(null as String?).apply {
        latitude = this@asAndroid.latitude
        longitude = this@asAndroid.longitude
    }

inline fun Coordinates.asTransit(): EmbeddedCoordinates =
    EmbeddedCoordinates(latitude, longitude)

inline fun Coordinates.asMaps(): LatLng =
    LatLng(latitude, longitude)