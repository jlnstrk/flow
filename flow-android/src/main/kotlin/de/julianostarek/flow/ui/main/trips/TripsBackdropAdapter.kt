package de.julianostarek.flow.ui.main.trips

import android.os.Handler
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListUpdateCallback
import androidx.recyclerview.widget.RecyclerView
import de.julianostarek.flow.R
import de.julianostarek.flow.ui.common.viewholder.base.BaseViewHolder
import kotlinx.datetime.LocalDateTime
import de.jlnstrk.transit.common.model.Location
import de.jlnstrk.transit.common.model.Via

class TripsBackdropAdapter : RecyclerView.Adapter<BaseViewHolder<*>>(), ListUpdateCallback {
    private val handler: Handler = Handler()
    private var callback: Callback? = null
    private var origin: Location? = null
    private var destination: Location? = null
    private var via: List<Via>? = null
    private var time: LocalDateTime? = null
    private var timeMode: Boolean? = null

    fun withCallback(callback: Callback): TripsBackdropAdapter {
        this.callback = callback
        return this
    }

    interface Callback {

        fun onOriginSelected()

        fun onDestinationSelected()

        fun onWaitTimeSelected(via: Via, index: Int)

        fun onTimeSelected()

        fun onTimeModeSelected()

    }

    override fun onInserted(position: Int, count: Int) {
        notifyItemRangeInserted(position + 1, count)
        handler.post {
            notifyItemRangeChanged(
                position + 1 + count, 2,
                PAYLOAD_TRANSITION_NAME
            )
        }
        notifyItemChanged(itemCount - 2, PAYLOAD_DESTINATION_TYPE)
    }

    override fun onMoved(fromPosition: Int, toPosition: Int) {
        notifyItemMoved(fromPosition + 1, toPosition + 1)
        notifyItemChanged(fromPosition + 1, PAYLOAD_ROUTE_INDEX)
        notifyItemChanged(toPosition + 1, PAYLOAD_ROUTE_INDEX)
        handler.post {
            notifyItemChanged(fromPosition + 1, PAYLOAD_TRANSITION_NAME)
            notifyItemChanged(toPosition + 1, PAYLOAD_TRANSITION_NAME)
        }
    }

    override fun onChanged(position: Int, count: Int, payload: Any?) {
        notifyItemRangeChanged(position + 1, count, payload)
    }

    override fun onRemoved(position: Int, count: Int) {
        notifyItemRangeRemoved(position + 1, count)
        handler.post {
            notifyItemRangeChanged(
                position + 1, (via?.size ?: 0) - (position - 1),
                PAYLOAD_TRANSITION_NAME
            )
            notifyItemRangeChanged(
                1 + (via?.size ?: 0), 2,
                PAYLOAD_TRANSITION_NAME
            )
        }
        notifyItemChanged(itemCount - 2, PAYLOAD_DESTINATION_TYPE)
    }

    fun updateOrigin(origin: Location?) {
        this.origin = origin
        notifyItemChanged(0, PAYLOAD_LOCATION)
    }

    fun updateDestination(destination: Location?) {
        this.destination = destination
        notifyItemChanged(itemCount - 2, PAYLOAD_LOCATION)
    }

    fun updateTime(time: LocalDateTime?) {
        this.time = time
        notifyItemChanged(itemCount - 1, PAYLOAD_TIME)
    }

    fun updateTimeMode(mode: Boolean?) {
        this.timeMode = mode
        notifyItemChanged(itemCount - 1, PAYLOAD_TIME_MODE)
    }

    fun updateVias(via: List<Via>?) {
        val callback = ViaDiffCallback(this.via, via)
        val diff = DiffUtil.calculateDiff(callback)
        this.via = via
        diff.dispatchUpdatesTo(this as ListUpdateCallback)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        return when (viewType) {
            VIEW_TYPE_ANCHOR -> BackdropAnchorViewHolder(parent).also { holder ->
                holder.itemView.setOnClickListener {
                    if (holder.adapterPosition == 0) {
                        callback?.onOriginSelected()
                    } else {
                        callback?.onDestinationSelected()
                    }
                }
            }
            VIEW_TYPE_VIA -> BackdropViaViewHolder(parent).also { holder ->
                holder.viewBinding.waitTime.setOnClickListener {
                    callback?.onWaitTimeSelected(
                        via!![holder.adapterPosition - 1],
                        holder.adapterPosition - 1
                    )
                }
            }
            VIEW_TYPE_TIME -> BackdropTimeViewHolder(parent).also { holder ->
                holder.itemView.setOnClickListener {
                    callback?.onTimeSelected()
                }
                holder.viewBinding.mode.setOnClickListener {
                    callback?.onTimeModeSelected()
                }
            }
            else -> throw IllegalStateException()
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        onBindViewHolder(holder, position, mutableListOf(PAYLOAD_TRANSITION_NAME))
        when (holder) {
            is BackdropAnchorViewHolder -> holder.bindTo(if (position == 0) origin else destination)
            is BackdropViaViewHolder -> holder.bindTo(via!![position - 1])
            is BackdropTimeViewHolder -> holder.bindTo(time)
        }
    }

    override fun onBindViewHolder(
        holder: BaseViewHolder<*>,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isNotEmpty()) {
            if (payloads.contains(PAYLOAD_TRANSITION_NAME)) {
                ViewCompat.setTransitionName(
                    holder.itemView,
                    holder.itemView.context.getString(
                        R.string.tn_backdrop_field_arg,
                        position + 1
                    )
                )
            }
            if (holder is BackdropViaViewHolder) {
                val item = via!![position - 1]
                if (payloads.contains(PAYLOAD_LOCATION)) {
                    holder.invalidateLocation(item.location)
                }
                if (payloads.contains(PAYLOAD_ROUTE_INDEX)) {
                    holder.invalidateIndex()
                }
                if (payloads.contains(PAYLOAD_WAIT_TIME)) {
                    holder.invalidateWaitTime(item.period!!)
                }
            }
            if (holder is BackdropAnchorViewHolder) {
                if (payloads.contains(PAYLOAD_LOCATION)) {
                    holder.invalidateLocation(if (position == 0) origin else destination)
                }
                if (payloads.contains(PAYLOAD_DESTINATION_TYPE)) {
                    holder.invalidateIndex()
                }
            }
            if (holder is BackdropTimeViewHolder) {
                if (payloads.contains(PAYLOAD_TIME)) {
                    holder.invalidateTime(time)
                }
                if (payloads.contains(PAYLOAD_TIME_MODE)) {
                    holder.invalidateMode(timeMode)
                }
            }
        } else onBindViewHolder(holder, position)
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0, 1 + (via?.size ?: 0) -> VIEW_TYPE_ANCHOR
            2 + (via?.size ?: 0) -> VIEW_TYPE_TIME
            else -> VIEW_TYPE_VIA
        }
    }

    override fun getItemCount(): Int {
        return 1 + (via?.size ?: 0) + 2
    }

    class ViaDiffCallback(
        private val oldDataSet: List<Via>?,
        private val newDataSet: List<Via>?
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int {
            return oldDataSet?.size ?: 0
        }

        override fun getNewListSize(): Int {
            return newDataSet?.size ?: 0
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = oldDataSet!![oldItemPosition]
            val newItem = newDataSet!![newItemPosition]
            return oldItem.location == newItem.location
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = oldDataSet!![oldItemPosition]
            val newItem = newDataSet!![newItemPosition]
            return oldItemPosition == newItemPosition
                    && oldItem.period == newItem.period
        }

        override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
            val oldItem = oldDataSet!![oldItemPosition]
            val newItem = newDataSet!![newItemPosition]
            if (oldItem.location != newItem.location) {
                return PAYLOAD_LOCATION
            }
            if (oldItemPosition != newItemPosition) {
                return PAYLOAD_ROUTE_INDEX
            }
            if (oldItem.period == newItem.period) {
                return PAYLOAD_WAIT_TIME
            }
            return null
        }

    }

    companion object {
        private const val VIEW_TYPE_ANCHOR = 0
        private const val VIEW_TYPE_VIA = 1
        private const val VIEW_TYPE_TIME = 2

        const val PAYLOAD_LOCATION = "station"
        const val PAYLOAD_ROUTE_INDEX = "index"
        const val PAYLOAD_DESTINATION_TYPE = "destination_type"
        const val PAYLOAD_WAIT_TIME = "wait_time"
        const val PAYLOAD_TIME = "time"
        const val PAYLOAD_TIME_MODE = "time_mode"
        const val PAYLOAD_TRANSITION_NAME = "transition_name"
    }

}