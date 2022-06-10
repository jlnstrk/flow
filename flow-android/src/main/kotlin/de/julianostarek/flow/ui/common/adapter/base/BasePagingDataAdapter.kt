package de.julianostarek.flow.ui.common.adapter.base

import androidx.annotation.CallSuper
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import de.julianostarek.flow.ui.common.viewholder.base.BaseViewHolder
import de.julianostarek.flow.util.AndroidLocation

abstract class BasePagingDataAdapter<T : Any, VH : BaseViewHolder<T>>(
    itemCallback: DiffUtil.ItemCallback<T>
) : PagingDataAdapter<T, VH>(itemCallback),
    BaseAdapterContract {
    override var referenceLocation: AndroidLocation? = null

    @CallSuper
    override fun onBindViewHolder(holder: VH, position: Int) {
        // do nothing
    }

    @CallSuper
    override fun onBindViewHolder(holder: VH, position: Int, payloads: MutableList<Any>) {
        val item = getItem(position)
        if (item != null) {
            holder.bindTo(item, payloads)
        }
    }

    @CallSuper
    override fun onViewRecycled(holder: VH) {
        holder.unbind()
    }

    override fun onReferenceLocationChanged(location: AndroidLocation) {
        super.onReferenceLocationChanged(location)
        notifyItemRangeChanged(0, itemCount, BaseChangeSignal.REF_LOCATION)
    }

    override fun onTimeTick() {
        notifyItemRangeChanged(0, itemCount, BaseChangeSignal.TIME_TICK)
    }

}
