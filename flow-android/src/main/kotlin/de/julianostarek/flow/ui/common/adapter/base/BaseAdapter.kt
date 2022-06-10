package de.julianostarek.flow.ui.common.adapter.base

import androidx.annotation.CallSuper
import androidx.recyclerview.widget.RecyclerView
import de.julianostarek.flow.ui.common.viewholder.base.BaseViewHolder
import de.julianostarek.flow.util.AndroidLocation

abstract class BaseAdapter<VH : BaseViewHolder<*>> : RecyclerView.Adapter<VH>(),
    BaseAdapterContract {
    override var referenceLocation: AndroidLocation? = null

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