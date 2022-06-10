package de.julianostarek.flow.ui.common.viewholder

import android.view.ViewGroup
import de.julianostarek.flow.util.graphics.dp
import kotlin.math.roundToInt
import de.julianostarek.flow.ui.common.view.LineGroupView
import de.julianostarek.flow.ui.common.viewholder.base.BaseViewHolder
import de.jlnstrk.transit.common.model.Line

class LineGroupViewHolder(parent: ViewGroup) : BaseViewHolder<List<Line>>(
    LineGroupView(parent.context)
) {

    init {
        itemView.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            24F.dp(this).roundToInt()
        )
    }

    override fun bindTo(data: List<Line>) {
        (itemView as LineGroupView).setLines(data)
    }

    override fun unbind() {
        super.unbind()
        (itemView as LineGroupView).setLines(null)
    }

}