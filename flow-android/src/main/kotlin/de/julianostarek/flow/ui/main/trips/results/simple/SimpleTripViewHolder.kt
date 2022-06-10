package de.julianostarek.flow.ui.main.trips.results.simple

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import de.julianostarek.flow.R
import de.julianostarek.flow.databinding.ItemTripBinding
import de.julianostarek.flow.ui.common.adapter.base.BaseChangeSignal
import de.julianostarek.flow.util.text.formatHrsMin
import de.julianostarek.flow.ui.common.viewholder.base.BindingViewHolder
import de.julianostarek.flow.util.text.formatContext
import de.julianostarek.flow.util.transit.formatTimeMultiline
import de.jlnstrk.transit.common.model.Leg
import de.jlnstrk.transit.common.model.Trip

class SimpleTripViewHolder(parent: ViewGroup) :
    BindingViewHolder<Trip, ItemTripBinding>(
        parent, ItemTripBinding::inflate
    ), View.OnClickListener {

    fun interface Observer {
        fun onTripClicked(viewHolder: SimpleTripViewHolder)
    }

    init {
        itemView.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        adapterAsOptional<Observer>()?.onTripClicked(this)
    }

    override fun bindTo(data: Trip) {
        invalidateTimes(data)
        invalidateStatus(data)
        invalidatePreview(data)
        invalidateContext(data)
    }

    override fun bindTo(data: Trip, payloads: List<Any>) {
        super.bindTo(data, payloads)
        if (payloads.contains(BaseChangeSignal.TIME_TICK)) {
            invalidateContext(data)
        }
    }

    private fun invalidateTimes(data: Trip) {
        val departure = data.legs.first().departure
        val arrival = data.legs.last().arrival
        viewBinding.departureTime.setText(
            departure.formatTimeMultiline(viewBinding.departureTime.context),
            TextView.BufferType.SPANNABLE
        )
        viewBinding.arrivalTime.setText(
            arrival.formatTimeMultiline(viewBinding.arrivalTime.context),
            TextView.BufferType.SPANNABLE
        )
        viewBinding.duration.text = data.formatHrsMin(viewBinding.duration.context)
    }

    private fun invalidateStatus(data: Trip) {
        var isCancelled = false
        var isReachable = true
        for (leg in data.legs) {
            if (leg is Leg.Public) {
                if (leg.journey.isCancelled || leg.journey.isPartiallyCancelled
                ) {
                    isCancelled = true
                    break
                }
                if (!leg.journey.isReachable) {
                    isReachable = false
                }
            }
        }
        if (isCancelled || !isReachable) {
            viewBinding.status.setImageResource(
                if (isCancelled) R.drawable.ic_state_cancelled_24dp else
                    R.drawable.ic_state_error_40dp
            )
            viewBinding.status.visibility = View.VISIBLE
        }
    }

    private fun invalidatePreview(data: Trip) {
        viewBinding.previewBar.setTrip(data)
    }

    private fun invalidateContext(data: Trip) {
        val formatted = data.formatContext(viewBinding.info.context)
        viewBinding.info.setText(formatted, TextView.BufferType.SPANNABLE)
    }

    override fun unbind() {
        super.unbind()
        viewBinding.departureTime.text = null
        viewBinding.arrivalTime.text = null
        viewBinding.duration.text = null
        viewBinding.status.visibility = View.GONE
        viewBinding.previewBar.setTrip(null)
    }

}