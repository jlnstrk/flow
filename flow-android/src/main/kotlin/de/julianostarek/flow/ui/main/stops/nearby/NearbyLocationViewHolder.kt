package de.julianostarek.flow.ui.main.stops.nearby

import android.text.style.TextAppearanceSpan
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import de.julianostarek.flow.databinding.ItemLocationNearbyBinding
import de.julianostarek.flow.ui.common.adapter.base.BaseChangeSignal
import de.julianostarek.flow.ui.common.viewholder.base.BindingViewHolder
import de.julianostarek.flow.util.text.distanceFormatted
import de.julianostarek.flow.util.text.formatName
import de.julianostarek.flow.util.transit.asAndroid
import de.julianostarek.flow.util.type.captionAppearanceResId
import de.jlnstrk.transit.common.model.Location

class NearbyLocationViewHolder(parent: ViewGroup) :
    BindingViewHolder<Location, ItemLocationNearbyBinding>(
        parent,
        ItemLocationNearbyBinding::inflate
    ),
    View.OnClickListener {
    private val captionSpan = TextAppearanceSpan(
        itemView.context,
        itemView.context.captionAppearanceResId()
    )

    fun interface Observer {
        fun onLocationClicked(viewHolder: NearbyLocationViewHolder)
    }

    init {
        itemView.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        adapterAsOptional<Observer>()?.onLocationClicked(this)
    }

    override fun bindTo(data: Location) {
        invalidateName(data)
        invalidateDistance(data)
    }

    override fun bindTo(data: Location, payloads: List<Any>) {
        super.bindTo(data, payloads)
        if (payloads.contains(BaseChangeSignal.REF_LOCATION)) {
            invalidateDistance(data)
        }
    }

    private fun invalidateName(data: Location) {
        val formatted = data.formatName(
            itemView.context,
            captionSpan,
            viewBinding.name,
            nameNewline = true,
            productsNewline = true
        )
        viewBinding.name.setText(formatted, TextView.BufferType.SPANNABLE)
    }

    private fun invalidateDistance(data: Location) {
        referenceLocation?.let {
            val distanceMeters = data.coordinates?.asAndroid()?.distanceTo(it)
            viewBinding.distance.text =
                distanceMeters?.distanceFormatted(viewBinding.distance.context)
        }
    }

    override fun unbind() {
        super.unbind()
        viewBinding.name.text = null
        viewBinding.distance.text = null
    }

}