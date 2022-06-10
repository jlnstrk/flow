package de.julianostarek.flow.profile.hamburg

import de.julianostarek.flow.profile.FlowProduct
import de.julianostarek.flow.profile.StyledProfile
import de.julianostarek.flow.profile.DBProfile
import de.jlnstrk.transit.common.Profile
import de.jlnstrk.transit.common.model.Line
import de.jlnstrk.transit.common.model.TransportMode
import de.jlnstrk.transit.common.model.ProductClass

object HamburgProfile : Profile, StyledProfile {

    enum class Product(
        override val mode: TransportMode,
        override val id: Int,
        override val label: String
    ) : FlowProduct {
        U_BAHN(TransportMode.SUBWAY, 4, "U-Bahn"),
        S_BAHN(TransportMode.TRAIN, 3, "S-Bahn"),
        AKN(TransportMode.TRAIN, 2, "AKN"),

        // Regionalzug
        REGIONAL_EXPRESS(TransportMode.TRAIN, 0, "Regional-Express"),
        REGIONAL_BAHN(TransportMode.TRAIN, 1, "Regionalbahn"),

        METRO_BUS(TransportMode.BUS, 5, "MetroBus"),
        XPRESS_BUS(TransportMode.BUS, 6, "XpressBus"),
        SCHNELL_BUS(TransportMode.BUS, 7, "SchnellBus"),
        STADT_BUS(TransportMode.BUS, 8, "StadtBus"),
        REGIONAL_BUS(TransportMode.BUS, 9, "RegionalBus"),
        NACHT_BUS(TransportMode.BUS, 10, "NachtBus"),

        FAEHRE(TransportMode.WATERCRAFT, 11, "Fähre"),
        AST(TransportMode.CAR, 12, "AnrufSammelTaxi")
    }

    override val filterConfig = arrayOf(
        Profile.FilterEntry(
            Product.REGIONAL_EXPRESS,
            Product.REGIONAL_BAHN,

            isDefault = true,
            styleOf = Product.REGIONAL_BAHN
        ),
        Profile.FilterEntry(
            Product.AKN,
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
            Product.METRO_BUS,
            Product.XPRESS_BUS,
            Product.SCHNELL_BUS,
            Product.STADT_BUS,
            Product.REGIONAL_BUS,
            Product.NACHT_BUS,

            // also includes
            Product.AST,
            isDefault = true,
            styleOf = Product.STADT_BUS
        ),
        Profile.FilterEntry(
            Product.FAEHRE,
            isDefault = true
        )
    )

    override val brandingConfig: Array<ProductClass> = arrayOf(
        Product.U_BAHN,
        Product.S_BAHN,
        Product.AKN,
        Product.REGIONAL_BAHN,
        Product.STADT_BUS,
        Product.FAEHRE
    )

    override fun resolveProductStyle(product: ProductClass): StyledProfile.ProductStyle {
        return when (product) {
            Product.REGIONAL_EXPRESS,
            Product.REGIONAL_BAHN -> PRODUCT_STYLE_REGIONAL
            Product.AKN -> PRODUCT_STYLE_AKN
            Product.S_BAHN -> DBProfile.PS_S_BAHN
            Product.U_BAHN -> PRODUCT_STYLE_SUBWAY
            Product.SCHNELL_BUS -> PS_SCHNELL_BUS
            Product.STADT_BUS,
            Product.METRO_BUS,
            Product.REGIONAL_BUS,
            Product.NACHT_BUS,
            Product.XPRESS_BUS -> PRODUCT_STYLE_BUS
            Product.FAEHRE -> PRODUCT_STYLE_FERRY
            else -> super.resolveProductStyle(product)
        }
    }

    override fun resolveLineStyle(line: Line): StyledProfile.LineStyle? {
        return when (line.product) {
            Product.S_BAHN -> when (line.name) {
                "S1", "S11" -> LINE_STYLE_S1_11
                "S2", "S21" -> LINE_STYLE_S2_21
                "S3", "S31" -> LINE_STYLE_S3_21
                else -> null
            }
            Product.U_BAHN -> when (line.name) {
                "U1" -> LINE_STYLE_U1
                "U2" -> LINE_STYLE_U2
                "U3" -> LINE_STYLE_U3
                "U4" -> LINE_STYLE_U4
                else -> null
            }
            else -> null
        }
    }

    private val PRODUCT_STYLE_AKN = StyledProfile.ProductStyle(
        0xFFF68712.toInt(),
        iconRes = "product_hamburg_ic_akn_24dp",
        iconRawRes = "product_hamburg_ic_akn_raw",
    )
    private val PRODUCT_STYLE_REGIONAL = StyledProfile.ProductStyle(
        0xFF000000.toInt(),
        iconRes = "product_hamburg_ic_regionalbahn_24dp",
        iconRawRes = "product_hamburg_ic_regionalbahn_raw",
    )
    private val PRODUCT_STYLE_SUBWAY = StyledProfile.ProductStyle(
        0xFF0664AB.toInt(),
        iconRes = "product_berlin_ic_subway_24dp",
        iconRawRes = "product_berlin_ic_subway_raw",
    )
    private val PS_SCHNELL_BUS = StyledProfile.ProductStyle(
        0xFFED0020.toInt(),
        iconRes = "product_hamburg_ic_schnellbus_24dp",
    )
    private val PRODUCT_STYLE_BUS = StyledProfile.ProductStyle(
        0xFFED0020.toInt(),
        iconRes = "product_hamburg_ic_bus_alt_24dp",
        iconRawRes = "product_hamburg_ic_bus_raw",
    )
    private val PRODUCT_STYLE_FERRY = StyledProfile.ProductStyle(
        0xFF049ACC.toInt(),
        iconRes = "product_hamburg_ic_faehre_24dp",
        iconRawRes = "product_hamburg_ic_faehre_raw",
    )

    private val LINE_STYLE_S1_11 = StyledProfile.LineStyle(0xFF00962C.toInt())
    private val LINE_STYLE_S2_21 = StyledProfile.LineStyle(0xFFb41439.toInt())
    private val LINE_STYLE_S3_21 = StyledProfile.LineStyle(0xFF54216E.toInt())

    private val LINE_STYLE_U1 = StyledProfile.LineStyle(0xFF005AA4.toInt())
    private val LINE_STYLE_U2 = StyledProfile.LineStyle(0xFFED0020.toInt())
    private val LINE_STYLE_U3 = StyledProfile.LineStyle(0xFFFFD600.toInt(), 0xFF000000.toInt())
    private val LINE_STYLE_U4 = StyledProfile.LineStyle(0xFF008B8F.toInt())

}