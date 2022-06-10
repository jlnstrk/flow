package de.julianostarek.flow.ui.main.trips.detail

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import de.julianostarek.flow.ui.common.diff.PublicLegDiffItemCallback
import de.julianostarek.flow.ui.common.diff.StopDiffItemCallback
import de.julianostarek.flow.ui.main.trips.detail.viewholder.PublicLegViewHolder
import de.julianostarek.flow.ui.main.trips.detail.viewholder.TransferViewHolder
import de.julianostarek.flow.ui.main.trips.detail.viewholder.TripLocationViewHolder
import de.julianostarek.flow.ui.main.trips.detail.viewholder.IndividualLegViewHolder
import de.julianostarek.flow.ui.common.viewholder.base.BaseViewHolder
import de.jlnstrk.transit.common.model.Leg
import de.jlnstrk.transit.common.model.Trip
import java.util.*
import kotlin.collections.ArrayList
import kotlin.time.Duration

class TripDetailAdapter : RecyclerView.Adapter<BaseViewHolder<*>>() {
    private var data: Trip? = null
    private var mapping: List<Pair<Int, Int>>? = null
    private var tripIndex: Int = 0
    private val activePagers: MutableList<PublicLegViewHolder> = LinkedList()

    fun updateDataSet(data: Trip?) {
        val oldSize = this.mapping?.size ?: 0
        this.mapping = data?.positionMapping()
        val newSize = this.mapping?.size ?: 0
        this.data = data
        if (oldSize > 0) {
            notifyItemRangeRemoved(0, oldSize)
        }
        notifyItemRangeInserted(0, newSize)
    }

    fun selectTrip(tripIndex: Int) {
        if (this.tripIndex == tripIndex)
            return
        this.tripIndex = tripIndex
        try {
            this.mapping?.forEachIndexed { index, pair ->
                when (pair.first) {
                    VIEW_TYPE_LOCATION -> notifyItemChanged(
                        index,
                        StopDiffItemCallback.Signal.COUNTDOWN
                    )
                    VIEW_TYPE_PUBLIC_LEG -> {
                        notifyItemChanged(index, PublicLegDiffItemCallback.Signal.ARRIVAL_TIME)
                        notifyItemChanged(index, PublicLegDiffItemCallback.Signal.STOPS)
                    }
                    else -> notifyItemChanged(index, PAYLOAD_TRIP_INDEX)
                }
            }
        } catch (ignored: Exception) {
            // do nothing
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        return when (viewType) {
            VIEW_TYPE_LOCATION -> TripLocationViewHolder(parent)
            VIEW_TYPE_INDIVIDUAL_LEG -> IndividualLegViewHolder(parent)
            VIEW_TYPE_PUBLIC_LEG -> PublicLegViewHolder(parent)
            VIEW_TYPE_TRANSFER -> TransferViewHolder(parent)
            else -> throw IllegalArgumentException()
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        // do nothing
    }

    override fun onBindViewHolder(
        holder: BaseViewHolder<*>,
        position: Int,
        payloads: MutableList<Any>
    ) {
        val entry = mapping!![position]
        when (entry.first) {
            VIEW_TYPE_LOCATION -> {
                val location = data?.legs!![entry.second]
                    .let { if (position == 0) it.departure else it.arrival }
                (holder as TripLocationViewHolder).bindTo(location, payloads)
            }
            VIEW_TYPE_PUBLIC_LEG -> {
                val leg = data?.legs!![entry.second]
                (holder as PublicLegViewHolder).bindTo(leg as Leg.Public, payloads)
                if (payloads.isEmpty()) {
                    holder.invalidateJourney(data?.legs!![entry.second] as Leg.Public)
                }
            }
            VIEW_TYPE_TRANSFER -> {
                val transferDuration = getTransferDuration(entry.second)
                (holder as TransferViewHolder).bindTo(transferDuration, payloads)
            }
            VIEW_TYPE_INDIVIDUAL_LEG -> {
                (holder as IndividualLegViewHolder).bindTo(
                    data?.legs?.get(entry.second) as Leg.Individual,
                    payloads
                )
            }
        }
    }

    override fun onViewAttachedToWindow(holder: BaseViewHolder<*>) {
        super.onViewAttachedToWindow(holder)
        if (holder is PublicLegViewHolder)
            activePagers.add(holder)
    }

    override fun onViewDetachedFromWindow(holder: BaseViewHolder<*>) {
        super.onViewDetachedFromWindow(holder)
        if (holder is PublicLegViewHolder)
            activePagers.remove(holder)
    }

    override fun onViewRecycled(holder: BaseViewHolder<*>) {
        holder.unbind()
    }

    override fun getItemViewType(position: Int): Int {
        return mapping!![position].first
    }

    override fun getItemCount(): Int = mapping?.size ?: 0

    private fun getWalkDataMinutes(legIndex: Int): Pair<Int, Long> {
        val walkStart = data?.legs!![legIndex].departure
        val walkStop = data?.legs!![legIndex].arrival
        val startTime = walkStart.departureScheduled
        val stopTime = walkStop.arrivalScheduled
        val duration = (stopTime - startTime).inWholeMinutes
        val distance = 0 // data!!.relativeTrip()?.legs!![legIndex].distance ?: 0
        return distance to duration
    }

    private fun getTransferDuration(transferToIndex: Int): Duration {
        val alight = data?.legs!![transferToIndex - 1].arrival
        val board = data?.legs!![transferToIndex].departure
        val alightTime = alight.arrivalScheduled
        val boardTime = board.departureScheduled
        return boardTime - alightTime
    }

    private fun Trip.positionMapping(): List<Pair<Int, Int>> {
        val mapping = ArrayList<Pair<Int, Int>>()
        var last: Leg? = null
        for (i in legs.indices) {
            val leg = legs[i]
            if (last is Leg.Public
                && leg is Leg.Public
            ) {
                mapping.add(VIEW_TYPE_TRANSFER to i)
            }
            val viewType = if (leg is Leg.Individual) {
                if (i == 0)
                    mapping.add(VIEW_TYPE_LOCATION to i)
                VIEW_TYPE_INDIVIDUAL_LEG
            } else VIEW_TYPE_PUBLIC_LEG
            mapping.add(viewType to i)
            if (viewType == VIEW_TYPE_INDIVIDUAL_LEG && i == legs.lastIndex)
                mapping.add(VIEW_TYPE_LOCATION to i)
            last = leg
        }
        return mapping
    }

    companion object {
        private const val VIEW_TYPE_LOCATION = 0
        private const val VIEW_TYPE_PUBLIC_LEG = 1
        private const val VIEW_TYPE_INDIVIDUAL_LEG = 2
        private const val VIEW_TYPE_TRANSFER = 3

        private const val PAYLOAD_TRIP_INDEX = "trip_index"
    }

}