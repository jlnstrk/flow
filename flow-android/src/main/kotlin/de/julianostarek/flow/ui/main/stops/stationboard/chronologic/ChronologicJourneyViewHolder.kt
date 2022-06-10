package de.julianostarek.flow.ui.main.stops.stationboard.chronologic

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import de.julianostarek.flow.databinding.ItemJourneyBinding
import de.julianostarek.flow.ui.common.adapter.base.BaseChangeSignal
import de.julianostarek.flow.ui.common.diff.JourneyDiffItemCallback
import de.julianostarek.flow.ui.common.viewholder.base.BindingViewHolder
import de.julianostarek.flow.util.view.setDisplayStop
import de.julianostarek.flow.util.text.formatContext
import de.jlnstrk.transit.common.model.Journey

class ChronologicJourneyViewHolder(parent: ViewGroup, private val showProduct: Boolean = true) :
    BindingViewHolder<Journey, ItemJourneyBinding>(
        parent,
        ItemJourneyBinding::inflate
    ), View.OnClickListener {

    interface Observer {
        fun onJourneyClicked(viewHolder: ChronologicJourneyViewHolder)
    }

    init {
        itemView.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        adapterAsOptional<Observer>()?.onJourneyClicked(this)
    }

    override fun bindTo(data: Journey) {
        invalidateLineDirection(data)
        invalidateContext(data)
        invalidateCountdown(data)
    }

    override fun bindTo(data: Journey, payloads: List<Any>) {
        super.bindTo(data, payloads)
        if (payloads.contains(JourneyDiffItemCallback.ChangeSignal.TYPE)) {
            invalidateLineDirection(data)
        }
        if (payloads.contains(JourneyDiffItemCallback.ChangeSignal.STATUS)
            || payloads.contains(JourneyDiffItemCallback.ChangeSignal.REALTIME)
        ) {
            invalidateContext(data)
        }
        if (payloads.contains(JourneyDiffItemCallback.ChangeSignal.TYPE)
            || payloads.contains(JourneyDiffItemCallback.ChangeSignal.STATUS)
            || payloads.contains(JourneyDiffItemCallback.ChangeSignal.REALTIME)
            || payloads.contains(BaseChangeSignal.TIME_TICK)
        ) {
            invalidateCountdown(data)
        }
    }

    private fun invalidateLineDirection(data: Journey) {
        viewBinding.lineDirection.setFromJourney(data, showProduct)
    }

    private fun invalidateContext(data: Journey) {
        val formatted = data.stop.formatContext(itemView.context, data.line.product)
        viewBinding.status.setText(formatted, TextView.BufferType.SPANNABLE)
    }

    private fun invalidateCountdown(data: Journey) {
        viewBinding.countdown.setDisplayStop(data.stop)
    }

    override fun unbind() {
        super.unbind()
        viewBinding.lineDirection.setFromJourney(null)
        viewBinding.status.text = null
        viewBinding.countdown.clearDisplay()
        itemView.alpha = 1.0F
    }

}