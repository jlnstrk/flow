package de.julianostarek.flow.ui.main.stops.nearby

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import de.julianostarek.flow.ui.common.adapter.base.BaseListAdapter
import de.julianostarek.flow.util.AndroidLocation
import de.jlnstrk.transit.common.model.Journey
import de.jlnstrk.transit.common.model.Location

class NearbyDeparturesAdapter(
    private val listener: Listener
) : BaseListAdapter<Location, NearbyDeparturesViewHolder>(ItemCallback),
    NearbyDeparturesViewHolder.Observer {
    private val journeyViewPool: RecyclerView.RecycledViewPool = RecyclerView.RecycledViewPool()
    private var map: Map<Location, List<Journey>>? = null

    interface Listener {

        fun onLocationClicked(location: Location)

        fun onJourneyClicked(journey: Journey)

    }

    fun submitDepartures(list: List<Journey>?) {
        val container = AndroidLocation(null as String?)
        map = list
            ?.sortedBy {
                container.latitude = it.stop.location.coordinates!!.latitude
                container.longitude = it.stop.location.coordinates!!.longitude
                referenceLocation?.distanceTo(container)
            }
            ?.groupByTo(LinkedHashMap()) { it.stop.location }
            ?.mapValues { it.value.take(3) }
        submitList(map?.keys?.toList())
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NearbyDeparturesViewHolder {
        val viewHolder = NearbyDeparturesViewHolder(parent)
        viewHolder.viewBinding.recyclerView.setRecycledViewPool(journeyViewPool)
        return viewHolder
    }

    override fun onLocationClicked(viewHolder: NearbyDeparturesViewHolder) {
        listener.onLocationClicked(viewHolder.data!!)
    }

    override fun onJourneyClicked(viewHolder: NearbyDeparturesViewHolder, journey: Journey) {
        listener.onJourneyClicked(journey)
    }

    override fun onBindViewHolder(
        holder: NearbyDeparturesViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        super.onBindViewHolder(holder, position, payloads)
        if (payloads.isNotEmpty()) {
            return
        }
        val journeys = map?.get(getItem(position))
        if (journeys != null) {
            holder.bindToJourneys(journeys)
        }
    }

    object ItemCallback : DiffUtil.ItemCallback<Location>() {

        override fun areItemsTheSame(oldItem: Location, newItem: Location): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Location, newItem: Location): Boolean {
            return oldItem == newItem
        }

        override fun getChangePayload(oldItem: Location, newItem: Location): Any? {
            return null // TODO: Implement
        }
    }

}