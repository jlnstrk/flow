package de.julianostarek.flow.profile.frankfurt

import de.julianostarek.flow.profile.FlowProduct
import de.julianostarek.flow.profile.StyledProfile
import de.julianostarek.flow.profile.DBProfile
import de.jlnstrk.transit.common.Profile
import de.jlnstrk.transit.common.model.Line
import de.jlnstrk.transit.common.model.TransportMode
import de.jlnstrk.transit.common.model.ProductClass

object FrankfurtProfile : Profile, StyledProfile {

    enum class Product(
        override val mode: TransportMode,
        override val id: Int,
        override val label: String,
        override val showAtStations: Boolean = true
    ) : FlowProduct {
        REGIONALZUG(TransportMode.TRAIN, 0, "RE/RB"),
        S_BAHN(TransportMode.TRAIN, 1, "S-Bahn"),
        U_BAHN(TransportMode.SUBWAY, 2, "U-Bahn"),
        TRAM(TransportMode.LIGHT_RAIL, 3, "Tram"),
        METRO_BUS(TransportMode.BUS, 4, "MetroBus"),
        EXPRESS_BUS(TransportMode.BUS, 5, "ExpressBus"),
        BUS(TransportMode.BUS, 6, "Bus"),
        NACHT_BUS(TransportMode.BUS, 7, "NachtBus", showAtStations = false),
        SEV(TransportMode.BUS, 8, "SEV", showAtStations = false),
        AST(TransportMode.CAR, 9, "AnrufSammelTaxi")
    }

    override val filterConfig: Array<Profile.FilterEntry> = arrayOf(
        Profile.FilterEntry(
            Product.METRO_BUS,
            Product.EXPRESS_BUS,
            Product.BUS,
            Product.NACHT_BUS,
            Product.SEV,
            Product.AST,
            isDefault = true,
            styleOf = Product.BUS
        ),
        Profile.FilterEntry(
            Product.TRAM,
            isDefault = true
        ),
        Profile.FilterEntry(
            Product.U_BAHN,
            isDefault = true
        ),
        Profile.FilterEntry(
            Product.S_BAHN,
            isDefault = true
        ),
        Profile.FilterEntry(
            Product.REGIONALZUG,
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

    override fun resolveProductStyle(
        product: ProductClass
    ): StyledProfile.ProductStyle {
        return when (product) {
            Product.REGIONALZUG -> PRODUCT_STYLE_REGIONAL
            Product.S_BAHN -> DBProfile.PS_S_BAHN
            Product.U_BAHN -> PRODUCT_STYLE_SUBWAY
            Product.TRAM -> PRODUCT_STYLE_TRAM
            Product.METRO_BUS,
            Product.EXPRESS_BUS,
            Product.BUS,
            Product.NACHT_BUS -> PRODUCT_STYLE_BUS
            Product.SEV -> PRODUCT_STYLE_SEV
            else -> super.resolveProductStyle(product)
        }
    }

    override fun resolveLineStyle(line: Line): StyledProfile.LineStyle? {
        return when (line.product) {
            Product.S_BAHN -> {
                if (line.admin == "800528" || line.admin == null) {
                    when (line.label) {
                        "S1" -> LINE_STYLE_S1
                        "S2" -> LINE_STYLE_S2
                        "S3" -> LINE_STYLE_S3
                        "S4" -> LINE_STYLE_S4
                        "S5" -> LINE_STYLE_S5
                        "S6" -> LINE_STYLE_S6
                        "S7" -> LINE_STYLE_S7
                        "S8" -> LINE_STYLE_S8
                        "S9" -> LINE_STYLE_S9
                        else -> null
                    }
                } else null
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
            Product.TRAM -> when (line.label) {
                "11" -> LINE_STYLE_11
                "12" -> LINE_STYLE_12
                "14" -> LINE_STYLE_14
                "15" -> LINE_STYLE_15
                "16" -> LINE_STYLE_16
                "17" -> LINE_STYLE_17
                "18" -> LINE_STYLE_18
                "19" -> LINE_STYLE_19
                "20" -> LINE_STYLE_20
                "21" -> LINE_STYLE_21
                else -> null
            }
            Product.METRO_BUS -> LINE_STYLE_MBUS
            Product.EXPRESS_BUS -> when (line.label) {
                // local
                "X17" -> LINE_STYLE_XBUS_X17
                "X19" -> LINE_STYLE_XBUS_X19
                "X26" -> LINE_STYLE_XBUS_X26
                "X27" -> LINE_STYLE_XBUS_X27
                "X57" -> LINE_STYLE_XBUS_X57
                "X64" -> LINE_STYLE_XBUS_X64
                "X83" -> LINE_STYLE_XBUS_X83
                "X97" -> LINE_STYLE_XBUS_X97

                // regional
                "X33" -> LINE_STYLE_XBUS_X33
                "X37" -> LINE_STYLE_XBUS_X37
                "X38" -> LINE_STYLE_XBUS_X38
                "X39" -> LINE_STYLE_XBUS_X39
                "X40" -> LINE_STYLE_XBUS_X40
                "X41" -> LINE_STYLE_XBUS_X41
                "X71" -> LINE_STYLE_XBUS_X71
                "X72" -> LINE_STYLE_XBUS_X72
                "X74" -> LINE_STYLE_XBUS_X74
                "X76" -> LINE_STYLE_XBUS_X76
                "X78" -> LINE_STYLE_XBUS_X78
                "X79" -> LINE_STYLE_XBUS_X79
                "X89" -> LINE_STYLE_XBUS_X89
                "X93" -> LINE_STYLE_XBUS_X93
                else -> null
            }
            Product.NACHT_BUS -> when {
                line.label.length == 2 -> LINE_STYLE_NSUB
                line.label.length == 3 -> LINE_STYLE_NTRAM
                else -> null
            }
            else -> null
        }
    }

    private val PRODUCT_STYLE_REGIONAL = StyledProfile.ProductStyle(
        0xFF000000.toInt(),
        iconRes = "product_frankfurt_ic_regional_train_24dp",
        iconRawRes = "product_frankfurt_ic_regional_train_raw",
    )
    private val PRODUCT_STYLE_SUBWAY = StyledProfile.ProductStyle(
        0xFF0070BA.toInt(),
        iconRes = "product_frankfurt_ic_subway_24dp",
        iconRawRes = "product_frankfurt_ic_subway_raw",
    )
    private val PRODUCT_STYLE_TRAM = StyledProfile.ProductStyle(
        0xFFF36F21.toInt(),
        iconRes = "product_frankfurt_ic_tram_24dp",
        iconRawRes = "product_frankfurt_ic_tram_raw",
    )
    private val PRODUCT_STYLE_BUS = StyledProfile.ProductStyle(
        0xFFA2238E.toInt(),
        iconRes = "product_frankfurt_ic_bus_24dp",
        iconRawRes = "product_frankfurt_ic_bus_raw",
    )
    private val PRODUCT_STYLE_SEV = StyledProfile.ProductStyle(
        0xFFA2238E.toInt(),
        iconRes = "product_frankfurt_ic_sev_24dp",
    )

    private val LINE_STYLE_S1 = StyledProfile.LineStyle(0xFF0095D9.toInt())
    private val LINE_STYLE_S2 = StyledProfile.LineStyle(0xFFEE1D23.toInt())
    private val LINE_STYLE_S3 = StyledProfile.LineStyle(0xFF01A896.toInt())
    private val LINE_STYLE_S4 =
        StyledProfile.LineStyle(0xFFFFCA0A.toInt(), shapeTextColor = 0xFF000000.toInt())
    private val LINE_STYLE_S5 = StyledProfile.LineStyle(0xFF955B36.toInt())
    private val LINE_STYLE_S6 = StyledProfile.LineStyle(0xFFF57921.toInt())
    private val LINE_STYLE_S7 = StyledProfile.LineStyle(0xFF20543E.toInt())
    private val LINE_STYLE_S8 = StyledProfile.LineStyle(0xFF8CC63E.toInt())
    private val LINE_STYLE_S9 = StyledProfile.LineStyle(0xFF902690.toInt())

    private val LINE_STYLE_U1 = StyledProfile.LineStyle(0xFFA5061C.toInt())
    private val LINE_STYLE_U2 = StyledProfile.LineStyle(0xFF12AC5D.toInt())
    private val LINE_STYLE_U3 = StyledProfile.LineStyle(0xFF485296.toInt())
    private val LINE_STYLE_U4 = StyledProfile.LineStyle(0xFFE13885.toInt())
    private val LINE_STYLE_U5 = StyledProfile.LineStyle(0xFF237542.toInt())
    private val LINE_STYLE_U6 = StyledProfile.LineStyle(0xFF208DCC.toInt())
    private val LINE_STYLE_U7 = StyledProfile.LineStyle(0xFFDE9206.toInt())
    private val LINE_STYLE_U8 = StyledProfile.LineStyle(0xFFBF66A1.toInt())
    private val LINE_STYLE_U9 =
        StyledProfile.LineStyle(0xFFE5C001.toInt(), shapeTextColor = 0xFF000000.toInt())

    private val LINE_STYLE_11 = StyledProfile.LineStyle(0xFF8781BD.toInt())
    private val LINE_STYLE_12 = StyledProfile.LineStyle(0xFFE8B809.toInt())
    private val LINE_STYLE_14 = StyledProfile.LineStyle(0xFF00A6E0.toInt())
    private val LINE_STYLE_15 = StyledProfile.LineStyle(0xFFF58220.toInt())
    private val LINE_STYLE_16 = StyledProfile.LineStyle(0xFF4CB848.toInt())
    private val LINE_STYLE_17 = StyledProfile.LineStyle(0xFFEE1E24.toInt())
    private val LINE_STYLE_18 = StyledProfile.LineStyle(0xFF18469D.toInt())
    private val LINE_STYLE_19 =
        StyledProfile.LineStyle(0xFF77CCD0.toInt(), fill = StyledProfile.LineStyle.Fill.STROKE)
    private val LINE_STYLE_20 = StyledProfile.LineStyle(0xFF939598.toInt())
    private val LINE_STYLE_21 = StyledProfile.LineStyle(0xFFF392BC.toInt())

    private val LINE_STYLE_XBUS_X17 = StyledProfile.LineStyle(
        0xFFDCA327.toInt(),
        shapeTextColor = 0xFF000000.toInt()
    )
    private val LINE_STYLE_XBUS_X19 = StyledProfile.LineStyle(0xFF132759.toInt())
    private val LINE_STYLE_XBUS_X26 = StyledProfile.LineStyle(0xFF006441.toInt())
    private val LINE_STYLE_XBUS_X27 = StyledProfile.LineStyle(0xFFBE7332.toInt())
    private val LINE_STYLE_XBUS_X57 = StyledProfile.LineStyle(0xFF00BEC4.toInt())
    private val LINE_STYLE_XBUS_X64 = StyledProfile.LineStyle(0xFFE7792B.toInt())
    private val LINE_STYLE_XBUS_X83 = StyledProfile.LineStyle(0xFFB41669.toInt())
    private val LINE_STYLE_XBUS_X97 = StyledProfile.LineStyle(0xFF84BF41.toInt())

    private val LINE_STYLE_XBUS_X33 = StyledProfile.LineStyle(0xFF292D78.toInt())
    private val LINE_STYLE_XBUS_X37 = StyledProfile.LineStyle(0xFF3F7183.toInt())
    private val LINE_STYLE_XBUS_X38 = StyledProfile.LineStyle(0xFFE05656.toInt())
    private val LINE_STYLE_XBUS_X39 = StyledProfile.LineStyle(0xFFAD703F.toInt())
    private val LINE_STYLE_XBUS_X40 = StyledProfile.LineStyle(0xFF40B075.toInt())
    private val LINE_STYLE_XBUS_X41 = StyledProfile.LineStyle(0xFFA7A329.toInt())
    private val LINE_STYLE_XBUS_X71 = StyledProfile.LineStyle(0xFF009C7F.toInt())
    private val LINE_STYLE_XBUS_X72 = StyledProfile.LineStyle(0xFFD92053.toInt())
    private val LINE_STYLE_XBUS_X74 = StyledProfile.LineStyle(0xFFD8232A.toInt())
    private val LINE_STYLE_XBUS_X76 = StyledProfile.LineStyle(0xFF4A4562.toInt())
    private val LINE_STYLE_XBUS_X78 = StyledProfile.LineStyle(0xFF00B0DE.toInt())
    private val LINE_STYLE_XBUS_X79 = StyledProfile.LineStyle(0xFF00656F.toInt())
    private val LINE_STYLE_XBUS_X89 = StyledProfile.LineStyle(0xFF7F78AB.toInt())
    private val LINE_STYLE_XBUS_X93 = StyledProfile.LineStyle(0xFF815267.toInt())

    private val LINE_STYLE_MBUS = StyledProfile.LineStyle(PRODUCT_STYLE_BUS.productColor)
    private val LINE_STYLE_NSUB = StyledProfile.LineStyle(PRODUCT_STYLE_SUBWAY.productColor)
    private val LINE_STYLE_NTRAM = StyledProfile.LineStyle(PRODUCT_STYLE_TRAM.productColor)

}