package de.julianostarek.flow.util.text

import de.julianostarek.flow.R
import de.jlnstrk.transit.common.service.TripSearchService

val TripSearchService.Error?.messageRes: Int
    get() = when (this) {
        TripSearchService.Error.TOO_CLOSE -> R.string.error_too_close
        TripSearchService.Error.INVALID_ORIGIN -> R.string.error_invalid_origin
        TripSearchService.Error.INVALID_DESTINATION -> R.string.error_invalid_destination
        TripSearchService.Error.INVALID_VIA -> R.string.error_invalid_via
        TripSearchService.Error.INVALID_LOCATION -> R.string.error_invalid_location
        TripSearchService.Error.INVALID_DATETIME -> R.string.error_invalid_datetime
        else -> R.string.error_internal
    }