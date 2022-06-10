package de.julianostarek.flow.ui.main.stops.stationboard.merged

import android.view.ViewGroup
import de.julianostarek.flow.ui.common.adapter.base.BaseListAdapter
import de.jlnstrk.transit.common.model.Journey

class MergedJourneysAdapter(
    private val listener: Listener
) : BaseListAdapter<MergedJourney, MergedJourneyViewHolder>(MergedJourneyDiffItemCallback),
    MergedJourneyViewHolder.Observer {

    interface Listener {

        fun onJourneyClicked(journey: Journey)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MergedJourneyViewHolder {
        return MergedJourneyViewHolder(parent)
    }

    override fun onMergedJourneyClicked(viewHolder: MergedJourneyViewHolder) {
        // TODO
    }

    override fun onJourneyClicked(viewHolder: MergedJourneyViewHolder, journey: Journey) {
        listener.onJourneyClicked(journey)
    }

    fun submitJourneys(journeys: List<Journey>?) {
        submitList(journeys?.merged())
    }

}