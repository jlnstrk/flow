package de.julianostarek.flow.profile.zurich

import de.jlnstrk.transit.common.model.TransportMode
import de.jlnstrk.transit.common.model.ProductClass
import de.jlnstrk.transit.interop.hafas.HafasClassMapping

interface ZvvHafas : HafasClassMapping.OneToOne {
    override val mapping: Array<ProductClass> get() = MAPPING

    companion object {
        private val MAPPING: Array<ProductClass> = arrayOf(
            TransportMode.OTHER, // -> ?
            ZurichProfile.Product.ZUG,
            TransportMode.OTHER, // -> ?
            TransportMode.OTHER, // -> ?
            ZurichProfile.Product.SCHIFF,
            ZurichProfile.Product.S_BAHN,
            ZurichProfile.Product.BUS,
            ZurichProfile.Product.ZAHNRAD_BAHN,
            ZurichProfile.Product.NACHT_BUS,
            ZurichProfile.Product.TRAM,
        )
    }
}