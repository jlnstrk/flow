package de.julianostarek.flow.ui.common.viewholder.base

import de.julianostarek.flow.util.AndroidLocation

interface LocationAware {
    fun onReferenceLocationChanged(location: AndroidLocation)
}