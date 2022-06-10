package de.julianostarek.flow.ui.common.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import de.jlnstrk.transit.common.model.ProductClass
import de.julianostarek.flow.ui.common.viewholder.LineGroupFlexViewHolder
import de.jlnstrk.transit.common.model.Line
import de.jlnstrk.transit.common.model.LineSet

class ProductGroupedLineListAdapter : RecyclerView.Adapter<LineGroupFlexViewHolder>() {
    private var dataSet: Map<ProductClass, LineSet>? = null

    fun setData(lines: LineSet?) {
        if (lines == null) {
            notifyItemRangeRemoved(0, itemCount)
            dataSet = null
        } else {
            val newDataSet = lines.groupBy(Line::name)
                .map { it.value.first() }
                .sortedBy(Line::name)
                .groupBy { it.product }
                .toMap()
                .mapValues { LineSet().apply {
                    addAll(it.value)
                } }
            dataSet = newDataSet
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LineGroupFlexViewHolder {
        return LineGroupFlexViewHolder(parent)
    }

    override fun onBindViewHolder(holder: LineGroupFlexViewHolder, position: Int) {
        // do nothing
    }

    override fun onBindViewHolder(
        holder: LineGroupFlexViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        val key = dataSet!!.keys.elementAt(position)
        holder.bindTo(dataSet!![key]!!, payloads)
    }

    override fun getItemCount(): Int {
        return dataSet?.size ?: 0
    }

}