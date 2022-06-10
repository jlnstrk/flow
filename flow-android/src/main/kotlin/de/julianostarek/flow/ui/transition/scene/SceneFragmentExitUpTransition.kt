package de.julianostarek.flow.ui.transition.scene

import android.content.Context
import android.view.animation.AccelerateInterpolator
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.Fade
import androidx.transition.TransitionSet

class SceneFragmentExitUpTransition(context: Context) : TransitionSet() {

    init {
        val fade = Fade()

        //addTransition(fade)

        excludeTarget(RecyclerView::class.java, true)
        excludeChildren(RecyclerView::class.java, true)

        interpolator = AccelerateInterpolator()
    }

}