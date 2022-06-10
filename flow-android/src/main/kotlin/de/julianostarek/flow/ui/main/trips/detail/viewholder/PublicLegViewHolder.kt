package de.julianostarek.flow.ui.main.trips.detail.viewholder

import android.content.res.ColorStateList
import android.text.SpannableStringBuilder
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.ChangeBounds
import androidx.transition.Fade
import androidx.transition.TransitionManager
import androidx.transition.TransitionSet
import de.julianostarek.flow.R
import de.julianostarek.flow.databinding.ItemLegPublicBinding
import de.julianostarek.flow.ui.common.adapter.base.BaseChangeSignal
import de.julianostarek.flow.ui.common.decor.VerticalGridSpacingItemDecoration
import de.julianostarek.flow.ui.common.diff.PublicLegDiffItemCallback
import de.julianostarek.flow.ui.common.view.base.ExpandMoreButton
import de.julianostarek.flow.ui.common.viewholder.base.BindingViewHolder
import de.julianostarek.flow.ui.main.trips.detail.LegIntermediateStopsAdapter
import de.julianostarek.flow.util.graphics.dp
import de.julianostarek.flow.util.text.*
import de.julianostarek.flow.util.transit.defaultColor
import de.julianostarek.flow.util.view.setDisplayStop
import kotlin.math.roundToInt
import de.jlnstrk.transit.common.model.Leg

class PublicLegViewHolder(parent: ViewGroup) :
    BindingViewHolder<Leg.Public, ItemLegPublicBinding>(parent, ItemLegPublicBinding::inflate),
    ExpandMoreButton.OnExpandChangeListener {
    private val intermediateStopsAdapter = LegIntermediateStopsAdapter()
    private var intermediateStopsVisible: Boolean = false

    init {
        viewBinding.stops.layoutManager = LinearLayoutManager(itemView.context)
        viewBinding.stops.adapter = intermediateStopsAdapter
        viewBinding.messages.layoutManager = LinearLayoutManager(itemView.context)
        viewBinding.messages.addItemDecoration(
            VerticalGridSpacingItemDecoration(
                viewBinding.messages.context, 1, spacing = 12F.dp(this).roundToInt(),
                horizontalEdge = false
            )
        )
        viewBinding.summary.onExpandChangeListener = this
    }

    override fun bindTo(data: Leg.Public) {
        invalidateArrivalTime(data)
        invalidateStops(data)
        viewBinding.origin.text = data.departure.location.name
        viewBinding.destination.text = data.arrival.location.name
        val productColor = data.journey.line.defaultColor(itemView.context)
        viewBinding.sideline.backgroundTintList = ColorStateList.valueOf(productColor)
    }

    override fun bindTo(data: Leg.Public, payloads: List<Any>) {
        super.bindTo(data, payloads)
        if (payloads.contains(PublicLegDiffItemCallback.Signal.ARRIVAL_TIME))
            invalidateArrivalTime(data)
        if (payloads.contains(PublicLegDiffItemCallback.Signal.STOPS))
            invalidateStops(data)
        if (payloads.contains(BaseChangeSignal.TIME_TICK)) {
            invalidateJourney(data)
        }
    }

    private fun invalidateArrivalTime(data: Leg) {
        viewBinding.arrivalTime.setDisplayStop(data.arrival)
    }

    fun invalidateJourney(data: Leg.Public) {
        viewBinding.lineDirection.setFromJourney(data.journey)

        val stateFormatted =
            data.departure.formatContext(itemView.context, data.journey.line.product)
        viewBinding.status.setText(stateFormatted, TextView.BufferType.SPANNABLE)

        viewBinding.departureTime.setDisplayStop(data.departure)
    }

    private fun invalidateStops(data: Leg.Public) {
        val stopsCount = data.journey.stops?.let { it.size - 1 } ?: 0
        if (stopsCount == 1) {
            viewBinding.summary.isEnabled = false
        }
        val durationFormatted = data.formatHrsMin(itemView.context)
        val summary = SpannableStringBuilder()
            .appendPluralRes(
                itemView.context,
                R.plurals.stops,
                quantity = stopsCount,
                args = arrayOf(stopsCount)
            )
            .appendSeparator()
            .append(durationFormatted)
        viewBinding.summary.setText(summary, TextView.BufferType.SPANNABLE)
    }

    override fun unbind() {
        super.unbind()
        viewBinding.origin.text = null
        viewBinding.destination.text = null
        viewBinding.sideline.backgroundTintList = null
        intermediateStopsAdapter.submitList(null)
        viewBinding.messages.adapter = null
        viewBinding.summary.reset()
        // interval.visibility = View.VISIBLE
    }

    override fun onExpandChanged(isExpanded: Boolean) {
        val transition = TransitionSet()
        transition.ordering = TransitionSet.ORDERING_SEQUENTIAL
        transition.addTransition(ChangeBounds())
        transition.addTransition(Fade(Fade.MODE_IN))
        transition.excludeChildren(viewBinding.stops, true)
        val target = itemView.parent.parent.parent as ViewGroup
        TransitionManager.beginDelayedTransition(
            target,
            transition
        )
        intermediateStopsVisible = isExpanded
        intermediateStopsAdapter.submitList(
            if (intermediateStopsVisible) {
                data?.journey?.stops?.let { it.subList(1, it.lastIndex) }
            } else null
        )
    }

}