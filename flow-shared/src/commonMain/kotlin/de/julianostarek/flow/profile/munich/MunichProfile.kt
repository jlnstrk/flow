package de.julianostarek.flow.profile.munich

import de.julianostarek.flow.profile.FlowProduct
import de.julianostarek.flow.profile.StyledProfile
import de.julianostarek.flow.profile.DBProfile
import de.jlnstrk.transit.common.Profile
import de.jlnstrk.transit.common.model.Line
import de.jlnstrk.transit.common.model.TransportMode
import de.jlnstrk.transit.common.model.ProductClass

object MunichProfile : Profile, StyledProfile {

    enum class Product(
        override val mode: TransportMode,
        override val id: Int,
        override val label: String,
        override val showAtStations: Boolean = true
    ) : FlowProduct {
        S_BAHN(TransportMode.TRAIN, 0, "S-Bahn"),
        U_BAHN(TransportMode.SUBWAY, 1, "U-Bahn"),
        REGIONALZUG(TransportMode.TRAIN, 2, "R-Bahn"),

        // Tram
        TRAM(TransportMode.LIGHT_RAIL, 3, "Tram"),
        NACHT_TRAM(TransportMode.LIGHT_RAIL, 4, "NachtTram", showAtStations = false),

        // Bus
        METRO_BUS(TransportMode.BUS, 5, "MetroBus"),
        STADT_BUS(TransportMode.BUS, 6, "StadtBus"),
        REGIONAL_BUS(TransportMode.BUS, 7, "RegionalBus"),
        EXPRESS_BUS(TransportMode.BUS, 8, "ExpressBus"),
        NACHT_BUS(TransportMode.BUS, 9, "NachtBus", showAtStations = false),

        RUFTAXI(TransportMode.OTHER, 10, "RufTaxi")
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
            Product.REGIONALZUG,
            isDefault = false
        ),
        Profile.FilterEntry(
            Product.TRAM, Product.NACHT_TRAM,
            isDefault = true,
            styleOf = Product.TRAM
        ),
        Profile.FilterEntry(
            Product.METRO_BUS,
            Product.STADT_BUS,
            Product.REGIONAL_BUS,
            Product.EXPRESS_BUS,
            Product.NACHT_BUS,

            // also includes
            Product.RUFTAXI,
            isDefault = true,
            styleOf = Product.STADT_BUS
        )
    )

    override val brandingConfig: Array<ProductClass> = arrayOf(
        Product.S_BAHN,
        Product.U_BAHN,
        Product.REGIONALZUG,
        Product.TRAM,
        Product.STADT_BUS,
        Product.EXPRESS_BUS
    )

    override fun resolveProductStyle(product: ProductClass): StyledProfile.ProductStyle {
        return when (product) {
            Product.S_BAHN -> DBProfile.PS_S_BAHN
            Product.U_BAHN -> PS_UBAHN
            Product.REGIONALZUG -> PS_REGIONAL
            Product.TRAM -> PS_TRAM
            Product.METRO_BUS,
            Product.STADT_BUS,
            Product.REGIONAL_BUS -> PS_BUS
            Product.EXPRESS_BUS -> PS_EXPRESSBUS
            Product.NACHT_TRAM -> PS_TRAM
            Product.NACHT_BUS -> PS_NACHTLINIEN
            Product.RUFTAXI -> PS_RUFTAXI
            else -> super.resolveProductStyle(product)
        }
    }

    override fun resolveLineStyle(line: Line): StyledProfile.LineStyle? {
        if (line.name == null) return null
        return when (line.product) {
            Product.S_BAHN -> when (line.label) {
                "S1" -> LS_S1
                "S2" -> LS_S2
                "S3" -> LS_S3
                "S4" -> LS_S4
                "S6" -> LS_S6
                "S7" -> LS_S7
                "S8" -> LS_S8
                "S20" -> LS_S20
                else -> null
            }
            Product.U_BAHN -> when (line.label) {
                "U1" -> LS_U1
                "U2" -> LS_U2
                "U3" -> LS_U3
                "U4" -> LS_U4
                "U5" -> LS_U5
                "U6" -> LS_U6
                "U7" -> LS_U7
                "U8" -> LS_U8
                else -> null
            }
            Product.TRAM -> when (line.label) {
                "12" -> LS_12
                "15" -> LS_15
                "16" -> LS_16
                "17" -> LS_17
                "18" -> LS_18
                "19" -> LS_19
                "20" -> LS_20
                "21" -> LS_21
                "23" -> LS_23
                "25" -> LS_25
                "27" -> LS_27
                "28" -> LS_28
                "29" -> LS_29
                else -> LS_TRAM
            }
            Product.METRO_BUS -> when (line.label) {
                "58" -> LS_BUS_58
                "68" -> LS_BUS_68
                else -> LS_METRO_BUS
            }
            Product.STADT_BUS -> if (line.label == "100") LS_BUS_100 else null
            Product.EXPRESS_BUS -> LS_EXPRESS_BUS
            Product.NACHT_BUS -> LS_NACHT_BUS
            else -> null
        }
    }

    private val PS_REGIONAL = StyledProfile.ProductStyle(
        productColor = 0xFF36397F.toInt(),
        iconRes = "product_munich_ic_regional_train_24dp",
        iconRawRes = "product_munich_ic_regional_train_raw",
    )
    private val PS_UBAHN = StyledProfile.ProductStyle(
        productColor = 0xFF0068B0.toInt(),
        iconRes = "product_munich_ic_subway_24dp",
        iconRawRes = "product_munich_ic_subway_raw",
    )
    private val PS_TRAM = StyledProfile.ProductStyle(
        productColor = 0xFFD82020.toInt(),
        iconRes = "product_munich_ic_tram_24dp",
        iconRawRes = "product_munich_ic_tram_raw",
    )
    private val PS_EXPRESSBUS = StyledProfile.ProductStyle(
        productColor = 0xFF4D9380.toInt(),
        iconRes = "product_munich_ic_express_bus_24dp",
        iconRawRes = "product_munich_ic_express_bus_raw",
    )
    private val PS_BUS = StyledProfile.ProductStyle(
        productColor = 0xFF00586A.toInt(),
        iconRes = "product_munich_ic_bus_24dp",
        iconRawRes = "product_munich_ic_bus_raw",
    )
    private val PS_NACHTLINIEN = StyledProfile.ProductStyle(
        productColor = 0xFF000000.toInt(),
        iconRes = "product_munich_ic_night_24dp",
        iconRawRes = "product_munich_ic_night_raw",
    )
    private val PS_RUFTAXI = StyledProfile.ProductStyle(
        productColor = 0xFF00586A.toInt(),
        iconRes = "product_munich_ic_ruftaxi_24dp",
    )

    private val LS_S1 = StyledProfile.LineStyle(0xFF16C0E9.toInt())
    private val LS_S2 = StyledProfile.LineStyle(0xFF71BF44.toInt())
    private val LS_S3 = StyledProfile.LineStyle(0xFF7B107D.toInt())
    private val LS_S4 = StyledProfile.LineStyle(0xFFEE1C25.toInt())
    private val LS_S6 = StyledProfile.LineStyle(0xFF008A51.toInt())
    private val LS_S7 = StyledProfile.LineStyle(0xFF963833.toInt())
    private val LS_S8 = StyledProfile.LineStyle(0xFF000000.toInt(),shapeTextColor = 0xFFFFCB06.toInt())
    private val LS_S20 = StyledProfile.LineStyle(0xFF963833.toInt(),fill = StyledProfile.LineStyle.Fill.STROKE)

    private val LS_U1 = StyledProfile.LineStyle(0xFF52822F.toInt())
    private val LS_U2 = StyledProfile.LineStyle(0xFFC20831.toInt())
    private val LS_U3 = StyledProfile.LineStyle(0xFFEC6725.toInt())
    private val LS_U4 = StyledProfile.LineStyle(0xFF00A984.toInt())
    private val LS_U5 = StyledProfile.LineStyle(0xFFBC7A00.toInt())
    private val LS_U6 = StyledProfile.LineStyle(0xFF0065AE.toInt())
    private val LS_U7 = StyledProfile.LineStyle(
        0xFF52822F.toInt(),
        shapeSecondaryColor = 0xFFC20831.toInt()
    )
    private val LS_U8 = StyledProfile.LineStyle(
        0xFFC20831.toInt(),
        shapeSecondaryColor = 0xFFEC6725.toInt()
    )

    private val LS_12 = StyledProfile.LineStyle(0xFF903F97.toInt())
    private val LS_15 = StyledProfile.LineStyle(
        0xFFF48F99.toInt(),
        fill = StyledProfile.LineStyle.Fill.STROKE
    )
    private val LS_16 = StyledProfile.LineStyle(0xFF006CB3.toInt())
    private val LS_17 = StyledProfile.LineStyle(0xFF875A46.toInt())
    private val LS_18 = StyledProfile.LineStyle(0xFF20B14A.toInt())
    private val LS_19 = StyledProfile.LineStyle(0xFFEE1C25.toInt())
    private val LS_20 = StyledProfile.LineStyle(0xFF16C0E9.toInt())
    private val LS_21 = StyledProfile.LineStyle(0xFFBB8DCD.toInt())
    private val LS_23 = StyledProfile.LineStyle(0xFFB3D235.toInt())
    private val LS_25 = StyledProfile.LineStyle(0xFFF48F99.toInt())
    private val LS_27 = StyledProfile.LineStyle(0xFFFBA61C.toInt())
    private val LS_28 = StyledProfile.LineStyle(
        0xFFFBA61C.toInt(),
        fill = StyledProfile.LineStyle.Fill.STROKE
    )
    private val LS_29 = StyledProfile.LineStyle(
        0xFFEE1C25.toInt(),
        fill = StyledProfile.LineStyle.Fill.STROKE
    )

    private val LS_TRAM = StyledProfile.LineStyle(0xFFD82020.toInt())


    private val LS_METRO_BUS = StyledProfile.LineStyle(0xFFE4672F.toInt())
    private val LS_BUS_100 = StyledProfile.LineStyle(
        0xFF00586A.toInt(),
        featureIconRes = "product_generic_ic_museum_nopadding_10dp"
    )
    private val LS_BUS_58 = StyledProfile.LineStyle(
        0xFFE4672F.toInt(),
        featureIconRes = "product_generic_ic_ring_clockwise_nopadding_10dp"
    )
    private val LS_BUS_68 = StyledProfile.LineStyle(
        0xFFE4672F.toInt(),
        featureIconRes = "product_generic_ic_ring_anticlockwise_nopadding_10dp"
    )
    private val LS_EXPRESS_BUS = StyledProfile.LineStyle(0xFF4D9380.toInt())
    private val LS_NACHT_BUS = StyledProfile.LineStyle(
        0xFF000000.toInt(),
        shapeTextColor = 0xFFFABB00.toInt()
    )

}