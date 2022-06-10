package de.julianostarek.flow.profile.frankfurt

import de.julianostarek.flow.BuildKonfig
import de.jlnstrk.transit.interop.hafas.HafasClassMapping
import de.jlnstrk.transit.interop.hapi.HapiProvider
import kotlinx.datetime.TimeZone

object RmvHapi : HapiProvider(), HafasClassMapping.OneToOne by RmvHafas {

    /* endpoint specs */
    override val baseUrl: String = "https://www.rmv.de/hapi/"

    /* endpoint auth */
    override val accessId: String = BuildKonfig.RMV_AID

    override val timezone: TimeZone = TimeZone.of("Europe/Berlin")
}