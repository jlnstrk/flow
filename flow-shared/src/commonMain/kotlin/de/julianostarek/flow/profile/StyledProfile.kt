package de.julianostarek.flow.profile

import de.jlnstrk.transit.common.model.Line
import de.jlnstrk.transit.common.model.TransportMode
import de.jlnstrk.transit.common.model.ProductClass

interface StyledProfile {

    fun resolveProductStyle(line: Line): ProductStyle =
        resolveProductStyle(line.product)

    fun resolveProductStyle(product: ProductClass): ProductStyle {
        return when (product.mode) {
            TransportMode.TRAIN -> PRODUCT_GENERIC_TRAIN
            TransportMode.SUBWAY -> PRODUCT_GENERIC_SUBWAY
            TransportMode.LIGHT_RAIL -> PRODUCT_GENERIC_LIGHT_RAIL
            TransportMode.BUS -> PRODUCT_GENERIC_BUS
            TransportMode.CABLE,
            TransportMode.CAR,
            TransportMode.WATERCRAFT,
            TransportMode.OTHER -> PRODUCT_UNKNOWN
        }
    }

    fun resolveLineStyle(line: Line): LineStyle?

    class ProductStyle(
        val productColor: Int,
        val iconRes: String,
        val iconRawRes: String? = null,
        val isFallback: Boolean = false
    )

    data class LineStyle(
        val shapePrimaryColor: Int,
        val shapeSecondaryColor: Int = 0,
        val shapeTextColor: Int = 0xFFFFFFFF.toInt(),
        val featureIconRes: String? = null,
        val fill: Fill = Fill.SOLID,
        val shape: Shape = Shape.ROUND_RECT
    ) {

        enum class Fill {
            SOLID, STROKE
        }

        enum class Shape {
            OVAL, RECT, ROUND_RECT,
        }

        enum class Pattern {
            DASHED, DIAGONAL
        }
    }

    companion object {
        val PRODUCT_GENERIC_TRAIN: ProductStyle = ProductStyle(
            productColor = 0xFF333333.toInt(),
            iconRes = "product_generic_ic_train_24dp",
            iconRawRes = "product_generic_ic_train_24dp",
            isFallback = true
        )
        val PRODUCT_GENERIC_SUBWAY: ProductStyle = ProductStyle(
            productColor = 0xFF333333.toInt(),
            iconRes = "product_generic_ic_subway_24dp",
            iconRawRes = "product_generic_ic_subway_24dp",
            isFallback = true
        )
        val PRODUCT_GENERIC_LIGHT_RAIL: ProductStyle = ProductStyle(
            productColor = 0xFF333333.toInt(),
            iconRes = "product_generic_ic_light_rail_24dp",
            iconRawRes = "product_generic_ic_light_rail_24dp",
            isFallback = true
        )
        val PRODUCT_GENERIC_BUS: ProductStyle = ProductStyle(
            productColor = 0xFF333333.toInt(),
            iconRes = "product_generic_ic_bus_24dp",
            iconRawRes = "product_generic_ic_bus_24dp",
            isFallback = true
        )
        val PRODUCT_UNKNOWN: ProductStyle = ProductStyle(
            productColor = 0xFF333333.toInt(),
            iconRes = "product_generic_ic_unknown_24dp",
            iconRawRes = "product_generic_ic_unknown_raw",
            isFallback = true
        )
    }
}