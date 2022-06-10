package de.julianostarek.flow.profile

import de.jlnstrk.transit.common.Profile
import de.jlnstrk.transit.common.model.ProductClass
import de.jlnstrk.transit.common.model.TransportMode

abstract class DBProfile : Profile, StyledProfile {

    enum class Product(
        override val mode: TransportMode,
        override val id: Int
    ) : FlowProduct {
        ICE(TransportMode.TRAIN, 0),
        IC(TransportMode.TRAIN, 1),
        RE(TransportMode.TRAIN, 2),
        RB(TransportMode.TRAIN, 3),
        S_BAHN(TransportMode.TRAIN, 4)
    }

    override fun resolveProductStyle(product: ProductClass): StyledProfile.ProductStyle {
        return when (product) {
            Product.S_BAHN -> PS_S_BAHN
            else -> super.resolveProductStyle(product)
        }
    }

    companion object {
        val PS_S_BAHN: StyledProfile.ProductStyle =
            StyledProfile.ProductStyle(
                productColor = 0xFF006F35.toInt(),
                iconRes = "product_munich_ic_suburban_train_24dp",
                iconRawRes = "product_generic_ic_train_suburban_raw",
            )
    }

}