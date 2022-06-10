package de.julianostarek.flow.ui.transition.scene

import android.content.Context
import android.view.animation.DecelerateInterpolator
import androidx.transition.Fade
import androidx.transition.TransitionSet
import com.google.android.material.progressindicator.LinearProgressIndicator
import de.julianostarek.flow.ui.common.view.LoadStateIndicator

class SceneFragmentEnterDownTransition(context: Context) : TransitionSet() {

    init {
        val fade = Fade()
        addTransition(fade)

        interpolator = DecelerateInterpolator()

        excludeTarget(LinearProgressIndicator::class.java, true)
        excludeChildren(LoadStateIndicator::class.java, true)
    }

}