package de.julianostarek.flow.ui.common.diff

import androidx.recyclerview.widget.DiffUtil
import de.jlnstrk.transit.common.model.NetworkMap

object NetworkMapDiffItemCallback : DiffUtil.ItemCallback<NetworkMap>() {

    override fun areItemsTheSame(oldItem: NetworkMap, newItem: NetworkMap): Boolean {
        return oldItem.fileUrl == newItem.fileUrl
    }

    override fun areContentsTheSame(oldItem: NetworkMap, newItem: NetworkMap): Boolean {
        return oldItem.title == newItem.title
                && oldItem.author == newItem.author
                && oldItem.products == newItem.products
                && oldItem.thumbnailUrl == newItem.thumbnailUrl
    }

}