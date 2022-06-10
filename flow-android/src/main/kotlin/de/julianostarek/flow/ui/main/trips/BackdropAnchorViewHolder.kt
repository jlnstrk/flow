package de.julianostarek.flow.ui.main.trips

import android.view.ViewGroup
import de.julianostarek.flow.R
import de.julianostarek.flow.databinding.ItemBackdropAnchorBinding
import de.julianostarek.flow.ui.common.viewholder.base.BindingViewHolder
import de.julianostarek.flow.util.transit.applyTo
import de.jlnstrk.transit.common.model.Location

class BackdropAnchorViewHolder(parent: ViewGroup) :
    BindingViewHolder<Location?, ItemBackdropAnchorBinding>(
        parent,
        ItemBackdropAnchorBinding::inflate
    ) {
    private val isOrigin: Boolean get() = adapterPosition == 0
    private val isOnlyDestination: Boolean get() = adapterPosition == 1

    override fun bindTo(data: Location?) {
        invalidateIndex()
        invalidateLocation(data)
        viewBinding.location.setHint(if (isOrigin) R.string.input_hint_choose_origin else R.string.input_hint_choose_destination)
    }

    fun invalidateLocation(data: Location?) {
        if (data != null) {
            data.applyTo(
                viewBinding.location,
                if (isOrigin) R.string.input_prefix_from_location else R.string.input_prefix_to_location
            )
        } else viewBinding.location.text = null
    }

    fun invalidateIndex() {
        viewBinding.location.setCompoundDrawablesRelativeWithIntrinsicBounds(
            when {
                isOrigin -> R.drawable.ic_origin_24dp
                isOnlyDestination -> R.drawable.ic_destination_24dp
                else -> {
                    val typedArray =
                        viewBinding.location.resources.obtainTypedArray(R.array.ic_via_ids)
                    val iconRes = typedArray.getResourceId(adapterPosition - 1, 0)
                    viewBinding.location.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        iconRes,
                        0,
                        0,
                        0
                    )
                    typedArray.recycle()
                    iconRes
                }
            },
            0,
            if (data is Location.Point) R.drawable.ic_my_location_24dp else 0, 0
        )
    }

    override fun unbind() {
        super.unbind()
        viewBinding.location.text = null
        viewBinding.location.hint = null
        viewBinding.location.setCompoundDrawablesRelativeWithIntrinsicBounds(
            null, null, null, null
        )
    }

}