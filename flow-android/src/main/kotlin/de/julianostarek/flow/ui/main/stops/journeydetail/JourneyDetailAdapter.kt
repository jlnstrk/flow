package de.julianostarek.flow.ui.main.stops.journeydetail

import android.view.ViewGroup
import de.julianostarek.flow.ui.common.adapter.base.BaseListAdapter
import de.julianostarek.flow.ui.common.diff.StopDiffItemCallback
import de.julianostarek.flow.ui.main.trips.detail.viewholder.IntermediateStopViewHolder
import de.jlnstrk.transit.common.model.stop.Stop

class JourneyDetailAdapter(
    private val listener: Listener
) : BaseListAdapter<Stop, IntermediateStopViewHolder>(StopDiffItemCallback) {

    interface Listener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IntermediateStopViewHolder {
        return IntermediateStopViewHolder(parent)
    }

}