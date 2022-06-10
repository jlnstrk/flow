package de.julianostarek.flow.ui.main.stops.stationboard.chronologic

import android.view.ViewGroup
import de.julianostarek.flow.ui.common.adapter.base.BaseListAdapter
import de.julianostarek.flow.ui.common.diff.JourneyDiffItemCallback
import de.jlnstrk.transit.common.model.Journey

class ChronologicJourneysAdapter(
    private val listener: Listener
) : BaseListAdapter<Journey, ChronologicJourneyViewHolder>(JourneyDiffItemCallback),
    ChronologicJourneyViewHolder.Observer {

    fun interface Listener {
        fun onJourneyClicked(journey: Journey)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ChronologicJourneyViewHolder {
        return ChronologicJourneyViewHolder(parent)
    }

    override fun onJourneyClicked(viewHolder: ChronologicJourneyViewHolder) {
        listener.onJourneyClicked(viewHolder.data!!)
    }

}