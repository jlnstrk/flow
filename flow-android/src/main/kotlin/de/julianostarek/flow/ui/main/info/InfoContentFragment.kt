package de.julianostarek.flow.ui.main.info

import de.julianostarek.flow.ui.common.backdrop.ContentLayerFragment
import de.julianostarek.flow.ui.common.backdrop.ContentLayerSceneFragment
import de.julianostarek.flow.ui.main.info.linemessages.MessagesSceneFragment
import de.julianostarek.flow.ui.main.info.linegroups.LinesSceneFragment
import de.julianostarek.flow.ui.main.info.messagedetail.MessageDetailSceneFragment
import kotlin.reflect.KClass

class InfoContentFragment : ContentLayerFragment<InfoContentFragment.Scene>() {
    override val initialScene: Scene = Scene.LINES

    enum class Scene(
        override val mode: Mode,
        override val type: KClass<out ContentLayerSceneFragment>
    ) : ContentLayerFragment.Scene {
        LINES(Mode.ANCHORED, LinesSceneFragment::class),
        MESSAGES(Mode.EXPANDED, MessagesSceneFragment::class),
        MESSAGE_DETAIL(Mode.IMMERSED, MessageDetailSceneFragment::class)
    }
}