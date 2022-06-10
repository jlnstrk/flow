package de.julianostarek.flow.profile.stuttgart

import de.jlnstrk.transit.client.efa.model.EfaMeansOfTransport
import de.jlnstrk.transit.interop.efa.EfaProvider
import de.jlnstrk.transit.interop.efa.normalization.EfaMeansNormalization
import kotlinx.datetime.TimeZone

object VvsEfa : EfaProvider(), EfaMeansNormalization.Simple {
    override val timezone: TimeZone = TimeZone.of("Europe/Berlin")
    override val baseUrl: String = "https://www2.vvs.de/vvs/"

    override val baseMap = EfaMeansNormalization.Simple.MeansBiMap(
        EfaMeansOfTransport.TRAIN_LOCAL to StuttgartProfile.Product.REGIONALZUG,
        EfaMeansOfTransport.COMMUTER_RAILWAY to StuttgartProfile.Product.S_BAHN,
        EfaMeansOfTransport.CITY_RAIL to StuttgartProfile.Product.STADTBAHN,
        EfaMeansOfTransport.CITY_BUS to StuttgartProfile.Product.BUS,
    )
}