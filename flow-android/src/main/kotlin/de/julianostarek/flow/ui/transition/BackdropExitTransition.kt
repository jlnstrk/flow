package de.julianostarek.flow.ui.transition

import android.content.Context
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.transition.Fade
import androidx.transition.Transition

abstract class BackdropExitTransition(context: Context) : Fade(MODE_OUT) {

    init {
        duration = 150
        interpolator = FastOutSlowInInterpolator()
        addFadeTargets(context, this)
    }

    abstract fun addFadeTargets(context: Context, transition: Transition)

}