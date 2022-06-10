package de.julianostarek.flow.ui.main.trips.detail.viewholder

import android.view.ViewGroup
import de.jlnstrk.transit.common.model.stop.Stop
import de.julianostarek.flow.R
import de.julianostarek.flow.databinding.ItemLocationTripBinding
import de.julianostarek.flow.ui.common.diff.StopDiffItemCallback
import de.julianostarek.flow.ui.common.time.util.toSystemLocal
import de.julianostarek.flow.ui.common.viewholder.base.BindingViewHolder
import de.julianostarek.flow.util.datetime.TIME_FORMAT_HH_MM

class TripLocationViewHolder(parent: ViewGroup) :
    BindingViewHolder<Stop, ItemLocationTripBinding>(parent, ItemLocationTripBinding::inflate) {

    override fun bindTo(data: Stop) {
        invalidateText(data)
        invalidateTime(data)
    }

    override fun bindTo(data: Stop, payloads: List<Any>) {
        super.bindTo(data, payloads)
        if (payloads.contains(StopDiffItemCallback.Signal.COUNTDOWN)) {
            invalidateTime(data)
        }
    }

    private fun invalidateText(data: Stop) {
        viewBinding.location.text = data.location.name
        viewBinding.location.setCompoundDrawablesRelativeWithIntrinsicBounds(
            if (adapterPosition == 0) R.drawable.ic_origin_24dp else R.drawable.ic_destination_24dp,
            0, 0, 0
        )
    }

    private fun invalidateTime(data: Stop) {
        viewBinding.time.text = TIME_FORMAT_HH_MM.formatDateTime(
            when (data) {
                is Stop.Arrival -> data.arrivalScheduled
                is Stop.Departure -> data.departureScheduled
                else -> throw IllegalStateException()
            }.toSystemLocal()
        )
    }

    override fun unbind() {
        super.unbind()
        viewBinding.location.text = null
        viewBinding.location.setCompoundDrawablesRelativeWithIntrinsicBounds(
            null, null, null,
            null
        )
        viewBinding.time.text = null
    }

}