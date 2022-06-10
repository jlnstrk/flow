package de.julianostarek.flow.profile.vienna

import de.jlnstrk.transit.client.hci.HciConfig
import de.jlnstrk.transit.client.hci.model.HciAuth
import de.jlnstrk.transit.client.hci.model.HciAuthType
import de.jlnstrk.transit.client.hci.model.HciVersion
import de.jlnstrk.transit.client.hci.model.client.HciClient
import de.jlnstrk.transit.client.hci.model.client.HciClientId
import de.jlnstrk.transit.client.hci.model.client.HciClientType
import de.jlnstrk.transit.common.model.Line
import de.jlnstrk.transit.interop.hci.HciProvider
import kotlinx.datetime.TimeZone

object OebbHci : HciProvider(), OebbHafas {
    override val timezone: TimeZone = TimeZone.of("Europe/Vienna")
    override val config: HciConfig = HciConfig {
        baseUrl = "https://fahrplan.oebb.at/bin/"
        ver = HciVersion._1_16
        client = HciClient(
            type = HciClientType.AND,
            id = HciClientId.OEBB,
            v = 6000500,
            name = "oebbIPAD_ADHOC",
            //os = "iOS 10.3.3"
        )
        auth = HciAuth(
            aid = "OWDL4fE4ixNiPBBm", // "OWDL4fE4ixNiPBBm"
            type = HciAuthType.AID
        )
    }

    override fun normalizeLine(line: Line): Line = line.copy(label = line.shortName ?: line.label)
}