package de.julianostarek.flow.ui.common.diff

import androidx.recyclerview.widget.DiffUtil
import de.jlnstrk.transit.common.model.Leg

object PublicLegDiffItemCallback : DiffUtil.ItemCallback<Leg.Public>() {

    enum class Signal {
        COUNTDOWN,
        DEPARTURE_TIME,
        ARRIVAL_TIME,
        STATUS,
        STOPS
    }

    override fun areItemsTheSame(oldItem: Leg.Public, newItem: Leg.Public): Boolean {
        throw UnsupportedOperationException()
    }

    override fun areContentsTheSame(oldItem: Leg.Public, newItem: Leg.Public): Boolean {
        throw UnsupportedOperationException()
    }

}