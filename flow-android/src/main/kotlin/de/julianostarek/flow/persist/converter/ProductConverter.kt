package de.julianostarek.flow.persist.converter

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import de.jlnstrk.transit.common.model.TransportMode
import de.jlnstrk.transit.common.model.ProductClass
import de.jlnstrk.transit.common.model.ProductSet
import kotlin.reflect.KClass

@ProvidedTypeConverter
class ProductConverter(pClass: KClass<out Enum<*>>) {
    private val pValues: Array<out Enum<*>> = pClass.java.enumConstants!!

    init {
        check(ProductClass::class.java.isAssignableFrom(pClass.java)) {
            "Must be a subtype of TransportProduct, too"
        }
   }

    @TypeConverter
    fun deserialize(mask: Int?): Set<ProductClass>? {
        if (mask == null)
            return null

        val set = ProductSet()
        // Read base product bits from right
        var tmpMask: Int = mask
        var power = 0

        do {
            if (tmpMask and 1 == 1 && BASE_PRODUCTS.size > power) {
                set.add(BASE_PRODUCTS[power])
            }
            power++
            tmpMask = tmpMask ushr 1
        } while (tmpMask != 0)

        tmpMask = mask
        power = 0

        do {
            if (tmpMask and Int.MIN_VALUE == Int.MIN_VALUE && pValues.size > power) {
                set.add(pValues[power] as ProductClass)
            }
            power++
            tmpMask = tmpMask shl 1
        } while (tmpMask != 0)

        return set
    }

    @TypeConverter
    fun deserialize(value: Int): ProductClass {
        var tmpValue: Int = value
        var power = 0

        do {
            if (tmpValue and 1 == 1 && BASE_PRODUCTS.size > power) {
                return BASE_PRODUCTS[power]
            }
            power++
            tmpValue = tmpValue ushr 1
        } while (tmpValue != 0)

        tmpValue = value
        power = 0

        do {
            if (tmpValue and Int.MIN_VALUE == Int.MIN_VALUE && pValues.size > power) {
                return pValues[power] as ProductClass
            }
            power++
            tmpValue = tmpValue shl 1
        } while (tmpValue != 0)

        throw IllegalArgumentException()
    }

    @TypeConverter
    fun serialize(filter: Set<ProductClass>?): Int? {
        if (filter == null)
            return null

        var mask = 0
        // Set base product bits from right
        for (baseProduct in BASE_PRODUCTS) {
            if (filter.contains(baseProduct)) {
                mask = mask or (1 shl baseProduct.ordinal)
            }
        }
        // Set profile product bits from left
        for (constant in pValues) {
            if (filter.contains(constant as ProductClass)) {
                mask = mask or (Int.MIN_VALUE ushr constant.ordinal)
            }
        }
        return mask
    }

    @TypeConverter
    fun serialize(product: ProductClass): Int {
        for (baseProduct in BASE_PRODUCTS) {
            if (product == baseProduct) {
                return 1 shl baseProduct.ordinal
            }
        }
        for (constant in pValues) {
            if (product == constant) {
                return Int.MIN_VALUE ushr constant.ordinal
            }
        }
        throw IllegalArgumentException()
    }

    companion object {
        private val BASE_PRODUCTS = TransportMode.values()
    }
}