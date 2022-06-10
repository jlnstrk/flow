package de.julianostarek.flow.ui.locationsearch

import android.view.ViewGroup
import de.julianostarek.flow.ui.common.adapter.base.BasePagingDataAdapter
import de.julianostarek.flow.ui.common.diff.location.LocationDiffItemCallback
import de.jlnstrk.transit.common.model.Location

class OfflineLocationsAdapter(
    private val listener: Listener
) : BasePagingDataAdapter<Location, LocationViewHolder>(LocationDiffItemCallback),
    LocationViewHolder.Observer {

    interface Listener {
        fun onLocationClicked(location: Location)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder {
        return LocationViewHolder(parent)
    }

    override fun onLocationClicked(location: Location) {
        listener.onLocationClicked(location)
    }

}