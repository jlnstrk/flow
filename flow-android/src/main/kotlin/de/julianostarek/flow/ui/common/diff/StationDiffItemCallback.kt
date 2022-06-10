package de.julianostarek.flow.ui.common.diff

import androidx.recyclerview.widget.DiffUtil
import de.jlnstrk.transit.common.model.Location

object StationDiffItemCallback : DiffUtil.ItemCallback<Location.Station>() {

    override fun areItemsTheSame(oldItem: Location.Station, newItem: Location.Station): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Location.Station, newItem: Location.Station): Boolean {
        return oldItem == newItem
    }

    override fun getChangePayload(oldItem: Location.Station, newItem: Location.Station): Any? {
        return null // TODO: Implement
    }

}