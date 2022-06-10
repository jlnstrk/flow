package de.julianostarek.flow.ui.transition.scene

import android.content.Context
import android.view.animation.AccelerateInterpolator
import androidx.transition.Fade
import androidx.transition.TransitionSet

class SceneFragmentExitDownTransition(context: Context) : TransitionSet() {

    init {
        val fade = Fade()
        addTransition(fade)

        interpolator = AccelerateInterpolator()
    }

}