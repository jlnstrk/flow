package de.julianostarek.flow.profile.berlin

import de.julianostarek.flow.profile.base.LinePatternMatcher
import de.julianostarek.flow.profile.base.LocationNamePlaceMatcher
import de.julianostarek.flow.profile.base.MatchCase
import de.julianostarek.flow.profile.util.PATTERN_BERLIN_NAME_PLACE_SUFFIX
import de.jlnstrk.transit.common.model.Line
import de.jlnstrk.transit.common.model.Location
import de.jlnstrk.transit.common.model.TransportMode
import de.jlnstrk.transit.common.model.ProductClass
import de.jlnstrk.transit.common.normalize.NameAndPlace
import de.jlnstrk.transit.common.normalize.Normalization
import de.jlnstrk.transit.interop.hafas.HafasClassMapping

object BvgHafas : HafasClassMapping.OneToOne, Normalization {
    override val mapping: Array<ProductClass> = arrayOf(
        BerlinProfile.Product.S_BAHN,
        BerlinProfile.Product.U_BAHN,
        BerlinProfile.Product.TRAM,
        BerlinProfile.Product.BUS,
        BerlinProfile.Product.FAEHRE,
        TransportMode.TRAIN,
        BerlinProfile.Product.BAHN
    )
    private val LINE_PATTERNS = LinePatternMatcher(
        LinePatternMatcher.ProductClassPattern.PrefixS to BerlinProfile.Product.S_BAHN,
        LinePatternMatcher.ProductClassPattern.PrefixU to BerlinProfile.Product.U_BAHN,
        LinePatternMatcher.ProductClassPattern.PrefixX to BerlinProfile.Product.EXPRESS_BUS
    )
    private val NAME_PLACE_PATTERNS = LocationNamePlaceMatcher(BerlinPattern)

    object BerlinPattern : MatchCase<NameAndPlace, Any?> {
        override val regex: Regex
            get() = PATTERN_BERLIN_NAME_PLACE_SUFFIX

        override fun applyMatch(subject: NameAndPlace, result: MatchResult, value: Any?) {
            subject.name = result.groupValues[1]
            subject.place = result.groupValues[2]
            val suffix = result.groupValues[3]
            if (suffix.isNotEmpty()) {
                subject.name += " • $suffix"
            }
        }
    }

    override fun normalizeNameAndPlace(location: Location): NameAndPlace? {
        val nameAndPlace = NAME_PLACE_PATTERNS.normalizeNameAndPlace(location)
        when (nameAndPlace?.place) {
            "Bln" -> nameAndPlace.place = "Berlin"
        }
        return nameAndPlace
    }

    override fun normalizeLine(line: Line): Line {
        if (line.product === TransportMode.OTHER) return LINE_PATTERNS.normalizeLine(line)

        var newProduct: ProductClass? = null
        when (line.product) {
            BerlinProfile.Product.TRAM -> when (line.label[0]) {
                'M' -> newProduct = BerlinProfile.Product.METRO_TRAM
            }
            BerlinProfile.Product.BUS -> when (line.label[0]) {
                'M' -> newProduct = BerlinProfile.Product.METRO_BUS
                'X' -> newProduct = BerlinProfile.Product.EXPRESS_BUS
            }
        }

        return if (newProduct != null) line.copy(product = newProduct) else line
    }

    /*override fun normalizeLocation(location: Location) {
        val matcher = PATTERN_PLACE_IN_PARENTHESES.matchEntire(location.name.orEmpty())
        if (matcher != null) {
            location.name = matcher.groupValues[1]
            location.place = matcher.groupValues[2]
        }
    }

    override fun normalizeJourney(journey: Journey) {
        val matcher = PATTERN_PLACE_IN_PARENTHESES.matchEntire(journey.direction.orEmpty())
        if (matcher != null) {
            journey.direction = matcher.groupValues[1]
        }

    }*/
}