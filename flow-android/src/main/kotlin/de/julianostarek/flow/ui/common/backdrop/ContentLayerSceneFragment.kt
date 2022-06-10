package de.julianostarek.flow.ui.common.backdrop

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.core.view.ScrollingView
import androidx.fragment.app.Fragment
import de.julianostarek.flow.ui.transition.scene.SceneFragmentEnterDownTransition
import de.julianostarek.flow.ui.transition.scene.SceneFragmentEnterUpTransition
import de.julianostarek.flow.ui.transition.scene.SceneFragmentExitDownTransition
import de.julianostarek.flow.ui.transition.scene.SceneFragmentExitUpTransition

abstract class ContentLayerSceneFragment : Fragment(), ContentLayerSceneCallbacks {
    protected val contentLayer: ContentLayerFragment<*>?
        get() = parentFragment as? ContentLayerFragment<*>

    abstract val nestedScrollingChild: ScrollingView

    final override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val hierarchyThemedInflater = inflater.cloneInContext(container?.context)
        return onCreateSceneView(hierarchyThemedInflater, container, savedInstanceState)
    }

    open fun onCreateSceneView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onContentLayerOffsetChanged(contentLayer!!.bottomOffset)
    }

    @CallSuper
    override fun onContentLayerOffsetChanged(offset: Int) {
        if (contentLayer?.scene?.mode != ContentLayerFragment.Mode.IMMERSED
            && (nestedScrollingChild as View).paddingBottom != offset
        ) {
            (nestedScrollingChild as View).setPadding(0, 0, 0, offset)
        }
    }

    open fun applyTransitionForContext(context: Context, exit: Boolean, up: Boolean) {
        when {
            !exit && !up -> enterTransition = SceneFragmentEnterDownTransition(context)
            !exit && up -> enterTransition = SceneFragmentEnterUpTransition(context)
            exit && !up -> exitTransition = SceneFragmentExitDownTransition(context)
            exit && up -> exitTransition = SceneFragmentExitUpTransition(context)
        }
    }

}