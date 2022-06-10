package de.julianostarek.flow.ui.main.trips.detail.viewholder

import android.view.ViewGroup
import de.julianostarek.flow.R
import de.julianostarek.flow.databinding.ItemJourneyOverviewBinding
import de.julianostarek.flow.ui.common.viewholder.base.BindingViewHolder
import de.jlnstrk.transit.common.model.Journey
import de.jlnstrk.transit.common.model.stop.Stop

class JourneyOverviewViewHolder(parent: ViewGroup) :
    BindingViewHolder<Journey, ItemJourneyOverviewBinding>(
        parent,
        ItemJourneyOverviewBinding::inflate
    ) {

    override fun bindTo(data: Journey) {
        viewBinding.lineGroup.setLines(listOf(data.line))
        viewBinding.direction.text = (data.directionTo ?: data.directionFrom)!!.name
        val origin = data.stops?.first() as Stop.Departure
        val destination = data.stops?.last() as Stop.Arrival
        val legDuration =
            (destination.arrivalScheduled - origin.departureScheduled).inWholeMinutes
        val stopCount = data.stops?.size!!
        val summaryString = viewBinding.direction.context.resources.getQuantityString(
            R.plurals.stops, stopCount, stopCount, legDuration
        )
        viewBinding.stops.text = summaryString
    }

    override fun unbind() {
        super.unbind()
        viewBinding.direction.text = null
        viewBinding.stops.text = null
    }

}