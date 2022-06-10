package de.julianostarek.flow.profile.vienna

import de.jlnstrk.transit.common.model.TransportMode
import de.jlnstrk.transit.common.model.ProductClass
import de.jlnstrk.transit.interop.hafas.HafasClassMapping

interface OebbHafas : HafasClassMapping.OneToOne {
    override val mapping: Array<ProductClass>
        get() = arrayOf(
            TransportMode.TRAIN,
            TransportMode.TRAIN,
            TransportMode.TRAIN,
            TransportMode.TRAIN,
            ViennaProfile.Product.REGIONALZUG,
            ViennaProfile.Product.S_BAHN,
            ViennaProfile.Product.BUS,
            TransportMode.WATERCRAFT,
            ViennaProfile.Product.U_BAHN,
            ViennaProfile.Product.TRAM,
            TransportMode.OTHER,
            TransportMode.TRAIN
        )
}