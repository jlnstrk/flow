package de.julianostarek.flow.profile.berlin

import de.jlnstrk.transit.client.hci.HciConfig
import de.jlnstrk.transit.client.hci.model.HciAuth
import de.jlnstrk.transit.client.hci.model.HciAuthType
import de.jlnstrk.transit.client.hci.model.HciExtension
import de.jlnstrk.transit.client.hci.model.HciVersion
import de.jlnstrk.transit.client.hci.model.client.HciClient
import de.jlnstrk.transit.client.hci.model.client.HciClientId
import de.jlnstrk.transit.client.hci.model.client.HciClientType
import de.jlnstrk.transit.common.normalize.Normalization
import de.jlnstrk.transit.interop.hafas.HafasClassMapping
import de.jlnstrk.transit.interop.hci.HciProvider
import kotlinx.datetime.TimeZone

object BvgHci : HciProvider(),
    HafasClassMapping by BvgHafas,
    Normalization by BvgHafas {
    override val timezone: TimeZone = TimeZone.of("Europe/Berlin")
    override val config: HciConfig = HciConfig {
        baseUrl = "https://bvg-apps-ext.hafas.de/bin/"
        ver = HciVersion._1_44
        client = HciClient(
            type = HciClientType.IPA,
            id = HciClientId.BVG,
            name = "FahrInfo",
            v = 6020000
        )
        ext = HciExtension.BVG_1
        auth = HciAuth(
            aid = "Mz0YdF9Fgx0Mb9",
            type = HciAuthType.AID
        )
    }
}