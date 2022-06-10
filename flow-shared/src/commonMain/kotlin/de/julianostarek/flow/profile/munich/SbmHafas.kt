package de.julianostarek.flow.profile.munich

import de.jlnstrk.transit.common.model.TransportMode
import de.jlnstrk.transit.common.model.ProductClass
import de.jlnstrk.transit.interop.hafas.HafasClassMapping

object SbmHafas : HafasClassMapping.OneToOne {
    override val mapping: Array<ProductClass> = arrayOf(
        TransportMode.TRAIN,
        TransportMode.TRAIN,
        TransportMode.TRAIN, // -> NIGHT TRAIN
        MunichProfile.Product.REGIONALZUG,
        MunichProfile.Product.S_BAHN,
        MunichProfile.Product.STADT_BUS,
        TransportMode.OTHER, // -> UNKNOWN
        MunichProfile.Product.U_BAHN,
        MunichProfile.Product.TRAM,
        MunichProfile.Product.RUFTAXI
    )
}