package de.julianostarek.flow.ui.common.diff

import androidx.recyclerview.widget.DiffUtil
import de.jlnstrk.transit.common.model.Trip

object TripDiffItemCallback : DiffUtil.ItemCallback<Trip>() {

    enum class Signal {
        COUNTDOWN,
        REALTIME,
        STATUS
    }

    override fun areItemsTheSame(oldItem: Trip, newItem: Trip): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Trip, newItem: Trip): Boolean {
        return oldItem.isRideable == newItem.isRideable
                && oldItem.departure.departureRealtime == newItem.departure.departureRealtime
                && oldItem.arrival.arrivalRealtime == newItem.arrival.arrivalRealtime
    }

    override fun getChangePayload(oldItem: Trip, newItem: Trip): Any? {
        if (oldItem.isRideable != newItem.isRideable) {
            return Signal.STATUS
        }
        if (oldItem.departure.departureRealtime != newItem.departure.departureRealtime
            || oldItem.arrival.arrivalRealtime != newItem.arrival.arrivalRealtime
        ) {
            return Signal.REALTIME
        }
        return null
    }

}