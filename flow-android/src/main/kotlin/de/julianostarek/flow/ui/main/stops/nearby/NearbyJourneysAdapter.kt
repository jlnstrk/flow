package de.julianostarek.flow.ui.main.stops.nearby

import android.view.ViewGroup
import de.julianostarek.flow.ui.common.adapter.base.BaseListAdapter
import de.julianostarek.flow.ui.common.diff.JourneyDiffItemCallback
import de.jlnstrk.transit.common.model.Journey

class NearbyJourneysAdapter(
    private val listener: Listener
) : BaseListAdapter<Journey, NearbyJourneyViewHolder>(JourneyDiffItemCallback),
    NearbyJourneyViewHolder.Observer {

    fun interface Listener {
        fun onJourneyClicked(journey: Journey)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NearbyJourneyViewHolder {
        return NearbyJourneyViewHolder(parent)
    }

    override fun onJourneyClicked(viewHolder: NearbyJourneyViewHolder) {
        listener.onJourneyClicked(viewHolder.data!!)
    }

}