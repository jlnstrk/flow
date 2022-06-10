package de.julianostarek.flow.ui.main.stops.nearby

import android.view.ViewGroup
import de.julianostarek.flow.ui.common.adapter.base.BaseListAdapter
import de.julianostarek.flow.ui.common.diff.location.LocationDiffItemCallback
import de.julianostarek.flow.ui.common.viewholder.base.LocationAware
import de.jlnstrk.transit.common.model.Location

class NearbyLocationsAdapter(
    private val listener: Listener
) : BaseListAdapter<Location, NearbyLocationViewHolder>(LocationDiffItemCallback),
    NearbyLocationViewHolder.Observer, LocationAware {

    fun interface Listener {
        fun onLocationClicked(location: Location)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NearbyLocationViewHolder {
        return NearbyLocationViewHolder(parent)
    }

    override fun onLocationClicked(viewHolder: NearbyLocationViewHolder) {
        listener.onLocationClicked(viewHolder.data!!)
    }

}