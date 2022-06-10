package de.julianostarek.flow.ui.common.viewholder.base

import android.view.View
import androidx.annotation.CallSuper
import androidx.annotation.UiThread
import androidx.recyclerview.widget.RecyclerView
import de.julianostarek.flow.ui.common.adapter.base.BaseAdapterContract
import de.julianostarek.flow.util.AndroidLocation

abstract class BaseViewHolder<T>(view: View) :
    RecyclerView.ViewHolder(view) {
    var data: T? = null
        private set

    /**
     * Bind this ViewHolder from a clean state
     */
    @UiThread
    protected open fun bindTo(data: T) = Unit

    /**
     * Bind this ViewHolder based on a change notification, if [payloads] is empty
     */
    @UiThread
    @CallSuper
    open fun bindTo(data: T, payloads: List<Any> = emptyList()) {
        if (this.data !== data) {
            this.data = data
        }
        if (payloads.isEmpty()) {
            bindTo(data)
        }
    }

    /**
     * Reset this ViewHolder to a clean state
     */
    @UiThread
    @CallSuper
    open fun unbind() {
        this.data = null
    }

    /**
     * If the responsible adapter
     */
    inline fun <reified O> adapterAsOptional(): O? = bindingAdapter as? O

    protected val referenceLocation: AndroidLocation?
        get() = (bindingAdapter as? BaseAdapterContract)?.referenceLocation

}