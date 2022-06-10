package de.julianostarek.flow.profile.hamburg

import de.julianostarek.flow.BuildKonfig
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

object HvvHci : HciProvider(),
    HafasClassMapping by HvvHafas,
    Normalization by HvvHafas {
    override val config: HciConfig = HciConfig {
        baseUrl = "https://hvv-app.hafas.de/bin/"
        ver = HciVersion._1_18
        ext = HciExtension.HVV_1
        client = HciClient(
            type = HciClientType.AND,
            id = HciClientId.HVV,
            name = "HVVPROD_ADHOC",
            v = 4020100
        )
        auth = HciAuth(
            aid = "andcXUmC9Mq6hjrwDIGd2l3oiaMrTUzyH",
            type = HciAuthType.AID
        )
        salt = BuildKonfig.HVV_SALT
    }
}