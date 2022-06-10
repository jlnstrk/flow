package de.julianostarek.flow.ui.main.trips.results.simple

import android.view.ViewGroup
import de.julianostarek.flow.ui.common.adapter.base.BaseListAdapter
import de.julianostarek.flow.ui.common.diff.TripDiffItemCallback
import de.jlnstrk.transit.common.model.Trip

class SimpleTripAdapter(
    private val listener: Listener
) : BaseListAdapter<Trip, SimpleTripViewHolder>(TripDiffItemCallback),
    SimpleTripViewHolder.Observer {

    interface Listener {
        fun onTripClicked(trip: Trip)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimpleTripViewHolder {
        return SimpleTripViewHolder(parent)
    }

    override fun onTripClicked(viewHolder: SimpleTripViewHolder) {
        listener.onTripClicked(viewHolder.data!!)
    }

}