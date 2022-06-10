package de.julianostarek.flow.ui.main.info.linegroups

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import de.julianostarek.flow.ui.common.adapter.base.BaseListAdapter
import de.jlnstrk.transit.common.model.Line

class LineGroupsAdapter(
    private val listener: Listener
) : BaseListAdapter<LineGroup, LineGroupViewHolder>(ITEM_CALLBACK), LineGroupViewHolder.Observer {

    interface Listener {
        fun onLineClicked(line: Line)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LineGroupViewHolder {
        return LineGroupViewHolder(parent, this)
    }

    override fun onLineClicked(line: Line) = listener.onLineClicked(line)

    companion object {
        private val ITEM_CALLBACK = object : DiffUtil.ItemCallback<LineGroup>() {

            override fun areItemsTheSame(oldItem: LineGroup, newItem: LineGroup): Boolean {
                return oldItem.first == newItem.first
            }

            override fun areContentsTheSame(oldItem: LineGroup, newItem: LineGroup): Boolean {
                return oldItem.second.size == newItem.second.size
            }

        }
    }

}