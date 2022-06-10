package de.julianostarek.flow.ui.main.trips.results.simple

import android.view.ViewGroup
import de.julianostarek.flow.ui.common.adapter.base.BaseAdapter

class LoadTriggerAdapter(
    private val labelRes: Int,
    private val listener: Listener
) : BaseAdapter<LoadTriggerViewHolder>(),
    LoadTriggerViewHolder.Observer {
    var isTriggerVisible: Boolean = false
        set(value) {
            val oldValue = field
            field = value
            if (!oldValue && value) {
                notifyItemInserted(0)
            } else if (oldValue && !value) {
                notifyItemRemoved(0)
            }
        }

    fun interface Listener {
        fun onLoadTriggerClicked()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LoadTriggerViewHolder {
        return LoadTriggerViewHolder(parent)
    }

    override fun onBindViewHolder(holder: LoadTriggerViewHolder, position: Int) {
        holder.bindTo(labelRes)
    }

    override fun getItemCount(): Int {
        return if (isTriggerVisible) 1 else 0
    }

    override fun onLoadTriggerClicked(viewHolder: LoadTriggerViewHolder) {
        listener.onLoadTriggerClicked()
    }

}