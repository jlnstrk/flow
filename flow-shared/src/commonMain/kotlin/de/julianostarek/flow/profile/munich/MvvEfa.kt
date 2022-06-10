package de.julianostarek.flow.profile.munich

import de.jlnstrk.transit.client.efa.model.EfaMeansOfTransport
import de.jlnstrk.transit.common.model.Line
import de.jlnstrk.transit.interop.efa.EfaProvider
import de.jlnstrk.transit.interop.efa.normalization.EfaMeansNormalization
import kotlinx.datetime.TimeZone

object MvvEfa : EfaProvider(), EfaMeansNormalization.Simple {
    override val timezone: TimeZone = TimeZone.of("Europe/Berlin")
    override val baseUrl: String = "https://efa.mvv-muenchen.de/ng/"

    override val baseMap = EfaMeansNormalization.Simple.MeansBiMap(
        EfaMeansOfTransport.COMMUTER_RAILWAY to MunichProfile.Product.S_BAHN,
        EfaMeansOfTransport.SUBWAY to MunichProfile.Product.U_BAHN,
        EfaMeansOfTransport.TRAM to MunichProfile.Product.TRAM,
        EfaMeansOfTransport.CITY_BUS to MunichProfile.Product.STADT_BUS,
        EfaMeansOfTransport.REGIONAL_BUS to MunichProfile.Product.REGIONAL_BUS,
        EfaMeansOfTransport.EXPRESS_BUS to MunichProfile.Product.EXPRESS_BUS,
        EfaMeansOfTransport.TAXI_ON_DEMAND to MunichProfile.Product.RUFTAXI,
        EfaMeansOfTransport.TRAIN_LOCAL to MunichProfile.Product.REGIONALZUG
    )

    override fun resolveAltCode(from: Int): EfaMeansOfTransport? {
        return when (from) {
            1 -> EfaMeansOfTransport.SUBWAY
            2 -> EfaMeansOfTransport.COMMUTER_RAILWAY
            3 -> EfaMeansOfTransport.CITY_BUS
            4 -> EfaMeansOfTransport.TRAM
            6 -> EfaMeansOfTransport.TRAIN_LOCAL
            8 -> EfaMeansOfTransport.TAXI_ON_DEMAND
            else -> null
        }
    }

    override fun normalizeLine(line: Line): Line {
        var newLabel = line.number ?: line.label
        var newProduct = line.product

        when (line.product) {
            MunichProfile.Product.STADT_BUS -> {
                when (line.label[0]) {
                    'N' -> newProduct = MunichProfile.Product.NACHT_BUS
                    else -> when {
                        line.label.length == 2 -> newProduct = MunichProfile.Product.METRO_BUS
                    }
                }
            }
        }

        return line.copy(label = newLabel, product = newProduct)
    }
}