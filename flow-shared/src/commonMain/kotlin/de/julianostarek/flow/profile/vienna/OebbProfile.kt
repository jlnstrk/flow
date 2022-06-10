package de.julianostarek.flow.profile.vienna

import de.julianostarek.flow.profile.FlowProduct
import de.julianostarek.flow.profile.StyledProfile
import de.jlnstrk.transit.common.Profile
import de.jlnstrk.transit.common.model.TransportMode
import de.jlnstrk.transit.common.model.ProductClass


abstract class OebbProfile : Profile, StyledProfile {

    enum class Product(
        override val mode: TransportMode,
        override val id: Int
    ) : FlowProduct {
        RJX(TransportMode.TRAIN, 0),
        RJ(TransportMode.TRAIN, 1),
        NJ(TransportMode.TRAIN, 2),
        CITY_JET(TransportMode.TRAIN, 3),
        CITY_SHUTTLE(TransportMode.TRAIN, 4),
        S_BAHN(TransportMode.TRAIN, 5)
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
                productColor = 0xFF0097D9.toInt(),
                iconRes = "product_vienna_ic_suburban_train_24dp",
                iconRawRes = "product_vienna_ic_suburban_train_raw",
            )
    }
}