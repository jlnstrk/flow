package de.julianostarek.flow.ui.main.trips.results.simple

import android.view.View
import android.view.ViewGroup
import de.julianostarek.flow.databinding.ItemLoadTriggerHorizontalBinding
import de.julianostarek.flow.ui.common.viewholder.base.BindingViewHolder

class LoadTriggerViewHolder(parent: ViewGroup) :
    BindingViewHolder<Int, ItemLoadTriggerHorizontalBinding>(
        parent,
        ItemLoadTriggerHorizontalBinding::inflate
    ), View.OnClickListener {

    fun interface Observer {
        fun onLoadTriggerClicked(viewHolder: LoadTriggerViewHolder)
    }

    init {
        itemView.setOnClickListener(this)
    }

    private fun selfDisable(isDisabled: Boolean) {
        itemView.isEnabled = !isDisabled
        itemView.alpha = if (isDisabled) 0.5F else 1.0F
        itemView.isClickable = !isDisabled
        itemView.isFocusable = !isDisabled
    }

    override fun bindTo(data: Int) {
        selfDisable(false)
        viewBinding.text.setText(data)
    }

    override fun unbind() {
        super.unbind()
        viewBinding.text.text = null
        selfDisable(false)
    }

    override fun onClick(view: View) {
        selfDisable(true)
        adapterAsOptional<Observer>()?.onLoadTriggerClicked(this)
    }

}