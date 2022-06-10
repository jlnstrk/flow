package de.julianostarek.flow.profile.berlin

import de.julianostarek.flow.profile.DBProfile
import de.julianostarek.flow.profile.FlowProduct
import de.julianostarek.flow.profile.StyledProfile
import de.jlnstrk.transit.common.Profile
import de.jlnstrk.transit.common.model.Line
import de.jlnstrk.transit.common.model.TransportMode
import de.jlnstrk.transit.common.model.ProductClass

object BerlinProfile : Profile, StyledProfile {

    enum class Product(
        override val mode: TransportMode,
        override val id: Int,
        override val label: String
    ) : FlowProduct {
        S_BAHN(TransportMode.TRAIN, 0, "S-Bahn"),
        U_BAHN(TransportMode.SUBWAY, 1, "U-Bahn"),
        BAHN(TransportMode.TRAIN, 2, "Bahn"),
        METRO_TRAM(TransportMode.LIGHT_RAIL, 3, "MetroTram"),
        TRAM(TransportMode.LIGHT_RAIL, 4, "Tram"),
        METRO_BUS(TransportMode.BUS, 5, "MetroBus"),
        EXPRESS_BUS(TransportMode.BUS, 6, "ExpressBus"),
        BUS(TransportMode.BUS, 7, "Bus"),
        FAEHRE(TransportMode.WATERCRAFT, 8, "Fähre")
    }

    override val filterConfig = arrayOf(
        Profile.FilterEntry(
            Product.S_BAHN,
            isDefault = true
        ),
        Profile.FilterEntry(
            Product.U_BAHN,
            isDefault = true
        ),
        Profile.FilterEntry(
            Product.BAHN,
            isDefault = true
        ),
        Profile.FilterEntry(
            Product.TRAM, Product.METRO_TRAM,
            isDefault = true,
            styleOf = Product.TRAM
        ),
        Profile.FilterEntry(
            Product.EXPRESS_BUS, Product.METRO_BUS, Product.BUS,
            isDefault = true,
            styleOf = Product.BUS
        ),
        Profile.FilterEntry(
            Product.FAEHRE,
            isDefault = true
        )
    )

    override val brandingConfig: Array<ProductClass> = arrayOf(
        Product.S_BAHN,
        Product.U_BAHN,
        Product.BAHN,
        Product.TRAM,
        Product.BUS,
        Product.FAEHRE,
    )

    override fun resolveProductStyle(product: ProductClass): StyledProfile.ProductStyle {
        return when (product) {
            Product.BAHN -> PRODUCT_STYLE_REGIONAL
            Product.S_BAHN -> DBProfile.PS_S_BAHN
            Product.U_BAHN -> PRODUCT_STYLE_SUBWAY
            Product.METRO_TRAM,
            Product.TRAM -> PRODUCT_STYLE_TRAM
            Product.METRO_BUS,
            Product.EXPRESS_BUS,
            Product.BUS -> PRODUCT_STYLE_BUS
            Product.FAEHRE -> PRODUCT_STYLE_FERRY
            else -> super.resolveProductStyle(product)
        }
    }

    override fun resolveLineStyle(line: Line): StyledProfile.LineStyle? {
        return when (line.product) {
            Product.S_BAHN -> when (line.label) {
                "S1" -> LINE_STYLE_S1
                "S2" -> LINE_STYLE_S2
                "S25" -> LINE_STYLE_S25
                "S26" -> LINE_STYLE_S26
                "S3" -> LINE_STYLE_S3
                "S41" -> LINE_STYLE_S41
                "S42" -> LINE_STYLE_S42
                "S45" -> LINE_STYLE_S45
                "S46" -> LINE_STYLE_S46
                "S47" -> LINE_STYLE_S47
                "S5" -> LINE_STYLE_S5
                "S7" -> LINE_STYLE_S7
                "S75" -> LINE_STYLE_S75
                "S8" -> LINE_STYLE_S8
                "S85" -> LINE_STYLE_S85
                "S9" -> LINE_STYLE_S9
                else -> null
            }
            Product.U_BAHN -> when (line.label) {
                "U1" -> LINE_STYLE_U1
                "U2" -> LINE_STYLE_U2
                "U3" -> LINE_STYLE_U3
                "U4" -> LINE_STYLE_U4
                "U5" -> LINE_STYLE_U5
                "U6" -> LINE_STYLE_U6
                "U7" -> LINE_STYLE_U7
                "U8" -> LINE_STYLE_U8
                "U9" -> LINE_STYLE_U9
                else -> null
            }
            Product.METRO_TRAM -> when (line.label) {
                "M1" -> LINE_STYLE_M1
                "M2" -> LINE_STYLE_M2
                "M4" -> LINE_STYLE_M4
                "M5" -> LINE_STYLE_M5
                "M6" -> LINE_STYLE_M6
                "M8" -> LINE_STYLE_M8
                "M10" -> LINE_STYLE_M10
                "M13" -> LINE_STYLE_M13
                "M17" -> LINE_STYLE_M17
                else -> null
            }
            Product.TRAM -> when (line.label) {
                "12" -> LINE_STYLE_12
                "16" -> LINE_STYLE_16
                "18" -> LINE_STYLE_18
                "21" -> LINE_STYLE_21
                "27" -> LINE_STYLE_27
                "37" -> LINE_STYLE_37
                "50" -> LINE_STYLE_50
                "60" -> LINE_STYLE_60
                "61" -> LINE_STYLE_61
                "62" -> LINE_STYLE_62
                "63" -> LINE_STYLE_63
                "67" -> LINE_STYLE_67
                "68" -> LINE_STYLE_68
                else -> null
            }
            else -> null
        }
    }

    private val PRODUCT_STYLE_REGIONAL = StyledProfile.ProductStyle(
        0xFFDA251D.toInt(),
        iconRes = "product_berlin_ic_regional_train_24dp",
        iconRawRes = "product_berlin_ic_regional_train_raw",
    )
    private val PRODUCT_STYLE_SUBWAY = StyledProfile.ProductStyle(
        0xFF0664AB.toInt(),
        iconRes = "product_berlin_ic_subway_24dp",
        iconRawRes = "product_berlin_ic_subway_raw",
    )
    private val PRODUCT_STYLE_TRAM = StyledProfile.ProductStyle(
        0xFFCC0000.toInt(),
        iconRes = "product_berlin_ic_tram_24dp",
        iconRawRes = "product_berlin_ic_tram_raw",
    )
    private val PRODUCT_STYLE_BUS = StyledProfile.ProductStyle(
        0xFFA3007C.toInt(),
        iconRes = "product_berlin_ic_bus_24dp",
        iconRawRes = "product_berlin_ic_bus_raw",
    )
    private val PRODUCT_STYLE_FERRY = StyledProfile.ProductStyle(
        0xFF0080C0.toInt(),
        iconRes = "product_berlin_ic_ferry_24dp",
        iconRawRes = "product_berlin_ic_ferry_raw",
    )

    private val LINE_STYLE_S1 = StyledProfile.LineStyle(0xFFEB588F.toInt())
    private val LINE_STYLE_S2 = StyledProfile.LineStyle(0xFF047939.toInt())
    private val LINE_STYLE_S25 = StyledProfile.LineStyle(0xFF047939.toInt())
    private val LINE_STYLE_S26 = StyledProfile.LineStyle(0xFF047939.toInt())
    private val LINE_STYLE_S3 = StyledProfile.LineStyle(0xFF0A4B9A.toInt())
    private val LINE_STYLE_S41 = StyledProfile.LineStyle(
        0xFFAA3C1F.toInt(),
        featureIconRes = "product_generic_ic_ring_clockwise_nopadding_10dp"
    )
    private val LINE_STYLE_S42 = StyledProfile.LineStyle(
        0xFFBA622D.toInt(),
        featureIconRes = "product_generic_ic_ring_anticlockwise_nopadding_10dp"
    )
    private val LINE_STYLE_S45 = StyledProfile.LineStyle(0xFFCA8539.toInt())
    private val LINE_STYLE_S46 = StyledProfile.LineStyle(0xFFCA8539.toInt())
    private val LINE_STYLE_S47 = StyledProfile.LineStyle(0xFFCA8539.toInt())
    private val LINE_STYLE_S5 = StyledProfile.LineStyle(0xFFEA561C.toInt())
    private val LINE_STYLE_S7 = StyledProfile.LineStyle(0xFF764D9A.toInt())
    private val LINE_STYLE_S75 = StyledProfile.LineStyle(0xFF764D9A.toInt())
    private val LINE_STYLE_S8 = StyledProfile.LineStyle(0xFF4FA433.toInt())
    private val LINE_STYLE_S85 = StyledProfile.LineStyle(0xFF4FA433.toInt())
    private val LINE_STYLE_S9 = StyledProfile.LineStyle(0xFF951732.toInt())

    private val LINE_STYLE_U1 = StyledProfile.LineStyle(0xFF54A821.toInt())
    private val LINE_STYLE_U2 = StyledProfile.LineStyle(0xFFFD3300.toInt())
    private val LINE_STYLE_U3 = StyledProfile.LineStyle(0xFF009277.toInt())
    private val LINE_STYLE_U4 =
        StyledProfile.LineStyle(0xFFFDD802.toInt(), shapeTextColor = 0xFF000000.toInt())
    private val LINE_STYLE_U5 = StyledProfile.LineStyle(0xFF673019.toInt())
    private val LINE_STYLE_U6 = StyledProfile.LineStyle(0xFF73569C.toInt())
    private val LINE_STYLE_U7 = StyledProfile.LineStyle(0xFF3790C0.toInt())
    private val LINE_STYLE_U8 = StyledProfile.LineStyle(0xFF0C3C84.toInt())
    private val LINE_STYLE_U9 = StyledProfile.LineStyle(0xFFFD7301.toInt())

    private val LINE_STYLE_M1 = StyledProfile.LineStyle(0xFF63B8E9.toInt())
    private val LINE_STYLE_M2 = StyledProfile.LineStyle(0xFF79BA28.toInt())
    private val LINE_STYLE_M4 = StyledProfile.LineStyle(0xFFCA1114.toInt())
    private val LINE_STYLE_M5 = StyledProfile.LineStyle(0xFFC7893A.toInt())
    private val LINE_STYLE_M6 = StyledProfile.LineStyle(0xFF005595.toInt())
    private val LINE_STYLE_M8 = StyledProfile.LineStyle(0xFFED7103.toInt())
    private val LINE_STYLE_M10 = StyledProfile.LineStyle(0xFF017A3C.toInt())
    private val LINE_STYLE_M13 = StyledProfile.LineStyle(0xFF00A092.toInt())
    private val LINE_STYLE_M17 = StyledProfile.LineStyle(0xFFA64229.toInt())
    private val LINE_STYLE_12 = StyledProfile.LineStyle(0xFF8770A9.toInt())
    private val LINE_STYLE_16 = StyledProfile.LineStyle(0xFF007EA9.toInt())
    private val LINE_STYLE_18 = StyledProfile.LineStyle(0xFFD7AC01.toInt())
    private val LINE_STYLE_21 = StyledProfile.LineStyle(0xFFBC90C0.toInt())
    private val LINE_STYLE_27 = StyledProfile.LineStyle(0xFFC96218.toInt())
    private val LINE_STYLE_37 = StyledProfile.LineStyle(0xFFA6412A.toInt())
    private val LINE_STYLE_50 = StyledProfile.LineStyle(0xFFEA9000.toInt())
    private val LINE_STYLE_60 = StyledProfile.LineStyle(0xFF009BD8.toInt())
    private val LINE_STYLE_61 = StyledProfile.LineStyle(0xFFE30612.toInt())
    private val LINE_STYLE_62 = StyledProfile.LineStyle(0xFF00522E.toInt())
    private val LINE_STYLE_63 = StyledProfile.LineStyle(0xFFEC7103.toInt())
    private val LINE_STYLE_67 = StyledProfile.LineStyle(0xFFDD6BA5.toInt())
    private val LINE_STYLE_68 = StyledProfile.LineStyle(0xFF65B32E.toInt())
}