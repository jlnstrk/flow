package de.julianostarek.flow.profile.hamburg

import de.julianostarek.flow.profile.base.LinePatternMatcher
import de.julianostarek.flow.profile.util.PATTERN_SPLIT_PLACE_COMMA_NAME
import de.jlnstrk.transit.common.model.Line
import de.jlnstrk.transit.common.model.Location
import de.jlnstrk.transit.common.model.TransportMode
import de.jlnstrk.transit.common.model.ProductClass
import de.jlnstrk.transit.common.normalize.NameAndPlace
import de.jlnstrk.transit.common.normalize.Normalization
import de.jlnstrk.transit.interop.hafas.HafasClassMapping

object HvvHafas : HafasClassMapping.OneToOne, Normalization {
    override val mapping: Array<ProductClass> = arrayOf(
        HamburgProfile.Product.U_BAHN,
        HamburgProfile.Product.S_BAHN,
        HamburgProfile.Product.AKN, // -> AKN
        HamburgProfile.Product.REGIONAL_EXPRESS,
        HamburgProfile.Product.REGIONAL_BAHN,
        HamburgProfile.Product.FAEHRE,
        TransportMode.TRAIN, // -> LONG DISTANCE TRAIN
        HamburgProfile.Product.STADT_BUS,
        HamburgProfile.Product.SCHNELL_BUS,
        TransportMode.OTHER, // -> UNKNOWN
        HamburgProfile.Product.AST,
        TransportMode.BUS, // -> LONG DISTANCE BUS
        TransportMode.TRAIN, // -> LONG DISTANCE TRAIN
    )
    private val LINE_PATTERNS = LinePatternMatcher(
        LinePatternMatcher.ProductClassPattern.PrefixRbRe to HamburgProfile.Product.REGIONAL_BAHN,
        LinePatternMatcher.ProductClassPattern.PrefixS to HamburgProfile.Product.S_BAHN,
        LinePatternMatcher.ProductClassPattern.PrefixU to HamburgProfile.Product.U_BAHN,
        LinePatternMatcher.ProductClassPattern.PrefixBus to HamburgProfile.Product.STADT_BUS
    )

    override fun normalizeNameAndPlace(location: Location): NameAndPlace? {
        var newName: String? = null
        var newPlace: String? = null

        if (location.name != null) run matching@{
            val matchResult = PATTERN_SPLIT_PLACE_COMMA_NAME.matchEntire(location.name!!)
            if (matchResult != null) {
                newPlace = matchResult.groupValues[1]
                newName = matchResult.groupValues[2]
                return@matching
            }
        }

        if (newName != null) {
            return NameAndPlace(newName!!, newPlace)
        }
        return null
    }

    override fun normalizeLine(line: Line): Line = LINE_PATTERNS.normalizeLine(line)
}