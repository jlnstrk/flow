package de.julianostarek.flow.ui.main.stops.stationboard.merged

import android.view.ViewGroup
import android.widget.TextView
import de.julianostarek.flow.databinding.ItemJourneyMergedBinding
import de.julianostarek.flow.ui.common.adapter.base.BaseChangeSignal
import de.julianostarek.flow.ui.common.time.TimeDisplay
import de.julianostarek.flow.ui.common.time.util.composeTimeDisplay
import de.julianostarek.flow.ui.common.viewholder.base.BindingViewHolder
import de.julianostarek.flow.util.view.setDisplayStop
import de.jlnstrk.transit.common.model.Journey

class MergedJourneyViewHolder(parent: ViewGroup, private val showProduct: Boolean = true) :
    BindingViewHolder<MergedJourney, ItemJourneyMergedBinding>(
        parent,
        ItemJourneyMergedBinding::inflate
    ) {

    interface Observer {

        fun onMergedJourneyClicked(viewHolder: MergedJourneyViewHolder)

        fun onJourneyClicked(viewHolder: MergedJourneyViewHolder, journey: Journey)

    }

    override fun bindTo(data: MergedJourney) {
        invalidateLineDirection(data)
        invalidateSuccessors(data)
        invalidateCountdown(data)
    }

    override fun bindTo(data: MergedJourney, payloads: List<Any>) {
        super.bindTo(data, payloads)
        if (payloads.contains(BaseChangeSignal.TIME_TICK)) {
            invalidateSuccessors(data)
            invalidateCountdown(data)
        }
    }

    private fun invalidateLineDirection(data: MergedJourney) {
        viewBinding.lineDirection.setFromLineDirection(data.line, data.direction, showProduct)
    }

    private fun invalidateSuccessors(data: MergedJourney) {
        val formatted = data.journeys.map { it.stop }
            .composeTimeDisplay(itemView.context, TimeDisplay.Style.RELATIVE)
        viewBinding.status.setText(formatted, TextView.BufferType.SPANNABLE)
    }

    private fun invalidateCountdown(data: MergedJourney) {
        viewBinding.countdown.setDisplayStop(data.journeys.first().stop)
    }

    override fun unbind() {
        super.unbind()
        viewBinding.lineDirection.setFromLineDirection(null, null)
        viewBinding.status.text = null
        viewBinding.countdown.clearDisplay()
    }

}