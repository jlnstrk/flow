package de.julianostarek.flow.ui.main.stops.nearby

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import de.julianostarek.flow.databinding.ItemLocationNearbyDeparturesBinding
import de.julianostarek.flow.ui.common.adapter.base.BaseChangeSignal
import de.julianostarek.flow.ui.common.span.base.TextAppearanceSpanCompat
import de.julianostarek.flow.ui.common.viewholder.base.BindingViewHolder
import de.julianostarek.flow.util.text.distanceFormatted

import de.julianostarek.flow.util.text.formatName
import de.julianostarek.flow.util.transit.asAndroid
import de.julianostarek.flow.util.type.captionAppearanceResId
import de.jlnstrk.transit.common.model.Journey
import de.jlnstrk.transit.common.model.Location

class NearbyDeparturesViewHolder(parent: ViewGroup) :
    BindingViewHolder<Location, ItemLocationNearbyDeparturesBinding>(
        parent,
        ItemLocationNearbyDeparturesBinding::inflate
    ),
    View.OnClickListener,
    NearbyJourneysAdapter.Listener {
    private val journeysAdapter = NearbyJourneysAdapter(this)
    private val captionSpan = TextAppearanceSpanCompat(
        itemView.context,
        itemView.context.captionAppearanceResId()
    )

    interface Observer {

        fun onLocationClicked(viewHolder: NearbyDeparturesViewHolder)

        fun onJourneyClicked(viewHolder: NearbyDeparturesViewHolder, journey: Journey)

    }

    init {
        itemView.setOnClickListener(this)
        viewBinding.recyclerView.layoutManager = LinearLayoutManager(itemView.context)
        viewBinding.recyclerView.adapter = journeysAdapter
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
        if (payloads.contains(BaseChangeSignal.TIME_TICK)) {
            journeysAdapter.onTimeTick()
        }
    }

    fun bindToJourneys(journeys: List<Journey>) {
        journeysAdapter.submitList(journeys)
    }

    private fun invalidateName(data: Location) {
        val formatted = data.formatName(itemView.context, captionSpan, viewBinding.name, true)
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
        journeysAdapter.submitList(null)
    }

    override fun onJourneyClicked(journey: Journey) {
        adapterAsOptional<Observer>()?.onJourneyClicked(this, journey)
    }

}