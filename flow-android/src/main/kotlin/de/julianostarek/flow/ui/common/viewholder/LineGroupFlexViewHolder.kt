package de.julianostarek.flow.ui.common.viewholder

import android.view.ViewGroup
import de.julianostarek.flow.ui.common.view.ProductGroupedLineListView
import de.julianostarek.flow.ui.common.viewholder.base.BaseViewHolder
import de.jlnstrk.transit.common.model.LineSet

class LineGroupFlexViewHolder(parent: ViewGroup) :
    BaseViewHolder<LineSet>(ProductGroupedLineListView(parent.context)) {

    init {
        itemView.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun bindTo(data: LineSet) {
        super.bindTo(data)
        (itemView as ProductGroupedLineListView).lines = data
    }

    override fun unbind() {
        super.unbind()
        (itemView as ProductGroupedLineListView).lines = null
    }

}