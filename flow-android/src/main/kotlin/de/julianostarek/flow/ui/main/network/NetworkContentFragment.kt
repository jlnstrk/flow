package de.julianostarek.flow.ui.main.network

import de.julianostarek.flow.ui.common.backdrop.ContentLayerFragment
import de.julianostarek.flow.ui.common.backdrop.ContentLayerSceneFragment
import de.julianostarek.flow.ui.main.network.list.NetworkMapsSceneFragment
import kotlin.reflect.KClass

class NetworkContentFragment : ContentLayerFragment<NetworkContentFragment.Scene>() {
    override val initialScene: Scene = Scene.LIST

    enum class Scene(
        override val mode: Mode,
        override val type: KClass<out ContentLayerSceneFragment>
    ) : ContentLayerFragment.Scene {
        LIST(Mode.ANCHORED, NetworkMapsSceneFragment::class)
    }

}