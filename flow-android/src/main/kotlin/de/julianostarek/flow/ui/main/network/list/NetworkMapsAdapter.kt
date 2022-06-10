package de.julianostarek.flow.ui.main.network.list

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import de.julianostarek.flow.ui.common.diff.NetworkMapDiffItemCallback
import de.jlnstrk.transit.common.model.NetworkMap

class NetworkMapsAdapter : ListAdapter<NetworkMap, NetworkMapViewHolder>(NetworkMapDiffItemCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NetworkMapViewHolder {
        return NetworkMapViewHolder(parent)
    }

    override fun onBindViewHolder(holder: NetworkMapViewHolder, position: Int) {
        // do nothing
    }

    override fun onBindViewHolder(
        holder: NetworkMapViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        val item = getItem(position)
        holder.bindTo(item, payloads)
    }

    override fun onViewRecycled(holder: NetworkMapViewHolder) {
        holder.unbind()
    }

}