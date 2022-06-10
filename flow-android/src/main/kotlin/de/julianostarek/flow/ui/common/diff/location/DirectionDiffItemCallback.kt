package de.julianostarek.flow.ui.common.diff.location

import androidx.recyclerview.widget.DiffUtil
import de.jlnstrk.transit.common.model.Location

object DirectionDiffItemCallback : DiffUtil.ItemCallback<Location>() {

    override fun areItemsTheSame(oldItem: Location, newItem: Location): Boolean {
        return oldItem.place == newItem.place
                && oldItem.name == newItem.name
    }

    override fun areContentsTheSame(oldItem: Location, newItem: Location): Boolean {
        return true
    }

    override fun getChangePayload(oldItem: Location, newItem: Location): Any? {
        return null
    }

}