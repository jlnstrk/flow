package de.julianostarek.flow.ui.common.adapter.base

import androidx.annotation.CallSuper
import de.julianostarek.flow.ui.common.viewholder.base.LocationAware
import de.julianostarek.flow.ui.common.viewholder.base.TimeAware
import de.julianostarek.flow.util.AndroidLocation

interface BaseAdapterContract : LocationAware, TimeAware {
    var referenceLocation: AndroidLocation?

    @CallSuper
    override fun onReferenceLocationChanged(location: AndroidLocation) {
        this.referenceLocation = location
    }

    override fun onTimeTick() = Unit

}