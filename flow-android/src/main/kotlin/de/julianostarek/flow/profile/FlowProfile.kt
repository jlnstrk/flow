package de.julianostarek.flow.profile

import de.julianostarek.flow.profile.ViennaSequence
import de.julianostarek.flow.profile.vienna.*
import de.julianostarek.flow.profile.berlin.BerlinProfile
import de.julianostarek.flow.profile.BerlinSequence
import de.julianostarek.flow.profile.berlin.BvgHci
import de.julianostarek.flow.profile.frankfurt.FrankfurtProfile
import de.julianostarek.flow.profile.FrankfurtSequence
import de.julianostarek.flow.profile.frankfurt.RmvHci
import de.julianostarek.flow.profile.hamburg.HamburgProfile
import de.julianostarek.flow.profile.HamburgSequence
import de.julianostarek.flow.profile.hamburg.HvvHci
import de.julianostarek.flow.profile.munich.MunichProfile
import de.julianostarek.flow.profile.MunichSequence
import de.julianostarek.flow.profile.munich.MvvEfa
import de.julianostarek.flow.profile.munich.SbmHci
import de.julianostarek.flow.profile.rheinruhr.RheinRuhrProfile
import de.julianostarek.flow.profile.RheinRuhrSequence
import de.julianostarek.flow.profile.rheinruhr.VrrEfa
import de.julianostarek.flow.profile.stuttgart.StuttgartProfile
import de.julianostarek.flow.profile.StuttgartSequence
import de.julianostarek.flow.profile.stuttgart.VvsEfa
import de.julianostarek.flow.profile.zurich.*
import de.julianostarek.flow.profile.StyledProfile
import de.jlnstrk.transit.common.CompositeProvider
import de.jlnstrk.transit.common.Profile
import de.jlnstrk.transit.common.Provider
import de.jlnstrk.transit.common.extensions.use
import de.jlnstrk.transit.common.service.NetworkGeometryService
import de.jlnstrk.transit.common.service.NetworkMapsService
import de.julianostarek.flow.R
import kotlin.reflect.KClass

enum class FlowProfile(
    val profile: Profile,
    val productType: KClass<out Enum<*>>,
    val provider: Provider,
    val styles: StyledProfile = profile as StyledProfile,
    val themeRes: Int
) {
    MUNICH(MunichProfile, MunichProfile.Product::class, CompositeProvider {
        useAll(SbmHci)
        // use<StatusInformationService>(MvvEfa)
        of(MunichSequence) {
            use<NetworkMapsService>()
            use<NetworkGeometryService>()
        }
    }, themeRes = R.style.Theme_Sequence_Munich),

    MUNICH2(MunichProfile, MunichProfile.Product::class, CompositeProvider {
        useAll(MvvEfa)
        of(MunichSequence) {
            use<NetworkMapsService>()
            use<NetworkGeometryService>()
        }
    }, themeRes = R.style.Theme_Sequence_Munich),

    STUTTGART(StuttgartProfile, StuttgartProfile.Product::class, CompositeProvider {
        useAll(VvsEfa)
        use<NetworkMapsService>(StuttgartSequence)
    }, themeRes = R.style.Theme_Sequence_Stuttgart),

    FRANKFURT(FrankfurtProfile, FrankfurtProfile.Product::class, CompositeProvider {
        useAll(RmvHci)
        use<NetworkMapsService>(FrankfurtSequence)
    }, themeRes = R.style.Theme_Sequence_Frankfurt),

    BERLIN(BerlinProfile, BerlinProfile.Product::class, CompositeProvider {
        useAll(BvgHci)
        use<NetworkMapsService>(BerlinSequence)
    }, themeRes = R.style.Theme_Sequence_Berlin),

    HAMBURG(HamburgProfile, HamburgProfile.Product::class, CompositeProvider {
        useAll(HvvHci)
        use<NetworkMapsService>(HamburgSequence)
    }, themeRes = R.style.Theme_Sequence_Hamburg),

    ZURICH(ZurichProfile, ZurichProfile.Product::class, CompositeProvider {
        useAll(ZvvHci)
        use<NetworkMapsService>(HamburgSequence)
    }, themeRes = R.style.Theme_Sequence_Zurich),

    VIENNA(ViennaProfile, ViennaProfile.Product::class, CompositeProvider {
        useAll(WienerLinienEfa)
        use<NetworkMapsService>(ViennaSequence)
    }, themeRes = R.style.Theme_Sequence_Vienna),

    RHEIN_RUHR(RheinRuhrProfile, RheinRuhrProfile.Product::class, CompositeProvider {
        useAll(VrrEfa)
        use<NetworkMapsService>(RheinRuhrSequence)
    }, themeRes = R.style.Theme_Sequence_RheinRuhr)
}