package de.julianostarek.flow.ui.common.diff

import androidx.recyclerview.widget.DiffUtil
import de.jlnstrk.transit.common.model.Line

object LineDiffItemCallback : DiffUtil.ItemCallback<Line>() {

    override fun areItemsTheSame(oldItem: Line, newItem: Line): Boolean {
        return oldItem.product == newItem.product
                && oldItem.label == newItem.label
    }

    override fun areContentsTheSame(oldItem: Line, newItem: Line): Boolean {
        return oldItem == newItem
    }

}