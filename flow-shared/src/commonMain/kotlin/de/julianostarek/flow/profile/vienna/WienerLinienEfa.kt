package de.julianostarek.flow.profile.vienna

import de.jlnstrk.transit.client.efa.model.EfaMeansOfTransport
import de.jlnstrk.transit.common.model.ProductClass
import de.jlnstrk.transit.interop.efa.EfaProvider
import de.jlnstrk.transit.interop.efa.normalization.EfaMeansNormalization
import kotlinx.datetime.TimeZone

object WienerLinienEfa : EfaProvider(), EfaMeansNormalization.Simple {
    override val timezone: TimeZone = TimeZone.of("Europe/Vienna")
    override val baseUrl: String = "https://www.wienerlinien.at/ogd_routing/"

    override val baseMap = EfaMeansNormalization.Simple.MeansBiMap(
        EfaMeansOfTransport.COMMUTER_RAILWAY to ViennaProfile.Product.S_BAHN,
        EfaMeansOfTransport.SUBWAY to ViennaProfile.Product.U_BAHN,
        EfaMeansOfTransport.TRAM to ViennaProfile.Product.TRAM,
        EfaMeansOfTransport.CITY_BUS to ViennaProfile.Product.BUS,
        EfaMeansOfTransport.OTHER to ViennaProfile.Product.NIGHT_LINE
    )


    override fun denormalizeEfaMeans(productClasses: Set<ProductClass>): MutableSet<EfaMeansOfTransport> {
        val set = super.denormalizeEfaMeans(productClasses)
        if (productClasses.contains(ViennaProfile.Product.BUS)) {
            set.add(EfaMeansOfTransport.OTHER)
        }
        return set
    }
}