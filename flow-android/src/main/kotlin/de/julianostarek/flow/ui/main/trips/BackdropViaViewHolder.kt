package de.julianostarek.flow.ui.main.trips

import android.view.ViewGroup
import de.julianostarek.flow.R
import de.julianostarek.flow.databinding.ItemBackdropViaBinding
import de.julianostarek.flow.ui.common.viewholder.base.BindingViewHolder
import de.julianostarek.flow.util.transit.applyTo
import de.jlnstrk.transit.common.model.Location
import de.jlnstrk.transit.common.model.Via
import kotlin.time.Duration

class BackdropViaViewHolder(parent: ViewGroup) :
    BindingViewHolder<Via, ItemBackdropViaBinding>(
        parent,
        ItemBackdropViaBinding::inflate
    ) {

    override fun bindTo(data: Via) {
        invalidateLocation(data.location)
        invalidateIndex()
        invalidateWaitTime(data.period!!)
    }

    fun invalidateLocation(station: Location) {
        station.applyTo(viewBinding.location, when (adapterPosition) {
            1 -> R.string.input_prefix_via
            else -> R.string.input_prefix_and
        })
    }

    fun invalidateIndex() {
        invalidateLocation(data!!.location)
        val typedArray =
            viewBinding.location.resources.obtainTypedArray(R.array.ic_via_ids)
        val iconRes = typedArray.getResourceId(adapterPosition - 1, 0)
        viewBinding.location.setCompoundDrawablesRelativeWithIntrinsicBounds(iconRes, 0, 0, 0)
        typedArray.recycle()
    }

    fun invalidateWaitTime(waitTime: Duration) {
        viewBinding.waitTime.text =
            viewBinding.waitTime.context.getString(R.string.via_wait_time, waitTime.inWholeMinutes)
    }

    override fun unbind() {
        super.unbind()
        viewBinding.location.text = null
        viewBinding.location.hint = null
    }

}