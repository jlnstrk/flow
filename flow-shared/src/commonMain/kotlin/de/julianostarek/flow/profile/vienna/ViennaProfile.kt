package de.julianostarek.flow.profile.vienna

import de.julianostarek.flow.profile.FlowProduct
import de.julianostarek.flow.profile.StyledProfile
import de.jlnstrk.transit.common.Profile
import de.jlnstrk.transit.common.model.Line
import de.jlnstrk.transit.common.model.TransportMode
import de.jlnstrk.transit.common.model.ProductClass

object ViennaProfile : Profile, StyledProfile {

    enum class Product(
        override val mode: TransportMode,
        override val id: Int,
        override val label: String
    ) : FlowProduct {
        REGIONALZUG(TransportMode.TRAIN, 0, "Regionalzug"),
        S_BAHN(TransportMode.TRAIN, 1, "S-Bahn"),
        U_BAHN(TransportMode.SUBWAY, 2, "U-Bahn"),
        TRAM(TransportMode.LIGHT_RAIL, 3, "Tram"),
        BUS(TransportMode.BUS, 4, "Bus"),
        NIGHT_LINE(TransportMode.BUS, 5, "Nightline")
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
            Product.BUS, Product.NIGHT_LINE,
            isDefault = true,
            styleOf = Product.BUS,
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
            Product.S_BAHN -> OebbProfile.PS_S_BAHN
            Product.U_BAHN -> PRODUCT_STYLE_SUBWAY
            Product.TRAM -> PRODUCT_STYLE_TRAM
            Product.BUS -> PRODUCT_STYLE_BUS
            Product.NIGHT_LINE -> PRODUCT_STYLE_NIGHTLINE
            else -> super.resolveProductStyle(product)
        }
    }

    override fun resolveLineStyle(line: Line): StyledProfile.LineStyle? {
        return when (line.product) {
            Product.S_BAHN -> when (line.label) {
                "S45" -> LINE_STYLE_S45
                else -> null
            }
            Product.U_BAHN -> when (line.label) {
                "U1" -> LINE_STYLE_U1
                "U2" -> LINE_STYLE_U2
                "U3" -> LINE_STYLE_U3
                "U4" -> LINE_STYLE_U4
                "U6" -> LINE_STYLE_U6
                else -> null
            }
            else -> null
        }
    }

    private val PRODUCT_STYLE_SUBWAY = StyledProfile.ProductStyle(
        0xFF0081C9.toInt(),
        iconRes = "product_vienna_ic_subway_24dp",
        iconRawRes = "product_berlin_ic_subway_raw",
    )
    private val PRODUCT_STYLE_TRAM = StyledProfile.ProductStyle(
        0xFFC00D0E.toInt(),
        iconRes = "product_vienna_ic_tram_24dp",
        iconRawRes = "product_vienna_ic_tram_raw",
    )
    private val PRODUCT_STYLE_BUS = StyledProfile.ProductStyle(
        0xFF112A5D.toInt(),
        iconRes = "product_vienna_ic_bus_24dp",
        iconRawRes = "product_vienna_ic_bus_raw",
    )
    private val PRODUCT_STYLE_NIGHTLINE = StyledProfile.ProductStyle(
        0xFF112A5D.toInt(),
        iconRes = "product_vienna_ic_nightline_24dp",
        iconRawRes = "product_vienna_ic_nightline_raw",
    )

    private val LINE_STYLE_S_MAIN = StyledProfile.LineStyle(0xFFE987A0.toInt())
    private val LINE_STYLE_S45 = StyledProfile.LineStyle(0xFFB9D137.toInt())

    private val LINE_STYLE_U1 = StyledProfile.LineStyle(0xFFD8232A.toInt())
    private val LINE_STYLE_U2 = StyledProfile.LineStyle(0xFF945E98.toInt())
    private val LINE_STYLE_U3 = StyledProfile.LineStyle(0xFFE7792B.toInt())
    private val LINE_STYLE_U4 = StyledProfile.LineStyle(0xFF009949.toInt())
    private val LINE_STYLE_U6 = StyledProfile.LineStyle(0xFF865E3B.toInt())

}