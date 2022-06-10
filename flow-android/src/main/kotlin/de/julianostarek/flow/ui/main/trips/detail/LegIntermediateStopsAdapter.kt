package de.julianostarek.flow.ui.main.trips.detail

import android.view.ViewGroup
import de.julianostarek.flow.ui.common.adapter.base.BaseListAdapter
import de.julianostarek.flow.ui.common.diff.StopDiffItemCallback
import de.julianostarek.flow.ui.main.stops.journeydetail.JourneyStopViewHolder
import de.jlnstrk.transit.common.model.stop.Stop

class LegIntermediateStopsAdapter :
    BaseListAdapter<Stop, JourneyStopViewHolder>(StopDiffItemCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JourneyStopViewHolder {
        return JourneyStopViewHolder(parent)
    }

}