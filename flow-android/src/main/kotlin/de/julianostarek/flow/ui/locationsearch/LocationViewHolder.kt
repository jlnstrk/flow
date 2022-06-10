package de.julianostarek.flow.ui.locationsearch

import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.TextAppearanceSpan
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import de.julianostarek.flow.R
import de.julianostarek.flow.databinding.ItemLocationBinding
import de.julianostarek.flow.ui.common.adapter.base.BaseChangeSignal
import de.julianostarek.flow.ui.common.viewholder.base.BindingViewHolder
import de.julianostarek.flow.util.text.appendProducts
import de.julianostarek.flow.util.text.appendLineBreak
import de.julianostarek.flow.util.text.distanceFormatted
import de.julianostarek.flow.util.transit.asAndroid
import de.julianostarek.flow.util.type.captionAppearanceResId
import de.jlnstrk.transit.common.model.Location

class LocationViewHolder(parent: ViewGroup) :
    BindingViewHolder<Location, ItemLocationBinding>(parent, ItemLocationBinding::inflate),
    View.OnClickListener {
    private val captionSpan =
        TextAppearanceSpan(itemView.context, itemView.context.captionAppearanceResId())

    interface Observer {
        fun onLocationClicked(location: Location)
    }

    init {
        itemView.setOnClickListener(this)
    }

    override fun bindTo(data: Location) {
        invalidateIcon(data)
        invalidateName(data)
        referenceLocation?.let {
            invalidateDistance(data)
        }
    }

    override fun bindTo(data: Location, payloads: List<Any>) {
        super.bindTo(data, payloads)
        if (payloads.contains(BaseChangeSignal.REF_LOCATION)) {
            invalidateDistance(data)
        }
    }

    fun invalidateIcon(data: Location) {
        val iconRes = when (data) {
            is Location.Station -> R.drawable.ic_station_32dp
            is Location.Address -> R.drawable.ic_address_32dp
            is Location.Poi -> R.drawable.ic_poi_32dp
            is Location.Place -> R.drawable.ic_place_32dp
            else -> throw IllegalStateException()
        }
        viewBinding.icon.setImageResource(iconRes)
    }

    fun invalidateName(data: Location) {
        val products = (data as? Location.Station)?.products
        val nameBuilder = SpannableStringBuilder()
        if (data.place != null) {
            nameBuilder.append(data.place, captionSpan, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            nameBuilder.appendLineBreak()
        }
        nameBuilder.append(data.name)
        nameBuilder.appendProducts(
            viewBinding.name.context,
            products,
            viewBinding.name,
            newline = true
        )
        viewBinding.name.setText(nameBuilder, TextView.BufferType.SPANNABLE)
    }

    fun invalidateDistance(data: Location) {
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
        viewBinding.icon.setImageDrawable(null)
    }

    override fun onClick(view: View) {
        adapterAsOptional<Observer>()?.onLocationClicked(data!!)
    }

}