package de.julianostarek.flow.profile

import de.jlnstrk.transit.common.model.ProductClass

interface FlowProduct : ProductClass {
    val id: Int
    val label: String? get() = null
    val showAtStations: Boolean get() = true
}