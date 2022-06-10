package de.julianostarek.flow.ui.main.info.linegroups

import android.text.SpannableStringBuilder
import android.text.style.TextAppearanceSpan
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import de.julianostarek.flow.databinding.ItemLineGroupBinding
import de.julianostarek.flow.ui.common.viewholder.base.BindingViewHolder
import de.julianostarek.flow.util.text.appendLineBreak
import de.julianostarek.flow.util.type.captionAppearanceResId
import de.jlnstrk.transit.common.model.Line

class LineGroupViewHolder(parent: ViewGroup, private val observer: Observer) :
    BindingViewHolder<LineGroup, ItemLineGroupBinding>(parent, ItemLineGroupBinding::inflate),
    View.OnClickListener {
    private val captionSpan =
        TextAppearanceSpan(itemView.context, itemView.context.captionAppearanceResId())

    fun interface Observer {
        fun onLineClicked(line: Line)
    }

    init {
        itemView.setOnClickListener(this)
    }

    override fun bindTo(data: LineGroup) {
        super.bindTo(data)
        viewBinding.lineView.setLines(listOf(data.first))

        val textBuilder = SpannableStringBuilder()
        textBuilder.append(data.second.first().head)

        if (data.second.size >= 2) {
            textBuilder.appendLineBreak()
            textBuilder.appendLineBreak(thenInclusive = captionSpan)
            textBuilder.append("+${data.second.size - 1} more message(s)")
        }

        viewBinding.numMessages.setText(textBuilder, TextView.BufferType.SPANNABLE)
    }

    override fun unbind() {
        super.unbind()
        viewBinding.lineView.setLines(null)
        viewBinding.numMessages.text = null
    }

    override fun onClick(view: View?) {
        observer.onLineClicked(data!!.first)
    }

}