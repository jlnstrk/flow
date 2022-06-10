package de.julianostarek.flow.profile.frankfurt

import de.julianostarek.flow.profile.base.LinePatternMatcher
import de.julianostarek.flow.profile.util.PATTERN_SPLIT_PLACE_COMMA_NAME
import de.jlnstrk.transit.common.model.Line
import de.jlnstrk.transit.common.model.Location
import de.jlnstrk.transit.common.model.TransportMode
import de.jlnstrk.transit.common.model.ProductClass
import de.jlnstrk.transit.common.normalize.NameAndPlace
import de.jlnstrk.transit.common.normalize.Normalization
import de.jlnstrk.transit.interop.hafas.HafasClassMapping

object RmvHafas : HafasClassMapping.OneToOne, Normalization {
    override val mapping: Array<ProductClass> = arrayOf(
        TransportMode.TRAIN,
        TransportMode.TRAIN,
        FrankfurtProfile.Product.REGIONALZUG,
        FrankfurtProfile.Product.S_BAHN,
        FrankfurtProfile.Product.U_BAHN,
        FrankfurtProfile.Product.TRAM,
        FrankfurtProfile.Product.BUS,
        FrankfurtProfile.Product.SEV, // -> BUS SEV
        TransportMode.OTHER, // -> UNKNOWN
        FrankfurtProfile.Product.AST // -> TAXI SEV
    )

    private val LINE_PATTERNS = LinePatternMatcher(
        LinePatternMatcher.ProductClassPattern.PrefixRbRe to FrankfurtProfile.Product.REGIONALZUG,
        LinePatternMatcher.ProductClassPattern.PrefixS to FrankfurtProfile.Product.S_BAHN,
        LinePatternMatcher.ProductClassPattern.PrefixU to FrankfurtProfile.Product.U_BAHN,
        LinePatternMatcher.ProductClassPattern.PrefixTram to FrankfurtProfile.Product.TRAM,
        LinePatternMatcher.ProductClassPattern.PrefixBus to FrankfurtProfile.Product.BUS,
    )

    private val PATTERN_SPLIT_PLACE_SPACE_NAME =
        Regex("((?:Bad )?[\\p{L}]+(?: (?:\\([\\p{L}.]+\\)|(?:an|am|a\\.|v\\.) ?(?:der|d\\.)? ?[\\p{L}.]+))?(?:-(?:Bad )?[\\p{L}]+)*)(?: |-)(.+)")

    override fun normalizeNameAndPlace(location: Location): NameAndPlace? {
        var newName: String? = null
        var newPlace: String? = null
        if (location.name != null) {
            run namePlace@{
                var matchResult = PATTERN_SPLIT_PLACE_COMMA_NAME.matchEntire(location.name!!)
                if (matchResult != null) {
                    newPlace = matchResult.groupValues[1]
                    newName = matchResult.groupValues[2]
                    return@namePlace
                }
                matchResult = PATTERN_SPLIT_PLACE_SPACE_NAME.matchEntire(location.name!!)
                if (matchResult != null) {
                    newPlace = matchResult.groupValues[1]
                    newName = matchResult.groupValues[2]
                    return@namePlace
                }
            }
        }

        when (location.name) {
            "HB",
            "Hbf",
            "Hauptbahnhof",
            "Hauptbahnhof tief",
            "Südbahnhof" -> newName = location.place + ' ' + location.name
            "Bahnhof" -> newName = location.place
        }

        if (newName != null || newPlace != location.place) {
            return NameAndPlace(newName!!, newPlace)
        }
        return null
    }

    override fun normalizeLine(line: Line): Line {
        if (line.product === TransportMode.OTHER) return LINE_PATTERNS.normalizeLine(line)

        var newProduct: ProductClass? = null
        if (line.product == FrankfurtProfile.Product.BUS) {
            when (line.label[0]) {
                'X' -> newProduct = FrankfurtProfile.Product.EXPRESS_BUS
                'M' -> newProduct = FrankfurtProfile.Product.METRO_BUS
                'N', 'n' -> newProduct = FrankfurtProfile.Product.NACHT_BUS
            }
        }

        return if (newProduct != null) line.copy(product = newProduct) else line
    }

}