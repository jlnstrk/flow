package de.julianostarek.flow.ui.main.stops.stationboard.mergedgrouped

import android.view.ViewGroup
import de.julianostarek.flow.ui.common.adapter.base.BaseListAdapter
import de.julianostarek.flow.ui.common.diff.JourneyDiffItemCallback
import de.julianostarek.flow.ui.main.stops.stationboard.chronologic.ChronologicJourneyViewHolder
import de.julianostarek.flow.ui.main.stops.stationboard.merged.groupByProduct
import de.jlnstrk.transit.common.model.Journey
import de.jlnstrk.transit.common.model.ProductClass

class GroupedJourneysAdapter(
    private val listener: Listener
) : BaseListAdapter<Journey, ChronologicJourneyViewHolder>(JourneyDiffItemCallback) {
    private var grouped: Map<ProductClass, List<Journey>>? = null
    private var visibleProduct: ProductClass? = null

    interface Listener

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ChronologicJourneyViewHolder {
        return ChronologicJourneyViewHolder(parent, showProduct = false)
    }

    override fun submitList(list: List<Journey>?) {
        submitList(list, null)
    }

    @Suppress("UNCHECKED_CAST")
    override fun submitList(list: List<Journey>?, commitCallback: Runnable?) {
        if (list != null) {
            return submitJourneys(list, commitCallback)
        }
        super.submitList(list, commitCallback)
    }

    private fun submitJourneys(list: List<Journey>, commitCallback: Runnable?) {
        grouped = list.groupByProduct()
        if (visibleProduct == null || !grouped!!.containsKey(visibleProduct)) {
            visibleProduct = grouped?.keys?.first()
        }
        showProduct(visibleProduct!!)
    }

    fun showProduct(product: ProductClass) {
        if (grouped?.containsKey(product) == true) {
            this.visibleProduct = product
            super.submitList(grouped?.get(visibleProduct))
        }
    }

}