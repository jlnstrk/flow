package de.julianostarek.flow.profile.frankfurt

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

object RmvHci : HciProvider(),
    HafasClassMapping.OneToOne by RmvHafas,
    Normalization by RmvHafas {
    override val timezone: TimeZone = TimeZone.of("Europe/Berlin")
    override val config: HciConfig = HciConfig {
        baseUrl = "https://www.rmv.de/auskunft/bin/jp/"
        ver = HciVersion._1_44
        ext = HciExtension.RMV_1
        client = HciClient(
            type = HciClientType.WEB,
            id = HciClientId.RMV,
            name = "webapp",
        )
        auth = HciAuth(
            aid = "x0k4ZR33ICN9CWmj",
            type = HciAuthType.AID
        )
    }
}