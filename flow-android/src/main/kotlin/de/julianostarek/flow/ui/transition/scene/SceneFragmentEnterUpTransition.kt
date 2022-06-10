package de.julianostarek.flow.ui.transition.scene

import android.content.Context
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.transition.Fade
import androidx.transition.TransitionSet
import de.julianostarek.flow.ui.common.view.LoadStateIndicator

class SceneFragmentEnterUpTransition(context: Context) : TransitionSet() {

    init {
        val fade = Fade()
        addTransition(fade)

        excludeChildren(LoadStateIndicator::class.java, true)

        interpolator = AccelerateDecelerateInterpolator()
    }

}