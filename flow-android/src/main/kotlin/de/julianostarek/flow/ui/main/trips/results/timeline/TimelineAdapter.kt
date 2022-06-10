package de.julianostarek.flow.ui.main.trips.results.timeline

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import de.julianostarek.flow.ui.common.adapter.base.BaseListAdapter
import de.julianostarek.flow.ui.common.diff.TripDiffItemCallback
import de.julianostarek.flow.util.graphics.dp
import de.jlnstrk.transit.common.model.Trip
import de.jlnstrk.transit.common.util.departureEffective
import de.jlnstrk.transit.common.util.realtimeDuration
import kotlin.math.max
import kotlin.math.roundToInt

class TimelineAdapter(
    private val listener: Listener
) : BaseListAdapter<Trip, TimelineTripViewHolder>(TripDiffItemCallback),
    TimelineTripViewHolder.Observer {
    internal var specs: Specs? = null
    private var recyclerView: RecyclerView? = null

    interface Listener {

        fun onTripClicked(trip: Trip)

    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        val isFirstAttach = this.recyclerView !== recyclerView
        this.recyclerView = recyclerView
        if (isFirstAttach && !currentList.isNullOrEmpty()) {
            recomputeSpecs(currentList)
        }
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        this.recyclerView = null
    }

    override fun submitList(list: List<Trip>?) {
        if (currentList.isEmpty()
            || list.isNullOrEmpty()
            || list.orEmpty().size <= currentList.size
            || !list.contains(currentList.first())
        ) {
            this.specs = null
        }
        super.submitList(list)
        if (list.orEmpty().isNotEmpty()
            && recyclerView != null
        ) {
            recomputeSpecs(list.orEmpty())
        }
    }

    private fun recomputeSpecs(list: List<Trip>) {
        val earliest = list.minOf { it.departure.departureEffective }
        val averageDuration = list
            .map { it.realtimeDuration.inWholeMinutes }
            .average().toFloat()
        val averageOffsetMinutes = list
            .map { it.departure.departureEffective }
            .map { (it - earliest).inWholeMinutes }
            .average().toFloat()
        val averageOffsetStepMinutes = list
            .map { it.departure.departureEffective }
            .zipWithNext()
            .map { (a, b) -> (b - a).inWholeMinutes }
            .average().toFloat()

        val targetTripHeight = recyclerView!!.height * 0.75F
        val offsetRatio = averageOffsetMinutes / averageDuration
        val targetPixelsPerMinute =
            (targetTripHeight / ((1.0F + 0.5F * offsetRatio) * averageDuration)).roundToInt()

        // a 1-minute leg takes 4dp for 2 * 2dp corner radius
        val minimumPixelsPerMinute = 2F.dp(recyclerView as RecyclerView).roundToInt()
        this.specs = Specs(
            minuteScale = max(minimumPixelsPerMinute, targetPixelsPerMinute),
            averageDurationMinutes = averageDuration,
            averageOffsetMinutes = averageOffsetMinutes,
            averageOffsetStepMinutes = averageOffsetStepMinutes
        )
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimelineTripViewHolder {
        return TimelineTripViewHolder(parent)
    }

    override fun onTripClicked(trip: Trip) {
        listener.onTripClicked(trip)
    }

    data class Specs(
        val minuteScale: Int,
        val averageDurationMinutes: Float,
        val averageOffsetMinutes: Float,
        val averageOffsetStepMinutes: Float
    )

}