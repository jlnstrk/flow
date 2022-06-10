package de.julianostarek.flow.ui.main.stops.stationboard.merged

import de.jlnstrk.transit.common.model.Journey
import de.jlnstrk.transit.common.model.Line
import de.jlnstrk.transit.common.model.Location
import de.jlnstrk.transit.common.model.ProductClass
import de.jlnstrk.transit.common.model.base.Identifiable

data class MergedJourney(
    override val id: String,
    val line: Line,
    val direction: Location,
    val journeys: List<Journey>
) : Identifiable

fun Journey.groupHash(): String {
    var result = line.product.hashCode().toLong()
    result = 31 * result + line.label.hashCode()
    result = 31 * result + (directionTo ?: directionFrom)!!.place.hashCode()
    result = 31 * result + (directionTo ?: directionFrom)!!.name.hashCode()
    return result.toString()
}

inline fun Collection<Journey>.merged(): List<MergedJourney> {
    return groupBy(Journey::groupHash)
        .map {
            val first = it.value.first()
            MergedJourney(it.key, first.line, (first.directionTo ?: first.directionFrom)!!, it.value)
        }
}

@JvmName("mergedGroupByProduct")
inline fun Collection<MergedJourney>.groupByProduct(): Map<ProductClass, List<MergedJourney>> {
    return groupBy { it.line.product }
}

inline fun Collection<Journey>.groupByProduct(): Map<ProductClass, List<Journey>> {
    return groupBy { it.line.product }
}