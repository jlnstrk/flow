package de.julianostarek.flow.ui.common.diff

import androidx.recyclerview.widget.DiffUtil
import de.jlnstrk.transit.common.model.Journey
import de.jlnstrk.transit.common.model.stop.Stop

object JourneyDiffItemCallback : DiffUtil.ItemCallback<Journey>() {

    enum class ChangeSignal {
        TYPE,
        STATUS,
        REALTIME
    }

    override fun areItemsTheSame(oldItem: Journey, newItem: Journey): Boolean = oldItem.id == newItem.id

    override fun areContentsTheSame(
        oldItem: Journey,
        newItem: Journey
    ): Boolean {
        return oldItem.directionTo == newItem.directionTo
                && oldItem.line == newItem.line
                && oldItem.stop == newItem.stop
                && oldItem.isCancelled == newItem.isCancelled
    }

    override fun getChangePayload(
        oldItem: Journey,
        newItem: Journey
    ): Any? {
        if (oldItem.stop::class.java != newItem.stop::class.java
            || oldItem.directionTo != newItem.directionTo
        ) {
            return ChangeSignal.TYPE
        }
        if (oldItem.isCancelled != newItem.isCancelled)
            return ChangeSignal.STATUS
        when (newItem.stop) {
            is Stop.Arrival -> {
                val oldStop = oldItem.stop as Stop.Arrival
                val newStop = newItem.stop as Stop.Arrival
                if (oldStop.arrivalRealtime != newStop.arrivalRealtime
                    || oldStop.arrivalRealtimePlatform != newStop.arrivalRealtimePlatform
                    || oldStop.arrivalCancelled != newStop.arrivalCancelled
                ) {
                    return ChangeSignal.REALTIME
                }
            }
            is Stop.Departure -> {
                val oldStop = oldItem.stop as Stop.Departure
                val newStop = newItem.stop as Stop.Departure
                if (oldStop.departureRealtime != newStop.departureRealtime
                    || oldStop.departureRealtimePlatform != newStop.departureRealtimePlatform
                    || oldStop.departureCancelled != newStop.departureCancelled
                ) {
                    return ChangeSignal.REALTIME
                }
            }
            is Stop.Intermediate,
            is Stop.Passing -> throw IllegalStateException()
        }
        return null
    }
}