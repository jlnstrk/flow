package de.julianostarek.flow.ui.common.diff

import androidx.recyclerview.widget.DiffUtil
import de.julianostarek.flow.persist.model.RouteEntity
import de.julianostarek.flow.ui.common.diff.location.LocationDiffItemCallback
import de.julianostarek.flow.util.allIndexed

object RouteDiffItemCallback : DiffUtil.ItemCallback<RouteEntity>() {

    enum class Signal {
        LOCATIONS,
        FAVORITE
    }

    override fun areItemsTheSame(oldItem: RouteEntity, newItem: RouteEntity): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: RouteEntity, newItem: RouteEntity): Boolean {
        return oldItem.isFavorite == newItem.isFavorite
                && LocationDiffItemCallback.areItemsTheSame(oldItem.origin, newItem.origin)
                && LocationDiffItemCallback.areContentsTheSame(oldItem.origin, newItem.origin)
                && LocationDiffItemCallback.areItemsTheSame(
            oldItem.destination,
            newItem.destination
        )
                && LocationDiffItemCallback.areContentsTheSame(
            oldItem.destination,
            newItem.destination
        )
                && oldItem.via.size == newItem.via.size
                && oldItem.via.allIndexed { index, oldVia ->
            val newVia = newItem.via[index]
            LocationDiffItemCallback.areItemsTheSame(oldVia.location, newVia.location)
                    && LocationDiffItemCallback.areContentsTheSame(oldVia.location, newVia.location)
        }
    }

    override fun getChangePayload(oldItem: RouteEntity, newItem: RouteEntity): Any? {
        if (oldItem.isFavorite != newItem.isFavorite) {
            return Signal.FAVORITE
        }
        return null
    }

}