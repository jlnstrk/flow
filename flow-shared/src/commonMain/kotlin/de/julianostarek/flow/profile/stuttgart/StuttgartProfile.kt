package de.julianostarek.flow.profile.stuttgart

import de.julianostarek.flow.profile.FlowProduct
import de.julianostarek.flow.profile.StyledProfile
import de.julianostarek.flow.profile.DBProfile
import de.jlnstrk.transit.common.Profile
import de.jlnstrk.transit.common.model.Line
import de.jlnstrk.transit.common.model.TransportMode
import de.jlnstrk.transit.common.model.ProductClass

object StuttgartProfile : Profile, StyledProfile {

    enum class Product(
        override val mode: TransportMode,
        override val id: Int
    ) : FlowProduct {
        REGIONALZUG(TransportMode.TRAIN, 0),
        S_BAHN(TransportMode.TRAIN, 1),
        STADTBAHN(TransportMode.LIGHT_RAIL, 2),
        BUS(TransportMode.BUS, 3)
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
            Product.STADTBAHN,
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
        Product.STADTBAHN,
        Product.BUS
    )

    override fun resolveProductStyle(product: ProductClass): StyledProfile.ProductStyle {
        return when (product) {
            Product.S_BAHN -> DBProfile.PS_S_BAHN
            Product.STADTBAHN -> PRODUCT_STYLE_STADTBAHN
            Product.BUS -> PRODUCT_STYLE_BUS
            else -> super.resolveProductStyle(product)
        }
    }

    override fun resolveLineStyle(line: Line): StyledProfile.LineStyle? {
        return when (line.product) {
            Product.S_BAHN -> when (line.label) {
                "S1", "S11" -> LINE_STYLE_S1
                "S2" -> LINE_STYLE_S2
                "S3" -> LINE_STYLE_S3
                "S4" -> LINE_STYLE_S4
                "S5" -> LINE_STYLE_S5
                "S6" -> LINE_STYLE_S6
                "S60" -> LINE_STYLE_S60
                else -> null
            }
            Product.STADTBAHN -> when (line.label) {
                "U1" -> LINE_STYLE_U1
                "U2" -> LINE_STYLE_U2
                "U3" -> LINE_STYLE_U3
                "U4" -> LINE_STYLE_U4
                "U5" -> LINE_STYLE_U5
                "U6" -> LINE_STYLE_U6
                "U7" -> LINE_STYLE_U7
                "U8" -> LINE_STYLE_U8
                "U9" -> LINE_STYLE_U9
                "U11" -> LINE_STYLE_U11
                "U12" -> LINE_STYLE_U12
                "U13" -> LINE_STYLE_U13
                "U14" -> LINE_STYLE_U14
                "U15" -> LINE_STYLE_U15
                "U16" -> LINE_STYLE_U16
                "U19" -> LINE_STYLE_U19
                "U25" -> LINE_STYLE_U25
                "U29" -> LINE_STYLE_U29
                "U34" -> LINE_STYLE_U34
                else -> null
            }
            else -> null
        }
    }

    private val PRODUCT_STYLE_STADTBAHN = StyledProfile.ProductStyle(
        0xFF00A1D5.toInt(),
        iconRes = "product_stuttgart_ic_subway_24dp",
        iconRawRes = "product_berlin_ic_subway_raw",
    )
    private val PRODUCT_STYLE_BUS = StyledProfile.ProductStyle(
        0xFFEC2B26.toInt(),
        iconRes = "product_stuttgart_ic_bus_24dp",
        iconRawRes = "product_stuttgart_ic_bus_raw",
    )

    private val LINE_STYLE_S1 = StyledProfile.LineStyle(0xFF60A92C.toInt())
    private val LINE_STYLE_S2 = StyledProfile.LineStyle(0xFFE3051B.toInt())
    private val LINE_STYLE_S3 = StyledProfile.LineStyle(0xFFEF7D00.toInt())
    private val LINE_STYLE_S4 = StyledProfile.LineStyle(0xFF005DA9.toInt())
    private val LINE_STYLE_S5 = StyledProfile.LineStyle(0xFF009ED4.toInt())
    private val LINE_STYLE_S6 = StyledProfile.LineStyle(0xFF875300.toInt())
    private val LINE_STYLE_S60 = StyledProfile.LineStyle(0xFF969200.toInt())

    private val LINE_STYLE_U1 =
        StyledProfile.LineStyle(0xFFE3A160.toInt(), shapeTextColor = 0xFF000000.toInt())
    private val LINE_STYLE_U2 =
        StyledProfile.LineStyle(0xFFF58220.toInt())
    private val LINE_STYLE_U3 =
        StyledProfile.LineStyle(0xFF946341.toInt())
    private val LINE_STYLE_U4 =
        StyledProfile.LineStyle(0xFF7967AE.toInt())
    private val LINE_STYLE_U5 =
        StyledProfile.LineStyle(0xFF00BAF1.toInt(), shapeTextColor = 0xFF000000.toInt())
    private val LINE_STYLE_U6 =
        StyledProfile.LineStyle(0xFFED008C.toInt())
    private val LINE_STYLE_U7 =
        StyledProfile.LineStyle(0xFF0BB38D.toInt(), shapeTextColor = 0xFF000000.toInt())
    private val LINE_STYLE_U8 =
        StyledProfile.LineStyle(0xFFC0B678.toInt(), shapeTextColor = 0xFF000000.toInt())
    private val LINE_STYLE_U9 =
        StyledProfile.LineStyle(0xFFFFD403.toInt(), shapeTextColor = 0xFF000000.toInt())
    private val LINE_STYLE_U11 =
        StyledProfile.LineStyle(0xFFA0A0A0.toInt())
    private val LINE_STYLE_U12 =
        StyledProfile.LineStyle(0xFF66CCCC.toInt(), shapeTextColor = 0xFF000000.toInt())
    private val LINE_STYLE_U13 =
        StyledProfile.LineStyle(0xFFF69EB2.toInt(), shapeTextColor = 0xFF000000.toInt())
    private val LINE_STYLE_U14 =
        StyledProfile.LineStyle(0xFF5DB544.toInt(), shapeTextColor = 0xFF000000.toInt())
    private val LINE_STYLE_U15 =
        StyledProfile.LineStyle(0xFF000099.toInt())
    private val LINE_STYLE_U16 =
        StyledProfile.LineStyle(0xFFBAC219.toInt(), shapeTextColor = 0xFF000000.toInt())
    private val LINE_STYLE_U19 =
        StyledProfile.LineStyle(0xFFFAB902.toInt(), shapeTextColor = 0xFF000000.toInt())
    private val LINE_STYLE_U25 =
        StyledProfile.LineStyle(0xFFA0A0A0.toInt())
    private val LINE_STYLE_U29 =
        StyledProfile.LineStyle(0xFFFFD403.toInt(), shapeTextColor = 0xFF000000.toInt())
    private val LINE_STYLE_U34 =
        StyledProfile.LineStyle(0xFF5DB544.toInt(), shapeTextColor = 0xFF000000.toInt())


}