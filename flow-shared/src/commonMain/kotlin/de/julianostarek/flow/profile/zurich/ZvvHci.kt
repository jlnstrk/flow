package de.julianostarek.flow.profile.zurich

import de.julianostarek.flow.profile.util.PATTERN_SPLIT_PLACE_COMMA_NAME
import de.jlnstrk.transit.client.hci.HciConfig
import de.jlnstrk.transit.client.hci.model.HciAuth
import de.jlnstrk.transit.client.hci.model.HciAuthType
import de.jlnstrk.transit.client.hci.model.HciExtension
import de.jlnstrk.transit.client.hci.model.HciVersion
import de.jlnstrk.transit.client.hci.model.client.HciClient
import de.jlnstrk.transit.client.hci.model.client.HciClientId
import de.jlnstrk.transit.client.hci.model.client.HciClientType
import de.jlnstrk.transit.common.model.Location
import de.jlnstrk.transit.common.normalize.NameAndPlace
import de.jlnstrk.transit.interop.hci.HciProvider
import kotlinx.datetime.TimeZone

object ZvvHci : HciProvider(), ZvvHafas {
    override val timezone: TimeZone = TimeZone.of("Europe/Berlin")
    override val config: HciConfig = HciConfig {
        baseUrl = "https://online.fahrplan.zvv.ch/bin/"
        ver = HciVersion._1_42
        ext = HciExtension.ZVV_2
        client = HciClient(
            type = HciClientType.IPH,
            id = HciClientId.ZVV,
            name = "zvvPROD-STORE",
            v = 6000400
        )
        auth = HciAuth(
            aid = "TLRUqdDPF7ttB824Yoy2BN8mk",
            type = HciAuthType.AID
        )
    }

    override fun normalizeNameAndPlace(location: Location): NameAndPlace? {
        var newName: String? = null
        var newPlace: String? = null

        if (location.name != null) {
            when (location) {
                /*is Location.Station -> {
                    val matcher = PATTERN_SPLIT_NAME_COMMA_PLACE.matchEntire(name)
                    if (matcher != null) {
                        effective.place = matcher.groupValues[2]
                        effective.name = matcher.groupValues[1]
                    }
                }*/
                else -> {
                    val matcher = PATTERN_SPLIT_PLACE_COMMA_NAME.matchEntire(location.name!!)
                    if (matcher != null) {
                        newPlace = matcher.groupValues[1]
                        newName = matcher.groupValues[2]
                    }
                }
            }
        }

        if (newName != null) {
            return NameAndPlace(newName, newPlace)
        }
        return null
    }
}