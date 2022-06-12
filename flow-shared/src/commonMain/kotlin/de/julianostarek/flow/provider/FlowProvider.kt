package de.julianostarek.flow.provider

import de.julianostarek.flow.provider.service.FlowNetworkGeometryService
import de.julianostarek.flow.provider.service.FlowNetworkMapsService
import de.jlnstrk.transit.common.Provider
import de.jlnstrk.transit.common.service.NetworkGeometryService
import de.jlnstrk.transit.common.service.NetworkMapsService
import de.julianostarek.flow.profile.FlowProfile
import kotlinx.datetime.TimeZone

open class FlowProvider(
    private val profile: () -> FlowProfile
) : Provider.Implementation() {
    override val timezone: TimeZone get() = TimeZone.of("Europe/Berlin")

    init {
        registerService<NetworkMapsService> {
            FlowNetworkMapsService(profile())
        }
        registerService<NetworkGeometryService> {
            FlowNetworkGeometryService(profile())
        }
    }
}