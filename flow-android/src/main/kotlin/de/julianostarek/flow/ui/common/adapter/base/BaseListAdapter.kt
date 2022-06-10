package de.julianostarek.flow.ui.common.adapter.base

import androidx.annotation.CallSuper
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import de.julianostarek.flow.ui.common.viewholder.base.BaseViewHolder
import de.julianostarek.flow.util.AndroidLocation

abstract class BaseListAdapter<T : Any, VH : BaseViewHolder<T>> : ListAdapter<T, VH>,
    BaseAdapterContract {
    override var referenceLocation: AndroidLocation? = null

    constructor(itemCallback: DiffUtil.ItemCallback<T>) : super(itemCallback)

    constructor(config: AsyncDifferConfig<T>) : super(config)

    @CallSuper
    override fun onBindViewHolder(holder: VH, position: Int) {
        // do nothing
    }

    @CallSuper
    override fun onBindViewHolder(holder: VH, position: Int, payloads: MutableList<Any>) {
        val item = getItem(position)
        holder.bindTo(item, payloads)
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
