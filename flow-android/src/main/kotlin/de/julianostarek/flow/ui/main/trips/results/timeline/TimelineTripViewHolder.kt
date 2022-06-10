package de.julianostarek.flow.ui.main.trips.results.timeline

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import de.julianostarek.flow.databinding.ItemTripTimelineBinding
import de.julianostarek.flow.ui.common.viewholder.base.BindingViewHolder
import de.julianostarek.flow.util.recyclerview.parentAdapterOfType
import de.julianostarek.flow.util.view.setDisplayStop
import de.jlnstrk.transit.common.model.Trip

class TimelineTripViewHolder(
    parent: ViewGroup
) : BindingViewHolder<Trip, ItemTripTimelineBinding>(parent, ItemTripTimelineBinding::inflate),
    View.OnAttachStateChangeListener,
    View.OnClickListener {

    fun interface Observer {
        fun onTripClicked(trip: Trip)
    }

    init {
        itemView.setOnClickListener(this)
    }

    override fun onViewAttachedToWindow(view: View) {
        val pixelsPerMinute = (itemView.parent as? RecyclerView)?.adapter?.parentAdapterOfType<TimelineAdapter>()
                ?.specs?.minuteScale
        if (pixelsPerMinute != null) {
            viewBinding.trip.setTrip(data!!, pixelsPerMinute)
        }
    }

    override fun onViewDetachedFromWindow(v: View?) {
        // ignore
    }

    override fun bindTo(data: Trip) {
        super.bindTo(data)
        itemView.addOnAttachStateChangeListener(this)
        viewBinding.departureTime.setDisplayStop(data.departure)
        viewBinding.arrivalTime.setDisplayStop(data.arrival)
    }

    override fun unbind() {
        super.unbind()
        itemView.removeOnAttachStateChangeListener(this)
        viewBinding.departureTime.clearDisplay()
        viewBinding.arrivalTime.clearDisplay()
    }

    override fun onClick(view: View) {
        adapterAsOptional<Observer>()?.onTripClicked(data!!)
    }

    companion object {
        private const val VIEW_TYPE_INTERMEZZO = 1
        private const val VIEW_TYPE_JOURNEY = 2
    }

}