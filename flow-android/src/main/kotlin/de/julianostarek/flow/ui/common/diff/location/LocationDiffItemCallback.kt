package de.julianostarek.flow.ui.common.diff.location

import androidx.recyclerview.widget.DiffUtil
import de.jlnstrk.transit.common.model.Location

object LocationDiffItemCallback : DiffUtil.ItemCallback<Location>() {

    override fun areItemsTheSame(oldItem: Location, newItem: Location): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Location, newItem: Location): Boolean {
        return oldItem == newItem
    }

    override fun getChangePayload(oldItem: Location, newItem: Location): Any? {
        return null
    }

}