package de.julianostarek.flow.profile.munich

import de.julianostarek.flow.BuildKonfig
import de.julianostarek.flow.profile.munich.MunichProfile.Product
import de.julianostarek.flow.profile.util.PATTERN_SPLIT_NAME_COMMA_PLACE
import de.julianostarek.flow.profile.util.PATTERN_SPLIT_PLACE_COMMA_NAME
import de.jlnstrk.transit.client.hci.HciConfig
import de.jlnstrk.transit.client.hci.model.HciAuth
import de.jlnstrk.transit.client.hci.model.HciAuthType
import de.jlnstrk.transit.client.hci.model.HciExtension
import de.jlnstrk.transit.client.hci.model.HciVersion
import de.jlnstrk.transit.client.hci.model.client.HciClient
import de.jlnstrk.transit.client.hci.model.client.HciClientId
import de.jlnstrk.transit.client.hci.model.client.HciClientType
import de.jlnstrk.transit.common.extensions.toProductSet
import de.jlnstrk.transit.common.model.Line
import de.jlnstrk.transit.common.model.Location
import de.jlnstrk.transit.common.normalize.NameAndPlace
import de.jlnstrk.transit.common.normalize.Normalization
import de.jlnstrk.transit.interop.hafas.HafasClassMapping
import de.jlnstrk.transit.interop.hci.HciProvider
import kotlinx.datetime.TimeZone

object SbmHci : HciProvider(),
    HafasClassMapping by SbmHafas, Normalization {
    override val timezone: TimeZone = TimeZone.of("Europe/Berlin")
    override val config: HciConfig = HciConfig {
        baseUrl = "https://s-bahn-muenchen.hafas.de/bin/540/"
        ver = HciVersion._1_34
        ext = HciExtension.DB_R_15_12_A
        client = HciClient(
            type = HciClientType.IPH,
            id = HciClientId.DB_REGIO_MVV,
            name = "MuenchenNavigator",
            v = 5010100
        )
        auth = HciAuth(
            type = HciAuthType.AID,
            aid = "d491MVVhz9ZZts23"
        )
        salt = BuildKonfig.SBM_SALT
    }

    override fun normalizeNameAndPlace(location: Location): NameAndPlace? {
        var newName: String? = null
        var newPlace: String? = null

        if (location.name != null) {
            when (location) {
                is Location.Station -> {
                    val matcher = PATTERN_SPLIT_NAME_COMMA_PLACE.matchEntire(location.name)
                    if (matcher != null) {
                        newPlace = matcher.groupValues[2]
                        newName = matcher.groupValues[1]
                    }
                }
                else -> {
                    val matcher = PATTERN_SPLIT_PLACE_COMMA_NAME.matchEntire(location.name!!)
                    if (matcher != null) {
                        newPlace = matcher.groupValues[1]
                        newName = matcher.groupValues[2]
                    }
                }
            }
        }
        if (location.name == "Bahnhof") {
            newName = location.place + ' ' + location.name
        }

        return if (newName != null) NameAndPlace(newName, newPlace) else null
    }

    override fun normalizeStation(station: Location.Station): Location.Station {
        val shouldBeProducts = (station.products.orEmpty() + station.lines.orEmpty().map(Line::product))
            .toProductSet()
        if (shouldBeProducts != station.products) {
            return station.copy(products = shouldBeProducts)
        }
        return station
    }

    override fun normalizeLine(line: Line): Line {
        var newLabel = line.label
        var newProduct = line.product
        if (line.product == Product.REGIONALZUG) {
            newLabel = newLabel.substringAfter("BRB")
        }
        newLabel = line.label.replace(" ", "")
        when (line.product) {
            Product.STADT_BUS -> when (newLabel[0]) {
                'X' -> newProduct = Product.EXPRESS_BUS
                'N' -> newProduct = Product.NACHT_BUS
                else -> when {
                    newLabel.length == 2 -> newProduct = Product.METRO_BUS
                    newLabel.length == 3
                            && newLabel[0] != '1' -> newProduct = Product.REGIONAL_BUS
                }
            }
            Product.TRAM -> when (line.label[0]) {
                'N' -> newProduct = Product.NACHT_TRAM
            }
        }
        if (line.label != newLabel || line.product != newProduct) {
            return line.copy(label = newLabel, product = newProduct)
        }
        return line
    }

}