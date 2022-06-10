package de.julianostarek.flow.ui.common.viewholder.base

import android.view.ViewGroup
import androidx.viewbinding.ViewBinding

abstract class ObservableViewHolder<T, VB : ViewBinding, O>(
    parent: ViewGroup,
    inflate: BindingInflate<VB>
) : BindingViewHolder<T, VB>(parent, inflate) {
    protected var observer: O? = null
        private set

    fun attachObserver(observer: O) {
        this.observer = observer
    }

    fun detachObserver() {
        this.observer = null
    }

}