package de.julianostarek.flow.profile.rheinruhr

import de.jlnstrk.transit.interop.efa.EfaProvider
import de.jlnstrk.transit.interop.efa.normalization.EfaMeansNormalization
import kotlinx.datetime.TimeZone

object VrrEfa : EfaProvider(), EfaMeansNormalization.Simple {
    override val timezone: TimeZone = TimeZone.of("Europe/Berlin")
    override val baseUrl: String = "https:///efa.vrr.de/vrr/"
}