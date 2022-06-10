package de.julianostarek.flow.profile.berlin

import de.jlnstrk.transit.interop.hafas.HafasClassMapping
import de.jlnstrk.transit.interop.hapi.HapiProvider
import de.julianostarek.flow.BuildKonfig
import kotlinx.datetime.TimeZone

object VbbHapi : HapiProvider(), HafasClassMapping by BvgHafas {
    override val baseUrl: String = "https://demo.hafas.de/openapi/vbb-proxy/"
    override val accessId: String = BuildKonfig.VBB_AID

    override val timezone: TimeZone = TimeZone.of("Europe/Berlin")
}