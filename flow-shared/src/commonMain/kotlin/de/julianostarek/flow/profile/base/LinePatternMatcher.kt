package de.julianostarek.flow.profile.base

import de.jlnstrk.transit.common.model.Line
import de.jlnstrk.transit.common.model.Location
import de.jlnstrk.transit.common.model.TransportMode
import de.jlnstrk.transit.common.model.ProductClass
import de.jlnstrk.transit.common.normalize.NameAndPlace
import de.jlnstrk.transit.common.normalize.Normalization

data class LineProperties(
    var label: String? = null,
    var product: ProductClass? = null
)


interface MatchCase<Subject, CaseValue> {
    val regex: Regex

    fun applyMatch(subject: Subject, result: MatchResult, value: CaseValue)
}

class LocationNamePlaceMatcher(
    private vararg val cases: MatchCase<NameAndPlace, Any?>
) : Normalization {
    override fun normalizeNameAndPlace(location: Location): NameAndPlace? {
        if (location.name == null) return null
        var matchResult: MatchResult? = null
        for (matchCase in cases) {
            matchResult = matchCase.regex.matchEntire(location.name!!)
            if (matchResult != null) {
                val container = NameAndPlace(location.name!!)
                matchCase.applyMatch(container, matchResult, null)
                return container
            }
        }
        return null
    }
}

class LinePatternMatcher(
    private vararg val cases: Pair<MatchCase<LineProperties, ProductClass>, ProductClass>
) : Normalization {

    override fun normalizeLine(line: Line): Line {
        if (line.product !== TransportMode.OTHER) {
            return line
        }

        var matchResult: MatchResult? = null
        for ((matchCase, productClass) in cases) {
            matchResult = matchCase.regex.matchEntire(line.label)
            if (matchResult != null) {
                val container = LineProperties()
                matchCase.applyMatch(container, matchResult, productClass)
                return line.copy(
                    label = container.label ?: line.label,
                    product = container.product ?: line.product
                )
            }
        }
        return line
    }

    sealed interface ProductClassPattern : MatchCase<LineProperties, ProductClass> {

        override fun applyMatch(subject: LineProperties, result: MatchResult, value: ProductClass) {
            subject.product = value
        }

        object PrefixRbRe : ProductClassPattern {
            override val regex = Regex(".*(R[BE] ?[1-9]\\d{0,4})")

            override fun applyMatch(subject: LineProperties, result: MatchResult, value: ProductClass) {
                super.applyMatch(subject, result, value)
                subject.label = result.groupValues[1]
            }
        }

        object PrefixS : ProductClassPattern {
            override val regex = Regex("(?:S-Bahn )?(S ?\\d+)")

            override fun applyMatch(subject: LineProperties, result: MatchResult, value: ProductClass) {
                super.applyMatch(subject, result, value)
                subject.label = result.groupValues[1].replace(" ", "")
            }
        }

        object PrefixU : ProductClassPattern {
            override val regex = Regex("U ?[1-9]\\d{0,2}")

            override fun applyMatch(subject: LineProperties, result: MatchResult, value: ProductClass) {
                super.applyMatch(subject, result, value)
                subject.label = result.groupValues[0]
            }
        }

        object PrefixN : ProductClassPattern {
            override val regex = Regex("N ?[1-9]\\d{0,2}")

            override fun applyMatch(subject: LineProperties, result: MatchResult, value: ProductClass) {
                super.applyMatch(subject, result, value)
                subject.label = result.groupValues[0]
            }
        }

        object PrefixX : ProductClassPattern {
            override val regex = Regex("(?:ExpressBus )?(X ?\\d+)")

            override fun applyMatch(subject: LineProperties, result: MatchResult, value: ProductClass) {
                super.applyMatch(subject, result, value)
                subject.label = result.groupValues[1].replace(" ", "")
            }
        }

        object PrefixBus : ProductClassPattern {
            override val regex = Regex("Bus (\\d+)")

            override fun applyMatch(subject: LineProperties, result: MatchResult, value: ProductClass) {
                super.applyMatch(subject, result, value)
                subject.label = result.groupValues[1]
            }
        }

        object PrefixTram : ProductClassPattern {
            override val regex = Regex("Tram (\\d+)")

            override fun applyMatch(subject: LineProperties, result: MatchResult, value: ProductClass) {
                super.applyMatch(subject, result, value)
                subject.label = result.groupValues[1]
            }
        }
    }
}