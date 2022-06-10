package de.julianostarek.flow.util.view

import de.julianostarek.flow.ui.common.time.TimeView
import de.julianostarek.flow.util.transit.isCancelled
import de.julianostarek.flow.util.transit.realtime
import de.julianostarek.flow.util.transit.scheduled
import de.jlnstrk.transit.common.model.stop.Stop

inline fun TimeView.setDisplayStop(stop: Stop) {
    return setDisplayTime(stop.scheduled, stop.realtime, stop.isCancelled)
}