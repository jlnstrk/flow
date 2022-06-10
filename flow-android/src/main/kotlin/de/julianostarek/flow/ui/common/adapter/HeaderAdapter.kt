package de.julianostarek.flow.ui.common.adapter

import android.view.ViewGroup
import androidx.annotation.StringRes
import de.julianostarek.flow.ui.common.adapter.base.BaseAdapter
import de.julianostarek.flow.ui.common.viewholder.HeaderViewHolder

class HeaderAdapter(
    @StringRes headerRes: Int
) : BaseAdapter<HeaderViewHolder.StringRes>() {
    @StringRes
    var headerRes: Int = headerRes
        set(value) {
            field = value
            notifyItemChanged(0)
        }
    var isVisible: Boolean = true
        set(value) {
            if (value && !field) {
                notifyItemInserted(0)
            } else if (!value && field) {
                notifyItemRemoved(0)
            }
            field = value
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeaderViewHolder.StringRes {
        return HeaderViewHolder.StringRes(parent)
    }

    override fun onBindViewHolder(holder: HeaderViewHolder.StringRes, position: Int) {
        holder.bindTo(headerRes)
    }

    override fun getItemCount(): Int {
        return if (isVisible) 1 else 0
    }

}