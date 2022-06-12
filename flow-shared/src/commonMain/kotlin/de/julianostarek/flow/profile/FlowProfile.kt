package de.julianostarek.flow.profile

import de.jlnstrk.transit.common.CompositeProvider
import de.jlnstrk.transit.common.Profile
import de.jlnstrk.transit.common.Provider
import de.jlnstrk.transit.common.extensions.use
import de.jlnstrk.transit.common.model.ProductClass
import de.jlnstrk.transit.common.service.NetworkGeometryService
import de.jlnstrk.transit.common.service.NetworkMapsService
import de.julianostarek.flow.profile.berlin.BerlinFlow
import de.julianostarek.flow.profile.berlin.BerlinProfile
import de.julianostarek.flow.profile.berlin.BvgHci
import de.julianostarek.flow.profile.frankfurt.FrankfurtFlow
import de.julianostarek.flow.profile.frankfurt.FrankfurtProfile
import de.julianostarek.flow.profile.frankfurt.RmvHci
import de.julianostarek.flow.profile.hamburg.HamburgFlow
import de.julianostarek.flow.profile.hamburg.HamburgProfile
import de.julianostarek.flow.profile.hamburg.HvvHci
import de.julianostarek.flow.profile.munich.MunichFlow
import de.julianostarek.flow.profile.munich.MunichProfile
import de.julianostarek.flow.profile.munich.MvvEfa
import de.julianostarek.flow.profile.munich.SbmHci
import de.julianostarek.flow.profile.rheinruhr.RheinRuhrFlow
import de.julianostarek.flow.profile.rheinruhr.RheinRuhrProfile
import de.julianostarek.flow.profile.rheinruhr.VrrEfa
import de.julianostarek.flow.profile.stuttgart.StuttgartFlow
import de.julianostarek.flow.profile.stuttgart.StuttgartProfile
import de.julianostarek.flow.profile.stuttgart.VvsEfa
import de.julianostarek.flow.profile.vienna.ViennaFlow
import de.julianostarek.flow.profile.vienna.ViennaProfile
import de.julianostarek.flow.profile.vienna.WienerLinienEfa
import de.julianostarek.flow.profile.zurich.ZurichProfile
import de.julianostarek.flow.profile.zurich.ZvvHci
import kotlin.reflect.KClass

enum class FlowProfile(
    val profile: Profile,
    val products: List<FlowProduct>,
    val provider: Provider,
    val styles: StyledProfile = profile as StyledProfile
) {
    MUNICH(
        MunichProfile,
        MunichProfile.Product.values().toList(),
        CompositeProvider {
            useAll(SbmHci)
            // use<StatusInformationService>(MvvEfa)
            of(MunichFlow) {
                use<NetworkMapsService>()
                use<NetworkGeometryService>()
            }
        }
    ),

    MUNICH2(
        MunichProfile,
        MunichProfile.Product.values().toList(),
        CompositeProvider {
            useAll(MvvEfa)
            of(MunichFlow) {
                use<NetworkMapsService>()
                use<NetworkGeometryService>()
            }
        }
    ),

    STUTTGART(
        StuttgartProfile,
        StuttgartProfile.Product.values().toList(),
        CompositeProvider {
            useAll(VvsEfa)
            use<NetworkMapsService>(StuttgartFlow)
        }
    ),

    FRANKFURT(
        FrankfurtProfile,
        FrankfurtProfile.Product.values().toList(),
        CompositeProvider {
            useAll(RmvHci)
            use<NetworkMapsService>(FrankfurtFlow)
        }
    ),

    BERLIN(
        BerlinProfile,
        BerlinProfile.Product.values().toList(),
        CompositeProvider {
            useAll(BvgHci)
            use<NetworkMapsService>(BerlinFlow)
        }
    ),

    HAMBURG(
        HamburgProfile,
        HamburgProfile.Product.values().toList(),
        CompositeProvider {
            useAll(HvvHci)
            use<NetworkMapsService>(HamburgFlow)
        }
    ),

    ZURICH(
        ZurichProfile,
        ZurichProfile.Product.values().toList(),
        CompositeProvider {
            useAll(ZvvHci)
            use<NetworkMapsService>(HamburgFlow)
        }
    ),

    VIENNA(
        ViennaProfile,
        ViennaProfile.Product.values().toList(),
        CompositeProvider {
            useAll(WienerLinienEfa)
            use<NetworkMapsService>(ViennaFlow)
        }
    ),

    RHEINRUHR(
        RheinRuhrProfile,
        RheinRuhrProfile.Product.values().toList(),
        CompositeProvider {
            useAll(VrrEfa)
            use<NetworkMapsService>(RheinRuhrFlow)
        }
    );
}