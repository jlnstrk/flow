package de.julianostarek.flow.profile.zurich

import de.julianostarek.flow.profile.FlowProduct
import de.jlnstrk.transit.common.Profile
import de.jlnstrk.transit.common.model.Line
import de.jlnstrk.transit.common.model.TransportMode
import de.jlnstrk.transit.common.model.ProductClass
import de.julianostarek.flow.profile.StyledProfile

object ZurichProfile : Profile, StyledProfile {

    enum class Product(
        override val mode: TransportMode,
        override val id: Int
    ) : FlowProduct {
        ZUG(TransportMode.TRAIN, 0),
        S_BAHN(TransportMode.TRAIN, 1),
        TRAM(TransportMode.LIGHT_RAIL, 2),
        ZAHNRAD_BAHN(TransportMode.CABLE, 3),
        BUS(TransportMode.BUS, 4),
        NACHT_BUS(TransportMode.BUS, 5),
        SCHIFF(TransportMode.WATERCRAFT, 6)
    }

    override val filterConfig: Array<Profile.FilterEntry> = arrayOf(
        Profile.FilterEntry(
            Product.ZUG,
            isDefault = true
        ),
        Profile.FilterEntry(
            Product.S_BAHN,
            isDefault = true
        ),
        Profile.FilterEntry(
            Product.TRAM,
            isDefault = true
        ),
        Profile.FilterEntry(
            Product.ZAHNRAD_BAHN,
            isDefault = true
        ),
        Profile.FilterEntry(
            Product.BUS, Product.NACHT_BUS,
            isDefault = true,
            styleOf = Product.BUS
        ),
        Profile.FilterEntry(
            Product.SCHIFF,
            isDefault = true
        ),
    )

    override val brandingConfig: Array<ProductClass> = arrayOf(
        Product.S_BAHN,
        Product.TRAM,
        Product.ZAHNRAD_BAHN,
        Product.BUS,
        Product.SCHIFF
    )

    override fun resolveProductStyle(product: ProductClass): StyledProfile.ProductStyle {
        return when (product) {
            Product.ZUG,
            Product.S_BAHN -> PS_ZUG
            Product.TRAM -> PS_TRAM
            Product.ZAHNRAD_BAHN -> PS_ZAHNRADBAHN
            Product.BUS,
            Product.NACHT_BUS -> PS_BUS
            Product.SCHIFF -> PS_SCHIFF
            else -> super.resolveProductStyle(product)
        }
    }

    override fun resolveLineStyle(line: Line): StyledProfile.LineStyle? {
        return null
    }

    private val PS_ZUG = StyledProfile.ProductStyle(
        0xFF2D327D.toInt(),
        iconRes = "product_zurich_ic_train_24dp",
        iconRawRes = "product_zurich_ic_train_raw",
    )
    private val PS_TRAM = StyledProfile.ProductStyle(
        0xFF2D327D.toInt(),
        iconRes = "product_zurich_ic_tram_24dp",
        iconRawRes = "product_zurich_ic_tram_raw",
    )
    private val PS_ZAHNRADBAHN = StyledProfile.ProductStyle(
        0xFF2D327D.toInt(),
        iconRes = "product_rheinruhr_ic_suspension_rail_24dp",
        iconRawRes = "product_rheinruhr_ic_suspension_rail_raw",
    )
    private val PS_BUS = StyledProfile.ProductStyle(
        0xFF2D327D.toInt(),
        iconRes = "product_zurich_ic_bus_24dp",
        iconRawRes = "product_zurich_ic_bus_raw",
    )
    private val PS_SCHIFF = StyledProfile.ProductStyle(
        0xFF2D327D.toInt(),
        iconRes = "product_zurich_ic_waterway_24dp",
        iconRawRes = "product_zurich_ic_waterway_raw",
    )

}