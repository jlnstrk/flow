package de.julianostarek.flow.profile.rheinruhr

import de.julianostarek.flow.profile.FlowProduct
import de.julianostarek.flow.profile.StyledProfile
import de.julianostarek.flow.profile.DBProfile
import de.jlnstrk.transit.common.Profile
import de.jlnstrk.transit.common.model.Line
import de.jlnstrk.transit.common.model.TransportMode
import de.jlnstrk.transit.common.model.ProductClass

object RheinRuhrProfile : Profile, StyledProfile {

    enum class Product(
        override val mode: TransportMode,
        override val id: Int
    ) : FlowProduct {
        REGIONALZUG(TransportMode.TRAIN, 0),
        S_BAHN(TransportMode.TRAIN, 1),
        U_BAHN(TransportMode.SUBWAY, 2),
        TRAM(TransportMode.LIGHT_RAIL, 3),
        BUS(TransportMode.BUS, 4),
        SCHWEBEBAHN(TransportMode.CABLE, 5)
    }

    override val filterConfig: Array<Profile.FilterEntry> = arrayOf(
        Profile.FilterEntry(
            Product.REGIONALZUG,
            isDefault = true
        ),
        Profile.FilterEntry(
            Product.S_BAHN,
            isDefault = true
        ),
        Profile.FilterEntry(
            Product.U_BAHN,
            isDefault = true
        ),
        Profile.FilterEntry(
            Product.TRAM,
            isDefault = true
        ),
        Profile.FilterEntry(
            Product.BUS,
            isDefault = true
        ),
    )

    override val brandingConfig: Array<ProductClass> = arrayOf(
        Product.REGIONALZUG,
        Product.S_BAHN,
        Product.U_BAHN,
        Product.TRAM,
        Product.BUS
    )

    override fun resolveProductStyle(product: ProductClass): StyledProfile.ProductStyle {
        return when (product) {
            Product.REGIONALZUG -> PS_REGIONALZUG
            Product.S_BAHN -> DBProfile.PS_S_BAHN
            Product.U_BAHN -> PS_U_BAHN
            Product.TRAM -> PS_TRAM
            Product.SCHWEBEBAHN -> PS_WUPPERTAL_SCHWEBEBAHN
            Product.BUS -> PS_BUS
            else -> super.resolveProductStyle(product)
        }
    }

    override fun resolveLineStyle(line: Line): StyledProfile.LineStyle? {
        return null
    }

    private val PS_REGIONALZUG = StyledProfile.ProductStyle(
        0xFF393536.toInt(),
        iconRes = "product_rheinruhr_ic_regional_train_24dp",
        iconRawRes = "product_rheinruhr_ic_regional_train_raw",
    )
    private val PS_U_BAHN = StyledProfile.ProductStyle(
        0xFF4896d2.toInt(),
        iconRes = "product_berlin_ic_subway_24dp",
        iconRawRes = "product_berlin_ic_subway_raw",
    )
    private val PS_TRAM = StyledProfile.ProductStyle(
        0xFF008FCF.toInt(),
        iconRes = "product_rheinruhr_ic_tram_24dp",
        iconRawRes = "product_rheinruhr_ic_tram_raw",
    )
    private val PS_WUPPERTAL_SCHWEBEBAHN = StyledProfile.ProductStyle(
        0xFF4896d2.toInt(),
        iconRes = "product_rheinruhr_ic_suspension_rail_24dp",
        iconRawRes = "product_rheinruhr_ic_suspension_rail_raw",
    )
    private val PS_BUS = StyledProfile.ProductStyle(
        0xFF4896d2.toInt(),
        iconRes = "product_rheinruhr_ic_bus_24dp",
        iconRawRes = "product_rheinruhr_ic_bus_raw",
    )

}