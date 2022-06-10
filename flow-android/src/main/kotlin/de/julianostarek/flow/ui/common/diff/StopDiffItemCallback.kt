package de.julianostarek.flow.ui.common.diff

import androidx.recyclerview.widget.DiffUtil
import de.jlnstrk.transit.common.model.stop.Stop

object StopDiffItemCallback : DiffUtil.ItemCallback<Stop>() {

    enum class Signal {
        COUNTDOWN
    }

    override fun areItemsTheSame(oldItem: Stop, newItem: Stop): Boolean {
        return false
    }

    override fun areContentsTheSame(oldItem: Stop, newItem: Stop): Boolean {
        if (oldItem::class.java != newItem::class.java) {
            return false
        }
        return when (newItem) {
            is Stop.Arrival -> {
                oldItem as Stop.Arrival
                oldItem.arrivalRealtime == newItem.arrivalRealtime
                        && oldItem.arrivalRealtimePlatform == newItem.arrivalRealtimePlatform
                        && oldItem.arrivalCancelled == newItem.arrivalCancelled
            }
            is Stop.Departure -> {
                oldItem as Stop.Departure
                oldItem.departureRealtime == newItem.departureRealtime
                        && oldItem.departureRealtimePlatform == newItem.departureRealtimePlatform
                        && oldItem.departureCancelled == newItem.departureCancelled
            }
            else -> true
        }
    }

}